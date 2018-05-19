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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.utils.vocabularies.DCAT;
import org.aksw.simba.squirrel.utils.vocabularies.LMCSE;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ivierle
 * Analyzer collection dataSets and metaData provided by {@link https://www.mcloud.de/web/guest/home}
 */
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
    private static final String URI_SUFFIX = "/URI";
    private static final String METADATA_URI_SUFFIX = "/URI-METADATA";
    private static final String FTP_CONSTANT = "FTP";
    private static final String DOWNLOAD_CONSTANT = "DATEIDOWNLOAD";
    public static final String dataSetUriBase = LMCSE.getURI() + "dataset"; //+ -distribution is the distribution uri

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

    public McloudAnalyzer(UriCollector collector)
    {
        this.collector = collector;
    }

    /**
     * {@inheritDoc}
     */
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

    ////
    // mCloud scraping process
    ////

    /**
     * Starting at the seed ( @param baseUri) this method uses CSS-selectors to navigate 
     * the WebSites HTML content (in @param data) to retrieve and construct all pagination URIs containing dataSets.
     * Adds all found pagination URLs to the collector.
     * @throws IOException if @param data is not found or corrupted
     * @throws URISyntaxException if construction of pagination URLs CrawleableUri fails
     */
    private void scrapePaginatonList(CrawleableUri baseUri, File data) throws IOException, URISyntaxException
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

    /**
     * Starting on one page of the pagination ( @param baseUri) this method uses CSS-selectors to navigate 
     * the WebSites HTML content (in @param data) to collect all links to detail pages.
     * Adds all found detail URLs to the collector. 
     * @throws IOException if @param data is not found or corrupted
     * @throws URISyntaxException if construction of detail URLs CrawleableUri fails
     */
    private void scrapeDetailPagesList(CrawleableUri baseUri, File data) throws IOException, URISyntaxException
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

    /**
     * On a detail page containing all desired information ( @param baseUri) this method uses CSS-selectors to navigate 
     * the WebSites HTML content (in @param data) to extract the relevant metaData. 
     * Constructs a graph with all metaData for the mCloud entry in the DCAT vocabulary. 
     * Extracts all URLs that access an actual download page for a dataSet and adds the metadataGraph to the constructed CrawleableUri.
     * @return an iterator over all extracted download URLs containing the metaData Graph and additional information
     * @throws IOException if @param data is not found or corrupted
     */
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

        //create Model
        String datasetURI = createUniqueDatasetURI(title, detailURI);
        Model datasetModel = metaInformationToDcatDataset(
            datasetURI,
            detailURI,
            title,
            description,
            providerName,
            providerURI,
            categories);

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

                    String distributionURI = datasetURI + "-" + type;

                    addDcatDistributionToDataSet(
                        datasetModel,
                        datasetURI,
                        distributionURI,
                        url,
                        type,
                        licenseName,
                        licenseURI);

                    downloadSources.add(uri);
                }
            }
            catch (URISyntaxException e2)
            {
                LOGGER.error("Error parsing URI " + url + ". It will be ignored.", e2);
            }
        }

        //add full DataSet Model to each "Distribution" URI since they are stored seperately
        for (CrawleableUri source : downloadSources)
        {
            source.addData(Constants.MCLOUD_METADATA_URI, datasetURI + METADATA_URI_SUFFIX);
            source.addData(Constants.MCLOUD_METADATA_GRAPH, datasetModel);
        }

        return downloadSources.iterator();
    }

    ////
    // Creation of metaData catalog entries using the DCAT vocabulary
    ////

    /**
     * Transforms the scraped information to a DCAT.Dataset entry
     * @param datasetURI the URI created for this dataSet ({@link McloudAnalyzer#createUniqueDatasetURI(String, String)})
     * @param mCloudSourceUri the mCloud detail URI this information was scraped from
     * @param title the dataSets title
     * @param description the dataSets description
     * @param publisherName the name of the publisher of this dataSet
     * @param publisherURI a link to the webSite of the publisher
     * @param categories all mCloud categories that this dataSet was labeled with
     * @return a Model containing the newly created DataSet
     */
    private Model metaInformationToDcatDataset(String datasetURI,
                                               String mCloudSourceUri,
                                               String title,
                                               String description,
                                               String publisherName,
                                               URI publisherURI,
                                               List<String> categories)
    {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("dcterms", DCTerms.getURI());
        model.setNsPrefix("xsd", XSD.getURI());
        model.setNsPrefix("lmcse", LMCSE.getURI());
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());
        model.setNsPrefix("dcat", DCAT.getURI());

        Resource dataSet = model.createResource(datasetURI);

        dataSet.addProperty(RDF.type, DCAT.Dataset);
        dataSet.addProperty(DCAT.landingPage, mCloudSourceUri);

        dataSet.addProperty(DCTerms.title, title);
        dataSet.addProperty(DCTerms.description, description);

        Date timeStampDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //correct format according to https://www.w3.org/TR/xmlschema-2/#dateTime
        String timeStamp = dt.format(timeStampDate).toString();
        Literal timeLiteral = model.createTypedLiteral(timeStamp, XSD.dateTime.getURI());
        dataSet.addProperty(DCTerms.issued, timeLiteral);

        if (categories.isEmpty())
        {
            dataSet.addProperty(DCAT.keyword, model.createLiteral("No category attached", "en"));
        }
        else
        {
            for (String category : categories)
            {
                dataSet.addProperty(DCAT.keyword, model.createLiteral(category, "de"));
            }
        }

        if (publisherName != null && publisherURI != null)
        {
            Resource publisher = model.createResource(publisherURI.toString() + URI_SUFFIX);
            publisher.addProperty(RDF.type, DCTerms.Agent);
            publisher.addProperty(RDFS.label, publisherName);
            publisher.addProperty(DCTerms.source, publisherURI.toString());
            dataSet.addProperty(DCTerms.publisher, publisher);
        }
        else
        {
            Resource nullPublisher = model.createResource(LMCSE.NullPublisher);
            nullPublisher.addProperty(RDFS.label, model.createLiteral("Placeholder for empty publishers", "en"));
            nullPublisher.addProperty(RDFS.comment, model.createLiteral("Placeholder to collect all datasets that have no parseable publisher attached", "en"));
            dataSet.addProperty(DCTerms.publisher, nullPublisher);
        }

        return model;
    }

    /**
     * Creates a new DCAT.Distribution for each download link and adds the distribution to the owning DCAT.Dataset
     * @param model the model containing the dataSet
     * @param datasetURI the URI identifying the DataSet
     * @param distributionURI the URI identifying the Distribution, consisting of the DataSet URI appended by the access type
     * @param accessURL the download link
     * @param accessType the type describing the protocol/API/technology to access the data
     * @param licenseName the title of the license this Distribution was released under
     * @param licenseURI link to more detailed information about the license
     */
    private void addDcatDistributionToDataSet(Model model,
                                              String datasetURI,
                                              String distributionURI,
                                              String accessURL,
                                              String accessType,
                                              String licenseName,
                                              URI licenseURI)
    {
        if (!model.containsResource(ResourceFactory.createResource(datasetURI)))
        {
            throw new IllegalArgumentException("Creating and retrieving the DCat DataSet failed: " + datasetURI);
        }

        Resource dataset = model.getResource(datasetURI);
        Resource distribution = model.createResource(distributionURI);
        distribution.addProperty(RDF.type, DCAT.Distribution);
        distribution.addProperty(DCTerms.title, accessType + " distribution of dataset " + datasetURI);

        Resource accessResource = model.createResource(LMCSE.getURI() + createURIConformString(accessType));
        accessResource.addProperty(RDFS.label, accessType);
        distribution.addProperty(LMCSE.accessType, accessResource);

        if ("Portal".equals(accessType))
        {
            distribution.addProperty(DCAT.accessURL, accessURL);
        }
        else
        {
            distribution.addProperty(DCAT.downloadURL, accessURL);
        }

        if (licenseURI != null && licenseName != null)
        {
            Resource license = model.createResource(licenseURI.toString() + URI_SUFFIX);
            license.addProperty(RDF.type, DCTerms.LicenseDocument);
            license.addProperty(RDFS.label, licenseName);
            license.addProperty(DCTerms.source, licenseURI.toString());
            distribution.addProperty(DCTerms.license, license);
        }
        else
        {
            Resource nullLicense = model.createResource(LMCSE.NullLicense);
            nullLicense.addProperty(RDFS.label, model.createLiteral("Placeholder for empty licenses", "en"));
            nullLicense.addProperty(RDFS.comment, model.createLiteral("Placeholder to collect all datasets that have no parseable license attached", "en"));
            distribution.addProperty(DCTerms.license, nullLicense);
        }

        Literal timeStamp = dataset.getProperty(DCTerms.issued).getLiteral();
        distribution.addProperty(DCTerms.issued, timeStamp);

        dataset.addProperty(DCAT.distribution, distribution);
    }

    ////
    // Helper methods
    ////

    /**
     * Filters all broken or corrupt URIs from the metaData to ensure data quality
     * @param uri the URL to connect to
     * @return true iff a connection the webSite could be established within 10 seconds and with an HTTP-StatusCode < 400 
     */
    private boolean pageIsAvailable(String uri)
    {
        try
        {
            URLConnection connection = new URL(uri).openConnection();
            connection.setConnectTimeout(10 * 1000);
            connection.connect();
            return true;
        }
        catch (IOException e)
        {
            LOGGER.error("Can not establish a connection to the given URI. The web page does not exist or is not reachable. It will be ignored: " + uri, e);
            return false;
        }
    }

    /**
     * Creates a unique URI for each DCAT dataSet as the dataSets can not be enumerated.
     * Based on {@link McloudAnalyzer#dataSetUriBase} it is appended by the dataSets title and a unique HashCode
     * @param identifier the title of the dataSet
     * @param url used to create unique hash
     * @return the URI String
     */
    private String createUniqueDatasetURI(String identifier, String url)
    {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(identifier).append(url);

        return dataSetUriBase + "-" + createURIConformString(identifier) + "-" + builder.hashCode();
    }

    /**
     * @return escaped @param string that conforms to the URISyntax to prevent URISyntaxException on URI creation
     */
    private String createURIConformString(String string)
    {
        return string.replaceAll("\\s", "-").replaceAll("[^a-zA-Z0-9/#ßüöä]", "-").replaceAll("[-]+", "-");
    }

    ////
    // Sink to store MetaData graphs to file
    ////

    protected static class MCloudDataSink
    {
        private Sink sink;

        public MCloudDataSink(Sink sink)
        {
            this.sink = sink;
        }

        /**
         * Stores the metaData graph of the given @param curi to file
         */
        public void sinkCatalogData(CrawleableUri curi)
        {
            String uri = curi.getUri().toString();
            try
            {
                if (curi.getData().containsKey(Constants.MCLOUD_METADATA_GRAPH))
                {
                    String metadataUriString = (String) curi.getData(Constants.MCLOUD_METADATA_URI);
                    Model metadataModel = (Model) curi.getData(Constants.MCLOUD_METADATA_GRAPH);

                    //create a new MetadataURI with the Suffix to store the metaData graph
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

        /**
         * Stored a dataSet ( @param data) for the given @param curi to file
         * @throws FileNotFoundException if @param data is not found or corrupted
         */
        public void sinkData(CrawleableUri curi, File data) throws FileNotFoundException
        {
            sink.addData(curi, new FileInputStream(data));
        }
    }
}
