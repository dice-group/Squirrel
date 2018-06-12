package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.aksw.simba.squirrel.utils.vocabularies.DCAT;
import org.aksw.simba.squirrel.utils.vocabularies.LMCSE;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
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

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); //correct format according to https://www.w3.org/TR/xmlschema-2/#dateTime
    private static final DateTimeFormatter mCloudDatasetFormat = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss O yyyy");

    // mCloud URI related Strings and Patterns
    private static final String URI_SUFFIX = "/URI";
    private static final String METADATA_URI_SUFFIX = "/URI-METADATA";
    private static final String FTP_CONSTANT = "FTP";
    private static final String DOWNLOAD_CONSTANT = "DATEIDOWNLOAD";

    // collection of mCloud CSS selector constants for scraping
    private final String selectPagination = "ul.pagination > li.pagination-end > a[href]";
    private final String selectLinkToDetailPage = "div.small-24 > div.results-header ~ a[href]";
    private final String selectTitle = "div.content > h3";
    private final String selectDescription = "div.content > p";
    private final String selectDownloadEntry = "div.download-list-row";
    private final String selectDownloadUrl = "div.row > div.small-22 > a[href]";
    private final String selectDownloadType = "div.row > div.small-2 > span.filetype";
    private final String selectProvider = "div.detail-card > h5:contains(Bereitgestellt durch) + p > span.tag-date > a[href]";
    private final String selectCategories = "div.tag-theme > span.tag-theme-text";
    private final String selectLicense = "div.detail-card > h5:contains(Nutzungsbedingung) + p > a[href]";
    private final String selectDatasetDate = "div.detail-card > h5:contains(Aktualität d. Datensatzbeschreibung) + p > span.tag-date";
    private final String selectDistributionDate = "div.detail-card > h5:contains(Aktualität) + p > span.tag-date";
    private final String attrHref = "href";

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

        String baseUriString = baseUri.getUri().toString();
        Document docBase = Jsoup.parse(data, Constants.DEFAULT_CHARSET.name(), baseUriString);

        //create all pagination URLs from found limit and add to URI queue      
        String highestPage = docBase.select(selectPagination).attr(attrHref);
        Matcher matcher = Pattern.compile("[0-9]+").matcher(highestPage);
        matcher.find();
        int highestPageCount = Integer.parseInt(matcher.group());

        String searchBaseString = highestPage.substring(0, highestPage.lastIndexOf("/") + 1);

        for (int i = 0; i <= highestPageCount; i++)
        {
            CrawleableUri newUri = new CrawleableUri(new URI(searchBaseString + i));
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
        Elements detailPages = pageBase.select(selectLinkToDetailPage);

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
        String detailURI = baseUri.getUri().toString();
        LOGGER.debug("Collecting and processing metadata for {}", detailURI);

        Document detailPage = Jsoup.parse(data, Constants.DEFAULT_CHARSET.name(), detailURI);

        //metadata
        String title = "";
        List<String> categories = new ArrayList<>();
        String description = "";
        String providerName = "";
        URI providerURI = null;
        String licenseName = "";
        URI licenseURI = null;
        String datasetDate = "";
        String distributionDate = "";
        List<CrawleableUri> downloadSources = new ArrayList<>();
        String fallbackCurrentTime = LocalDateTime.now().format(dateTimeFormatter);

        //title
        Element titleElement = detailPage.selectFirst(selectTitle);
        if (titleElement != null)
        {
            title = titleElement.text();
        }
        else
        {
            LOGGER.warn("Failed to parse Dataset title from mCloud for URI {}.", detailURI);
        }

        //description
        Elements descriptionElement = detailPage.select(selectDescription);
        for (Element part : descriptionElement)
        {
            description = description.concat(part.text());
        }

        //categories
        Elements categoryElements = detailPage.select(selectCategories);
        for (Element category : categoryElements)
        {
            categories.add(category.text());
        }

        //provider
        Element providerElement = detailPage.selectFirst(selectProvider);
        if (providerElement != null)
        {
            try
            {
                providerURI = new URI(providerElement.attr(attrHref));
                providerName = providerElement.text();
            }
            catch (URISyntaxException | NullPointerException e)
            {
                LOGGER.warn("Failed to parse Dataset publisher URL from mCloud for URI {}. The publisher will be ignored.", detailURI);
            }
        }
        else
        {
            LOGGER.warn("Failed to parse Dataset publisher from mCloud for URI {}.", detailURI);
        }

        //license
        Element licenseElement = detailPage.selectFirst(selectLicense);
        if (licenseElement != null)
        {
            try
            {
                licenseURI = new URI(licenseElement.attr(attrHref));
                licenseName = licenseElement.text();
            }
            catch (URISyntaxException | NullPointerException e)
            {
                LOGGER.warn("Failed to parse Dataset license URL from mCloud for URI {}. The license will be ignored.", detailURI);
            }
        }
        else
        {
            LOGGER.warn("Failed to parse Dataset license from mCloud for URI {}.", detailURI);
        }

        //dataset timestamp
        Element datasetDateElement = detailPage.selectFirst(selectDatasetDate);
        if (datasetDateElement != null)
        {
            datasetDate = datasetDateElement.text();
        }
        else
        {
            LOGGER.warn("Failed to parse publication date for Dataset from mCloud for URI {}. The crawling timestamp will be used instead.", detailURI);
        }

        //distribution timestamp
        Element distDateElement = detailPage.selectFirst(selectDistributionDate);
        if (distDateElement != null)
        {
            distributionDate = distDateElement.text();
        }
        else
        {
            LOGGER.warn("Failed to parse publication date for Dataset from mCloud for URI {}. The crawling timestamp will be used instead.", detailURI);
        }

        //create Model with DataSet
        String datasetURI = createUniqueURI(LMCSE.DataSetUriBase, title, detailURI);
        Model datasetModel = metaInformationToDcatDataset(
            datasetURI,
            detailURI,
            title,
            description,
            providerName,
            providerURI,
            categories,
            datasetDate,
            fallbackCurrentTime);

        //sources including accessType
        Elements downloadRow = detailPage.select(selectDownloadEntry);

        for (Element download : downloadRow)
        {
            Element sourceLinkElement = download.selectFirst(selectDownloadUrl);
            Element sourceTypeElement = download.selectFirst(selectDownloadType);

            try
            {
                if (sourceLinkElement == null)
                {
                    throw new URISyntaxException("", "Failed to extract the Download link from mCloud. This Distribution will be ignored.");
                }

                String type;
                if (sourceTypeElement == null)
                {
                    LOGGER.warn("Failed to extract the links access type from mCloud.");
                    type = LMCSE.UnknownAccessType;
                }
                else
                {
                    type = sourceTypeElement.text();
                }

                String url = sourceLinkElement.attr(attrHref);

                if (pageIsAvailable(url))
                {
                    CrawleableUri uri = new CrawleableUri(new URI(url));
                    if (FTP_CONSTANT.equalsIgnoreCase(type.toUpperCase()) || DOWNLOAD_CONSTANT.equalsIgnoreCase(type.toUpperCase()))
                    {
                        //if fetcher implementation is available, add the fetchable constant
                        uri.addData(Constants.FETCHABLE_PROTOCOL, true);
                    }

                    String distributionURI = createUniqueURI(LMCSE.DistributionUriBase, title, url) + "-" + createURIConformString(type);

                    addDcatDistributionToDataSet(
                        datasetModel,
                        datasetURI,
                        distributionURI,
                        url,
                        type,
                        licenseName,
                        licenseURI,
                        distributionDate,
                        fallbackCurrentTime);

                    downloadSources.add(uri);
                }
            }
            catch (URISyntaxException e2)
            {
                LOGGER.error("Error parsing Distribution URI for Dataset " + detailURI + ". It will be ignored.", e2);
            }
        }

        //add full DataSet Model to each "Distribution" URI since they are stored separately
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
     * @param datasetURI the URI created for this dataSet ({@link McloudAnalyzer#createUniqueURI(String, String)})
     * @param mCloudSourceUri the mCloud detail URI this information was scraped from
     * @param title the dataSets title
     * @param description the dataSets description
     * @param publisherName the name of the publisher of this dataSet
     * @param publisherURI a link to the webSite of the publisher
     * @param categories all mCloud categories that this dataSet was labeled with
     * @param datasetDate the DataSet timeStamp provided by mCloud
     * @param fallbackDate the scraping time as fallback if no timeStamp is provided (already in right format)
     * @return a Model containing the newly created DataSet
     */
    private Model metaInformationToDcatDataset(String datasetURI,
                                               String mCloudSourceUri,
                                               String title,
                                               String description,
                                               String publisherName,
                                               URI publisherURI,
                                               List<String> categories,
                                               String datasetDate,
                                               String fallbackDate)
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

        Literal timeLiteral;
        try
        {
            String timeStamp = LocalDateTime.parse(datasetDate, mCloudDatasetFormat).format(dateTimeFormatter);
            timeLiteral = model.createTypedLiteral(timeStamp, XSD.dateTime.getURI());
        }
        catch (DateTimeException e)
        {
            //try parsing the date to the expected dateTime format, let this fail silently and fallback to scraping time
            timeLiteral = model.createTypedLiteral(fallbackDate, XSD.dateTime.getURI());

            //if the given timeframe is not empty just not parseable add it in a more general setting
            if (!datasetDate.isEmpty())
            {
                Literal generalTimeInfo = model.createTypedLiteral(datasetDate, DCTerms.PeriodOfTime.getURI());
                dataSet.addProperty(DCTerms.temporal, generalTimeInfo);
            }
        }
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

        if (!publisherName.isEmpty() && publisherURI != null)
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
     
     */
    /**
     * Creates a new DCAT.Distribution for each download link and adds the distribution to the owning DCAT.Dataset
     * @param model the model containing the dataSet
     * @param datasetURI the URI identifying the DataSet
     * @param distributionURI the URI identifying the Distribution, consisting of the DataSet URI appended by the access type
     * @param accessURL the download link
     * @param accessType the type describing the protocol/API/technology to access the data
     * @param licenseName the title of the license this Distribution was released under
     * @param licenseURI link to more detailed information about the license
     * @param distributionDate the Distribution timeStamp provided by mCloud
     * @param fallbackDate the scraping time as fallback if no timeStamp is provided (already in right format)
     */
    private void addDcatDistributionToDataSet(Model model,
                                              String datasetURI,
                                              String distributionURI,
                                              String accessURL,
                                              String accessType,
                                              String licenseName,
                                              URI licenseURI,
                                              String distributionDate,
                                              String fallbackDate)
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

        //format of 'Aktualität' is too irregular to even attempt to parse it, so we add it as a general temporal and use the datasets timetsamp as issued
        Literal generalTimeInfo = model.createTypedLiteral(distributionDate, DCTerms.PeriodOfTime.getURI());
        distribution.addProperty(DCTerms.temporal, generalTimeInfo);

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
     * @return true iff a connection the webSite could be established within 10 seconds and with an statusCode < 400 
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
    private String createUniqueURI(String base, String identifier, String url)
    {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(identifier).append(url);

        return base + "-" + createURIConformString(identifier) + "-" + builder.toHashCode();
    }

    /**
     * @return escaped @param string that conforms to the URISyntax to prevent URISyntaxException on URI creation
     */
    private String createURIConformString(String string)
    {
        return string.replaceAll("\\s", "-").replaceAll("[^a-zA-Z0-9/#ßüöä]", "-").replaceAll("[-]+", "-").replaceAll("-$", "");
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
            //example for post-modifying the metadatagraph 
            Model metadataGraph = (Model) curi.getData(Constants.MCLOUD_METADATA_GRAPH);
            if (metadataGraph != null)
            {
                //add filesize to distribution
                ResIterator accessIt = metadataGraph.listResourcesWithProperty(DCAT.accessURL, curi.getUri());
                while (accessIt.hasNext())
                {
                    accessIt.next().addProperty(DCAT.byteSize, String.valueOf(data.length()));
                }
                ResIterator downloadIt = metadataGraph.listResourcesWithProperty(DCAT.downloadURL, curi.getUri());
                while (downloadIt.hasNext())
                {
                    downloadIt.next().addProperty(DCAT.byteSize, String.valueOf(data.length()));
                }
                //sink metadata, this will only work properly when we can synchronize an URI DB from all workers
                sinkCatalogData(curi);
            }

            sink.addData(curi, new FileInputStream(data));
        }
    }
}
