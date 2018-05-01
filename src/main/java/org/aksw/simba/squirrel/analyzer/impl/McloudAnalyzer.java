package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.utils.vocabularies.CreativeCommons;
import org.aksw.simba.squirrel.utils.vocabularies.LMCSE;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.DCTypes;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class McloudAnalyzer implements Analyzer
{
    /**
     * If scraping is done locally i would advice to disable the actual download of mCloud resources
     * and limit the scraper to extract only the metadata from the platform.
     * Downloading the sources should only be done on a server with loads of disk space, 
     * if you want to test downloading be aware that you should terminate Squirrel at some point
     * as your disk will quickly be filled up
     */
    private static final boolean downloadDataSets = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(McloudAnalyzer.class);

    // mCloud URI related Strings and Patterns
    private static final String URI_SUFFIX = "#URI";
    private static final String METADATA_URI_SUFFIX = "#URI-METADATA";
    private static final String FTP_CONSTANT = "FTP";
    private static final String DOWNLOAD_CONSTANT = "DATEIDOWNLOAD";

    // fugly collection of mCloud HTML scraping related constants for css selector
    private final String paginationElement = "ul.pagination__list.mq-hide-m";
    private final String paginationRightWrapper = "ul li a.pagination__right.is-active";
    private final String paginationDoubleArrow = "double-arrow";
    private final String paginationFFPath = "a > svg > use";
    private final String pageLinkToDetailPage = "a.mcloud__link.tx-bold";
    private final String attrHref = "href";
    private final String attrWrappedHref = "a[href]";
    private final String attrXLinkHref = "xlink:href";
    private final String attrTitle = "title";
    private final String elHeading1 = "h1.mary-2";
    private final String elCategoryImage = "img.mcloud__content__img";
    private final String elTable = "table";
    private final String elDownloadGrid = "ul.link-list.download-list";
    private final String elDownloadSplit = "ul li a";
    private final String tagP = "p";
    private final String tagTd = "td";
    private final String tagSmall = "small";

    private UriCollector collector;
    private Set<String> dynamicUris;

    public McloudAnalyzer(UriCollector collector)
    {
        this.collector = collector;
        dynamicUris = new HashSet<>();
    }

    @Override
    public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink)
    {
        if (curi.getUri().toString().matches("^" + Constants.MCLOUD_SEED))
        {
            try
            {
                scrapePaginatonList(curi, data);
            }
            catch (IOException | URISyntaxException e)
            {
                LOGGER.error("Error retrieving pagination URIs from mCloud base. Aborting.", e);
            }
        }
        else if (curi.getData().containsKey(Constants.MCLOUD_SEARCH))
        {
            try
            {
                scrapeDetailPagesList(curi, data);
            }
            catch (IOException | URISyntaxException e)
            {
                LOGGER.error("Error retrieving detail URIs from mCloud page. Aborting.", e);
            }
        }
        else if (curi.getData().containsKey(Constants.MCLOUD_DETAIL))
        {
            MCloudDataSink mCloudSink = new MCloudDataSink(sink);
            try
            {
                Iterator<CrawleableUri> resourceIterator = scrapeDetailPage(curi, data);

                while (resourceIterator.hasNext())
                {
                    CrawleableUri crawledUri = resourceIterator.next();
                    mCloudSink.sinkCatalogData(crawledUri);

                    //because for now we are only able to process FTP and HTTP downloads, 
                    //let's filter URIs from other protocols here to save on many failed fetcher attempts
                    //this can be extended e.g. if API implementations are available
                    if (curi.getData().containsKey(Constants.FETCHABLE_PROTOCOL))
                    {
                        collector.addNewUri(curi, crawledUri);
                    }
                }
            }
            catch (IOException e)
            {
                LOGGER.error("Error processing and scraping details from mCloud detail page. Aboritng.", e);
            }
        }
        else if (downloadDataSets && data != null && curi.getData().containsKey(Constants.MCLOUD_METADATA_GRAPH))
        {
            MCloudDataSink mCloudSink = new MCloudDataSink(sink);
            try
            {
                mCloudSink.sinkData(curi, data);
            }
            catch (FileNotFoundException e)
            {
                LOGGER.error("The data file for the given URI {} could not be found and was not stored.", curi.getUri().toString(), e);
            }
        }
        else
        {
            LOGGER.info("URI is not mCloud related or does not match the expected mCloud patterns and can therefore not be processed by this analyzer. URI: {}", curi.getUri().toString());
        }

        return collector.getUris(curi);
    }

    protected void scrapePaginatonList(CrawleableUri baseUri, File data) throws IOException, URISyntaxException
    {
        LOGGER.debug("Collecting pagination from mCloud base {}", baseUri.getUri().toString());

        Document docBase = Jsoup.parse(data, Constants.DEFAULT_CHARSET.name(), baseUri.getUri().toString());

        Elements paginationArrows =
            docBase.select(paginationElement).select(paginationRightWrapper);

        //create all pagination URLs from found limit and add to URI queue
        int highestPageCount = 0;
        for (Element rightArr : paginationArrows)
        {
            String check = rightArr.select(paginationFFPath).first().attr(attrXLinkHref);

            if (check.contains(paginationDoubleArrow))
            {
                String ffLink = rightArr.attr(attrHref);

                Matcher matcher = Pattern.compile("[0-9]+").matcher(ffLink);
                while (matcher.find())
                {
                    String index = matcher.group();
                    highestPageCount = Integer.parseInt(index);
                    break;
                }
            }
        }

        for (int i = 0; i <= highestPageCount; i++)
        {
            CrawleableUri newUri = new CrawleableUri(new URI(Constants.MCLOUD_SEED + Constants.MCLOUD_SEARCH + i));
            newUri.addData(Constants.MCLOUD_SEARCH, i);
            collector.addNewUri(baseUri, newUri);
        }
    }

    protected void scrapeDetailPagesList(CrawleableUri baseUri, File data) throws IOException, URISyntaxException
    {
        LOGGER.debug("Collecting detail pages from mCloud pagination {}", baseUri.getUri().toString());

        Document pageBase = Jsoup.parse(data, Constants.DEFAULT_CHARSET.name(), baseUri.getUri().toString());

        Elements detailPages = pageBase.select(attrWrappedHref).select(pageLinkToDetailPage);

        for (Element link : detailPages)
        {
            String detailUrl = link.attr(attrHref);
            CrawleableUri newUri = new CrawleableUri(new URI(detailUrl));
            newUri.addData(Constants.MCLOUD_DETAIL, detailUrl);
            collector.addNewUri(baseUri, newUri);
        }
    }

    private Iterator<CrawleableUri> scrapeDetailPage(CrawleableUri baseUri, File data) throws IOException
    {
        LOGGER.debug("Collecting and processing metadata for {}", baseUri.getUri().toString());
        
        String detailURI = baseUri.getUri().toString();
        LOGGER.debug("Scraping data from {}", detailURI);
        Document detailPage = Jsoup.parse(data, Constants.DEFAULT_CHARSET.name(), detailURI);

        //Metadata
        String title;
        List<String> categories = new ArrayList<>();
        String description;
        String providerName;
        URI providerURI;
        URI licenseURI;
        String licenseName;
        List<CrawleableUri> downloadSources = new ArrayList<>();

        //Title
        Element titleElement = detailPage.select(elHeading1).first();
        title = titleElement.text();

        //Categories
        Elements categoryElements = detailPage.select(elCategoryImage);
        for (Element category : categoryElements)
        {
            categories.add(category.attr(attrTitle));
        }

        //Description (first <p> tag)
        Element descriptionElement = detailPage.select(tagP).first();
        description = descriptionElement.text();

        //Table
        Element table = detailPage.select(elTable).first();
        Elements tableElements = table.getElementsByTag(tagTd);

        //first row = provider
        Element firstRow = tableElements.get(0);
        providerName = firstRow.text();
        String providerUrl = firstRow.select(attrWrappedHref).first() != null ? firstRow.select(attrWrappedHref).first().attr(attrHref) : null;
        try
        {
            providerURI = new URI(providerUrl);
        }
        catch (URISyntaxException | NullPointerException e)
        {
            LOGGER.debug("Error parsing Provider. The Provider will be ignored.", e);
            providerName = null;
            providerURI = null;
        }

        //second row = license 
        Element secondRow = tableElements.get(1);
        licenseName = secondRow.text();
        String licenseUrl = secondRow.select(attrWrappedHref).first() != null ? secondRow.select(attrWrappedHref).first().attr(attrHref) : null;
        try
        {
            licenseURI = new URI(licenseUrl);
        }
        catch (URISyntaxException | NullPointerException e)
        {
            LOGGER.warn("Error parsing LICENSE URI. The License will be ignored.", e);
            licenseURI = null;
            licenseName = null;
        }

        //Sources including Download Type
        Elements downloadGrid = detailPage.select(elDownloadGrid);
        Elements downloadList = downloadGrid.select(elDownloadSplit);

        for (Element download : downloadList)
        {
            String url = download.attr(attrHref);
            String type = download.getElementsByTag(tagSmall).first().text();
            try
            {
                if (url == null || url.isEmpty())
                {
                    throw new URISyntaxException(url, "The given URL String may not be empty.");
                }

                if (pageIsAvailable(url))
                {
                    CrawleableUri uri = new CrawleableUri(new URI(url));
                    if (type != null && (FTP_CONSTANT.equals(type.toUpperCase()) || DOWNLOAD_CONSTANT.equals(type.toUpperCase())))
                    {
                        //if fetcher implementation is available, add the fetchable constant
                        uri.addData(Constants.FETCHABLE_PROTOCOL, true);
                    }
                    String metadataUri = createURIString(url, true);
                    uri.addData(Constants.MCLOUD_METADATA_URI, metadataUri);
                    Model metadataModel = metaInformationToRdf(metadataUri, url, detailURI, title, description, type, providerName, providerURI, licenseName, licenseURI, categories);
                    uri.addData(Constants.MCLOUD_METADATA_GRAPH, metadataModel);

                    downloadSources.add(uri);
                }
            }
            catch (URISyntaxException e2)
            {
                LOGGER.error("Error parsing URI " + url + ". It will be ignored.", e2);
            }
        }
        return downloadSources.iterator();
    }

    /**
     * filters all broken or corrupt URIs
     * @param uri
     * @return
     */
    private boolean pageIsAvailable(String uri)
    {
        try
        {
            URLConnection connection = new URL(uri).openConnection();
            connection.setConnectTimeout(60 * 100);
            connection.connect();
            return true;
        }
        catch (IOException e)
        {
            LOGGER.error("Can not establish a connection to the given URI. The web page does not exist or is not reachable. It will be ignored: " + uri, e);
            return false;
        }
    }

    private Model metaInformationToRdf(String metadataGraphUri,
                                       String describedUri,
                                       String mCloudSourceUri,
                                       String title,
                                       String description,
                                       String accessType,
                                       String providerName,
                                       URI providerURI,
                                       String licenseName,
                                       URI licenseURI,
                                       List<String> categories)
    {
        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix("dcterms", DCTerms.getURI());
        model.setNsPrefix("dctypes", DCTypes.getURI());
        model.setNsPrefix("cc", CreativeCommons.getURI());
        model.setNsPrefix("xsd", XSD.getURI());
        model.setNsPrefix("lmcse", LMCSE.getURI());
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());

        Date timeStampDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss"); //correct format according to https://www.w3.org/TR/xmlschema-2/#dateTime
        String timeStamp = dt.format(timeStampDate).toString();

        Resource resource = model.createResource(metadataGraphUri);
        resource.addProperty(RDF.type, DCTypes.Dataset);
        resource.addProperty(DCTerms.source, mCloudSourceUri);
        resource.addProperty(LMCSE.describes, describedUri);

        resource.addProperty(DCTerms.title, title);
        resource.addProperty(DCTerms.description, description);
        Resource accessResource = createOrGetDynamicResource(model, LMCSE.getURI(), accessType);
        accessResource.addProperty(RDFS.label, accessType);
        resource.addProperty(LMCSE.accessType, accessResource);

        Literal timeLiteral = model.createTypedLiteral(timeStamp, XSD.dateTime.getURI());
        resource.addProperty(DCTerms.created, timeLiteral);

        if (categories.isEmpty())
        {
            Resource nullCategory = model.createResource(LMCSE.NullCategory);
            nullCategory.addProperty(RDFS.label, model.createLiteral("Placeholder for empty categories", "en"));
            nullCategory.addProperty(RDFS.comment, model.createLiteral("Placeholder to collect all datasets that have no parseable category attached", "en"));
            resource.addProperty(DCTerms.subject, nullCategory);
        }
        else
        {
            for (String category : categories)
            {
                Resource categoryResource = createOrGetDynamicResource(model, LMCSE.getURI(), category);
                categoryResource.addProperty(RDFS.label, category);
                resource.addProperty(DCTerms.subject, categoryResource);
            }
        }

        if (providerName != null && providerURI != null)
        {
            Resource publisher = model.createResource(createURIString(providerURI.toString(), false));
            publisher.addProperty(RDF.type, DCTerms.Agent);
            publisher.addProperty(RDFS.label, providerName);
            publisher.addProperty(DCTerms.source, providerURI.toString());

            resource.addProperty(DCTerms.publisher, publisher);
        }
        else
        {
            Resource nullPublisher = model.createResource(LMCSE.NullPublisher);
            nullPublisher.addProperty(RDFS.label, model.createLiteral("Placeholder for empty publishers", "en"));
            nullPublisher.addProperty(RDFS.comment, model.createLiteral("Placeholder to collect all datasets that have no parseable publisher attached", "en"));
            resource.addProperty(DCTerms.publisher, nullPublisher);
        }

        if (licenseURI != null && licenseName != null)
        {
            Resource license = model.createResource(createURIString(licenseURI.toString(), false));
            license.addProperty(RDF.type, DCTerms.LicenseDocument);
            license.addProperty(RDFS.label, licenseName);
            license.addProperty(DCTerms.source, licenseURI.toString());
            resource.addProperty(CreativeCommons.license, license);
        }
        else
        {
            Resource nullLicense = model.createResource(LMCSE.NullLicense);
            nullLicense.addProperty(RDFS.label, model.createLiteral("Placeholder for empty licenses", "en"));
            nullLicense.addProperty(RDFS.comment, model.createLiteral("Placeholder to collect all datasets that have no parseable license attached", "en"));
            resource.addProperty(CreativeCommons.license, nullLicense);
        }

        return model;
    }

    private Resource createOrGetDynamicResource(Model model, String nameSpace, String propertyName)
    {
        String truncName = propertyName.replaceAll("\\s", "-").replaceAll("[^a-zA-Z0-9/#ßüöä]", "-");
        String uri = nameSpace + truncName;
        boolean isUriKnown = dynamicUris.stream().anyMatch(u -> u.equals(uri));

        if (isUriKnown)
        {
            return model.getResource(uri);
        }
        else
        {
            dynamicUris.add(uri);
            return model.createResource(uri);
        }
    }

    private String createURIString(String baseName, boolean isMetadata)
    {
        return isMetadata ? baseName + METADATA_URI_SUFFIX : baseName + URI_SUFFIX;
    }

    protected static class MCloudDataSink
    {
        private Sink sink;

        public MCloudDataSink(Sink sink)
        {
            this.sink = sink;
        }

        public void sinkCatalogData(CrawleableUri curi)
        {
            String uri = curi.getUri().toString();
            try
            {
                if (curi.getData().containsKey(Constants.MCLOUD_METADATA_GRAPH))
                {
                    String metadataUriString = (String) curi.getData(Constants.MCLOUD_METADATA_URI);
                    Model metadataModel = (Model) curi.getData(Constants.MCLOUD_METADATA_GRAPH);

                    //create a new MetadataURI with the Suffix to store the metadata graph
                    //this graph points to the real uri via LMCSE.describes
                    CrawleableUri metadataUri = new CrawleableUri(new URI(metadataUriString));
                    sink.addModel(metadataUri, metadataModel);
                }
            }
            catch (URISyntaxException e)
            {
                LOGGER.error("Error creating metadataURI for URI {}. The metadata could not be stored.", uri);
                e.printStackTrace();
            }
        }

        public void sinkData(CrawleableUri curi, File data) throws FileNotFoundException
        {
            sink.addData(curi, new FileInputStream(data));
        }
    }

}
