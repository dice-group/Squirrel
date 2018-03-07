package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.utils.vocabularies.CreativeCommons;
import org.aksw.simba.squirrel.utils.vocabularies.MCSE;
import org.apache.commons.net.ntp.TimeStamp;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.DCTypes;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VCARD;
import org.apache.jena.vocabulary.XSD;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class McloudAnalyzer implements Analyzer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(McloudAnalyzer.class);

    // mCloud URI related Strings and Patterns
    private static final Pattern NUMBERS_REGEX = Pattern.compile("[0-9]+");
    
    /**
     * Defines the language to use when storing data sets depicting mCloud meta data to file with the Jena Stream API.
     * Be careful, not all Languages are support the stream serialization, {@link https://jena.apache.org/documentation/io/streaming-io.html}
     */
    public static final Lang lang = Lang.TURTLE;
    public static final String fileExt = lang.getFileExtensions().get(0);

    // fugly collection of mCloud HTML scraping related constants (html tags and elements)
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
    private final String ftpConstant = "FTP";
    private final String downloadConstant = "DATEIDOWNLOAD";

    private UriCollector collector;
    private MCloudDataSink mCloudSink;

    public McloudAnalyzer(UriCollector collector, Sink sink)
    {
        this.collector = collector;
        this.mCloudSink = new MCloudDataSink(sink);
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
            try
            {
                processDetailPage(curi, data);
            }
            catch (IOException e)
            {
                LOGGER.error("Error processing and scraping details from mCloud detail page. Aboritng.", e);
            }
        }
        else if (data != null && curi.getData().containsKey(Constants.MCLOUD_RESOURCE) && curi.getData().containsKey(Constants.MCLOUD_FETCHABLE))
        {
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

        Document docBase = Jsoup.parse(data, Constants.DEFAULT_CHARSET.name(), baseUri.getUri().toString()); // charset null falls back to "UTF-8"

        Elements paginationArrows =
            docBase.select(paginationElement).select(paginationRightWrapper);

        //create all pagination URLs from found limit and add to URI queue
        int highestPageCount = getMaxPageSize(paginationArrows);
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

    protected void processDetailPage(CrawleableUri baseUri, File data) throws IOException
    {
        LOGGER.debug("Collecting and processing metadata for {}", baseUri.getUri().toString());

        Iterator<CrawleableUri> resourceIterator = scrapeDetailPage(baseUri, data);

        //to rdf and store via sink
        while (resourceIterator.hasNext())
        {
            CrawleableUri curi = resourceIterator.next();

            //store the metadata from mCloud for each found resource (URI) in a graph
            mCloudSink.sinkMetaData(curi);

            //because for now we are only able to process FTP and HTTP downloads, we will store meta data for all download sources
            //but only add the ones for crawling which can be processed by fetchers
            //this can be extended e.g. if API implementations are available
            String accessType = (String) curi.getData(Constants.URI_DATASET_ACCESS_TYPE);
            if (accessType != null && (ftpConstant.equals(accessType.toUpperCase()) || downloadConstant.equals(accessType.toUpperCase())))
            {
                curi.addData(Constants.MCLOUD_FETCHABLE, true);
                collector.addNewUri(baseUri, curi);
            }
        }
    }

    private int getMaxPageSize(Elements pagination)
    {
        int maximum = 0;
        for (Element rightArr : pagination)
        {
            String check = rightArr.select(paginationFFPath).first().attr(attrXLinkHref);

            if (check.contains(paginationDoubleArrow))
            {
                String ffLink = rightArr.attr(attrHref);

                Matcher matcher = NUMBERS_REGEX.matcher(ffLink);
                while (matcher.find())
                {
                    String index = matcher.group();
                    maximum = Integer.parseInt(index);
                    break;
                }
            }
        }
        return maximum;
    }

    private Iterator<CrawleableUri> scrapeDetailPage(CrawleableUri baseUri, File data) throws IOException
    {
        LOGGER.debug("Scraping data from {}", baseUri.getUri().toString());
        Document detailPage = Jsoup.parse(data, Constants.DEFAULT_CHARSET.name(), baseUri.getUri().toString());

        //Metadata
        String title;
        List<String> categories = new ArrayList<>();
        String description;
        String providerName;
        URI providerURI;
        URI license;
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
        String licenseUrl = secondRow.select(attrWrappedHref).first() != null ? secondRow.select(attrWrappedHref).first().attr(attrHref) : null;
        try
        {
            license = new URI(licenseUrl);
        }
        catch (URISyntaxException | NullPointerException e)
        {
            LOGGER.warn("Error parsing LICENSE URI. The License will be ignored.", e);
            license = null;
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
                CrawleableUri uri = new CrawleableUri(new URI(url));

                //add metadata to URI
                uri.addData(Constants.URI_DATASET_TITLE, title);
                uri.addData(Constants.URI_DATASET_DESCRIPTION, description);
                uri.addData(Constants.URI_DATASET_READ_TIME, new TimeStamp(System.currentTimeMillis()));
                uri.addData(Constants.MCLOUD_RESOURCE, true);

                uri.addData(Constants.URI_DATASET_ACCESS_TYPE, type);
                uri.addData(Constants.URI_DATASET_CATEGORIES, categories);
                uri.addData(Constants.URI_DATASET_PROVIDER_NAME, providerName);
                uri.addData(Constants.URI_DATASET_PROVIDER_URI, providerURI);
                uri.addData(Constants.URI_DATASET_LICENSE, license);

                downloadSources.add(uri);
            }
            catch (URISyntaxException e2)
            {
                LOGGER.error("Error parsing URI " + url + ". It will be ignored.", e2);
            }
        }
        return downloadSources.iterator();
    }

    //----------------------- OUTPUT  ----------------------
    protected static class MCloudDataSink
    {
        private static final String URI_SUFFIX = "#URI";

        private Sink sink;

        public MCloudDataSink(Sink sink)
        {
            this.sink = sink;
        }

        public void sinkMetaData(CrawleableUri curi)
        {
            String uriString = curi.getUri().toString();
            try
            {
                URI metadata = new URI(uriString + Constants.MCLOUD_METADATA_URI_SUFFIX);
                CrawleableUri metadataUri = new CrawleableUri(metadata);
                metadataUri.setData(curi.getData());

                sink.openSinkForUri(metadataUri); //store the metadata for the URI with metadata suffix 

                Model model = metaDataToRdf(curi);
                sink.addModel(metadataUri, model);

                sink.closeSinkForUri(metadataUri);
            }
            catch (URISyntaxException e)
            {
                LOGGER.error("Error creating metadataURI for URI {}. The metadata could not be stored.", uriString);
                e.printStackTrace();
            }
        }

        public void sinkData(CrawleableUri curi, File data) throws FileNotFoundException
        {
            sink.addData(curi, new FileInputStream(data));
        }

        @SuppressWarnings("unchecked")
        private Model metaDataToRdf(CrawleableUri curi)
        {
            Model model = ModelFactory.createDefaultModel();

            model.setNsPrefix("dcterms", DCTerms.getURI());
            model.setNsPrefix("dctypes", DCTypes.getURI());
            model.setNsPrefix("cc", CreativeCommons.getURI());
            model.setNsPrefix("xsd", XSD.getURI());
            model.setNsPrefix("mcse", MCSE.getURI());
            model.setNsPrefix("rdf", RDF.getURI());
            model.setNsPrefix("rdfs", RDFS.getURI());

            String uri = curi.getUri().toString();
            Resource resource = model.createResource(uri + Constants.MCLOUD_METADATA_URI_SUFFIX);
            resource.addProperty(RDF.type, DCTypes.Dataset);
            resource.addProperty(DCTerms.source, uri);

            String title = (String) curi.getData(Constants.URI_DATASET_TITLE);
            resource.addProperty(DCTerms.title, title);

            String description = (String) curi.getData(Constants.URI_DATASET_DESCRIPTION);
            resource.addProperty(DCTerms.description, description);

            String accessType = (String) curi.getData(Constants.URI_DATASET_ACCESS_TYPE);
            resource.addProperty(MCSE.accessType, accessType);

            TimeStamp timeStamp = (TimeStamp) curi.getData(Constants.URI_DATASET_READ_TIME);
            String time = timeStamp.toDateString();
            Resource crawlTimestamp = model.createResource(XSD.dateTime);
            crawlTimestamp.addProperty(RDF.value, time);
            resource.addProperty(DCTerms.created, crawlTimestamp);

            List<String> categories = (List<String>) curi.getData(Constants.URI_DATASET_CATEGORIES);
            if (categories.isEmpty())
            {
                Resource nullCategory = model.createResource(MCSE.NullCategory);
                resource.addProperty(DCTerms.subject, nullCategory);
            }
            else
            {
                for (String category : categories)
                {
                    resource.addProperty(DCTerms.subject, category);
                }
            }

            String providerName = (String) curi.getData(Constants.URI_DATASET_PROVIDER_NAME);
            URI providerURI = (URI) curi.getData(Constants.URI_DATASET_PROVIDER_URI);
            if (providerName != null && providerURI != null)
            {
                Resource publisher = model.createResource(providerURI + URI_SUFFIX);
                publisher.addProperty(RDF.type, DCTerms.Agent);
                publisher.addProperty(VCARD.N, providerName);
                publisher.addProperty(DCTerms.source, providerURI.toString());

                resource.addProperty(DCTerms.publisher, publisher);
            }
            else
            {
                Resource nullPublisher = model.createResource(MCSE.NullPublisher);
                resource.addProperty(DCTerms.publisher, nullPublisher);
            }

            URI licenseURI = (URI) curi.getData(Constants.URI_DATASET_LICENSE);
            if (licenseURI != null)
            {
                Resource license = model.createResource(licenseURI.toString());
                license.addProperty(RDF.type, DCTerms.LicenseDocument);

                resource.addProperty(CreativeCommons.license, license);
            }
            else
            {
                Resource nullLicense = model.createResource(MCSE.NullLicense);
                resource.addProperty(CreativeCommons.license, nullLicense);
            }

            resource.addProperty(MCSE.fileBasedLink, FileBasedSink.generateFileName(uri, false, true, fileExt));

            return model;
        }

    }

}
