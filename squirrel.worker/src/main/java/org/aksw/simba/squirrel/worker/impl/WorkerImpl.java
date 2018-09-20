package org.aksw.simba.squirrel.worker.impl;

import java.io.Closeable;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.analyzer.compress.impl.FileManager;
import org.aksw.simba.squirrel.analyzer.manager.SimpleOrderedAnalyzerManager;
import org.aksw.simba.squirrel.collect.SqlBasedUriCollector;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.fetcher.Fetcher;
import org.aksw.simba.squirrel.fetcher.ckan.java.SimpleCkanFetcher;
import org.aksw.simba.squirrel.fetcher.ftp.FTPFetcher;
import org.aksw.simba.squirrel.fetcher.http.HTTPFetcher;
import org.aksw.simba.squirrel.fetcher.manage.SimpleOrderedFetcherManager;
import org.aksw.simba.squirrel.fetcher.sparql.SparqlBasedFetcher;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.metadata.CrawlingActivity;
import org.aksw.simba.squirrel.metadata.CrawlingActivity.CrawlingURIState;
import org.aksw.simba.squirrel.robots.RobotsManager;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.uri.processing.UriProcessor;
import org.aksw.simba.squirrel.uri.processing.UriProcessorInterface;
import org.aksw.simba.squirrel.utils.Closer;
import org.aksw.simba.squirrel.utils.TempPathUtils;
import org.aksw.simba.squirrel.worker.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Standard implementation of the {@link Worker} interface.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class WorkerImpl implements Worker, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerImpl.class);

    /**
     * TODO what was this attribute for?
     */
    @SuppressWarnings("unused")
    @Deprecated
    private static final long DEFAULT_WAITING_TIME = 10000;
    private static final int MAX_URIS_PER_MESSAGE = 20;
    public static final boolean ENABLE_CKAN_CRAWLER_FORWARDING = false;
    /**
     * TODO what was this attribute for?
     */
    @SuppressWarnings("unused")
    @Deprecated
    private static final String CKAN_WHITELIST_FILE = "CKAN_WHITELIST_FILE";

    protected Frontier frontier;
    protected Sink sink;
    protected UriCollector collector;
    protected Analyzer analyzer;
    protected RobotsManager manager;
    @Autowired
    protected SparqlBasedFetcher sparqlBasedFetcher;
    protected Fetcher fetcher;
    protected UriProcessorInterface uriProcessor = new UriProcessor();
    protected Serializer serializer;
    protected String domainLogFile = null;
    protected long waitingTime;
    protected long timeStampLastUriFetched = 0;
    protected boolean terminateFlag;
    private final String uri = Constants.DEFAULT_WORKER_URI_PREFIX + UUID.randomUUID().toString();
    private boolean sendAliveMessages;

    /**
     * Constructor.
     *
     * @param frontier
     *            Frontier implementation used by this worker to get URI sets and
     *            send new URIs to.
     * @param sink
     *            Sink used by this worker to store crawled data.
     * @param manager
     *            RobotsManager for handling robots.txt files.
     * @param serializer
     *            Serializer for serializing and deserializing URIs.
     * @param collector
     *            The UriCollector implementation used by this worker.
     * @param waitingTime
     *            Time (in ms) the worker waits when the given frontier couldn't
     *            provide any URIs before requesting new URIs again.
     * @param logDir
     *            The directory to which a domain log will be written (or
     *            {@code null} if no log should be written).
     */
    public WorkerImpl(Frontier frontier, Sink sink, RobotsManager manager, Serializer serializer,
            UriCollector collector, long waitingTime, String logDir, boolean sendAliveMessages) {
        this.frontier = frontier;
        this.sink = sink;
        this.manager = manager;
        this.serializer = serializer;
        this.waitingTime = waitingTime;
        this.sendAliveMessages = sendAliveMessages;
        if (logDir != null) {
            domainLogFile = logDir + File.separator + "domain.log";
        }
        // Make sure that there is a collector. Otherwise, create one.
        if (collector == null) {
            LOGGER.warn("Will use a default configuration of the URI collector.");
            collector = SqlBasedUriCollector.create(serializer);
            if (collector == null) {
                throw new IllegalStateException("Couldn't create collector for storing identified URIs.");
            }
        }
        this.collector = collector;
        fetcher = new SimpleOrderedFetcherManager(
                // new SparqlBasedFetcher(),
                new HTTPFetcher(), new SimpleCkanFetcher(), new FTPFetcher());

        analyzer = new SimpleOrderedAnalyzerManager(collector);
    }

    @Override
    public void run() {
        terminateFlag = false;
        List<CrawleableUri> urisToCrawl;
        try {
            while (!terminateFlag) {
                // ask the Frontier for work
                urisToCrawl = frontier.getNextUris();
                if ((urisToCrawl == null) || (urisToCrawl.isEmpty())) {
                    // if there is no work, sleep for some time and ask again
                    try {
                        Thread.sleep(waitingTime);
                    } catch (InterruptedException e) {
                        LOGGER.debug("Interrupted while sleeping.", e);
                    }
                } else {
                    // perform work
                    crawl(urisToCrawl);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Got a severe exception. Aborting.", e);
        } finally {
            Closer.close(this, LOGGER);
        }
    }

    /* Reads all CKAN URLs for determining CKAN URLs from URIs */
    @Deprecated
    public List<String> ckanwhitelist() {

        List<String> ckanlist = Arrays.asList("https://demo.ckan.org", "http://open.canada.ca/data/en/",
                "http://datahub.io/");
        // This block can be used to feed list of CKANURLs for comparision
        // In case of using this, please create ckanwhitelist.txt in whitelist folder
        // under root
        // Also enable volumes and environment for each worker in
        // docker-compose-sparql-web.yml
        /*
         * List<String> list = new ArrayList<String>(); try { CkanWhiteListConfiguration
         * ckanwhiteListConfiguration =
         * CkanWhiteListConfiguration.getCkanWhiteListConfiguration(); if
         * (ckanwhiteListConfiguration != null) { Scanner s = new Scanner(new
         * File(ckanwhiteListConfiguration.getCkanWhiteListURI())); while (s.hasNext())
         * { list.add(s.next()); } s.close(); } }catch (FileNotFoundException e){
         * LOGGER.error("ckanwhitlelist file missing.",e); }
         */
        return ckanlist;
    }

    /* converting data from CkanCrawl to URI format <key,value> */
    public static CrawleableUri ckandata(String r) throws Exception {
        // String a = "https://demo.ckan.org";
        CrawleableUri uri = new CrawleableUri(new URI(r));
        // TODO: RECEIVED DATA FROM CKAN CRAWL SHOULD BE CONVERTED INTO URIs AND FED TO
        // FRONTIER
        uri.addData(Constants.URI_TYPE_KEY, Constants.URI_TYPE_VALUE_DUMP);
        return uri;
    }

    @Override
    public void crawl(List<CrawleableUri> uris) {
        // perform work
        for (CrawleableUri uri : uris) {
            if (uri == null) {
                LOGGER.error("Got null as CrawleableUri object. It will be ignored.");
            } else if (uri.getUri() == null) {
                LOGGER.error("Got a CrawleableUri object with getUri()=null. It will be ignored.");
            } else {
                try {
                    performCrawling(uri);
                } catch (Exception e) {
                    LOGGER.error("Unhandled exception while crawling \"" + uri.getUri().toString()
                            + "\". It will be ignored.", e);
                }
            }
        }
        // send results to the Frontier
        frontier.crawlingDone(uris);
    }

    @Override
    public void performCrawling(CrawleableUri uri) {
        CrawlingActivity activity = new CrawlingActivity(uri, getUri());
        uri.addData(Constants.URI_CRAWLING_ACTIVITY, activity);
        LOGGER.warn(uri.getUri().toString());
        // check robots.txt
        if (manager.isUriCrawlable(uri.getUri())) {
            try {
                long delay = timeStampLastUriFetched
                        - (System.currentTimeMillis() + manager.getMinWaitingTime(uri.getUri()));
                if (delay > 0) {
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                LOGGER.warn("Delay before crawling \"" + uri.getUri().toString() + "\" interrupted.", e);
            }
            LOGGER.debug("I start crawling {} now...", uri);

            File fetched = null;
            try {
                fetched = fetcher.fetch(uri);
            } catch (Exception e) {
                LOGGER.error("Exception while Fetching Data. Skipping...", e);
                activity.addStep(getClass(), "Exception while Fetching Data. " + e.getMessage());
            }
            timeStampLastUriFetched = System.currentTimeMillis();

            List<File> fetchedFiles = new ArrayList<>();
            if (fetched != null && fetched.isDirectory()) {
                fetchedFiles.addAll(TempPathUtils.searchPath4Files(fetched));
            } else {
                fetchedFiles.add(fetched);
            }

            // If there is at least one file
            if (fetchedFiles.size() > 0) {
                FileManager fm = new FileManager();
                List<File> fileList;
                try {
                    // open the sink only if a fetcher has been found
                    sink.openSinkForUri(uri);
                    collector.openSinkForUri(uri);
                    // Go over all files and analyze them
                    for (File data : fetchedFiles) {
                        if (data != null) {
                            fileList = fm.decompressFile(data);
                            for (File file : fileList) {
                                Iterator<byte[]> resultUris = analyzer.analyze(uri, file, sink);
                                sendNewUris(resultUris);
                            }
                        }
                    }
                } catch (Exception e) {
                    activity.addStep(getClass(), "Unhandled exception while Fetching Data. " + e.getMessage());
                    activity.setState(CrawlingURIState.FAILED);
                    activity.finishActivity(sink);
                    throw e;
                } finally {
                    // We don't want to handle any exception. Just make sure that sink and collector
                    // do not handle this uri anymore.
                    sink.closeSinkForUri(uri);
                    collector.closeSinkForUri(uri);
                }
                // If we reach this point, the crawling was successful
                activity.setState(CrawlingURIState.SUCCESSFUL);
            } else {
                // There are no files
                activity.addStep(getClass(), "No files for analysis available.");
                activity.setState(CrawlingURIState.FAILED);
            }
        } else {
            LOGGER.info("Crawling {} is not allowed by the RobotsManager.", uri);
            activity.addStep(manager.getClass(), "Decided to reject this URI.");
        }
        activity.finishActivity(sink);
        // LOGGER.debug("Fetched {} triples", count);
        setSpecificRecrawlTime(uri);

        // Remove the activity since we don't want to send it back to the Frontier
        uri.getData().remove(Constants.URI_CRAWLING_ACTIVITY);

        // TODO (this is only a unsatisfying quick fix to avoid unreadable graphs
        // because of too much nodes)
        // return (ret.size() > 25) ? new ArrayList<>(ret.subList(0, 25)) : ret;
    }

    private void setSpecificRecrawlTime(CrawleableUri uri) {
        // TODO: implement special cases

        // else set every time to default
        // uri.setTimestampNextCrawl(System.currentTimeMillis() +
        // FrontierImpl.getGeneralRecrawlTime());
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public boolean sendsAliveMessages() {
        return sendAliveMessages;
    }

    /**
     * Sends the given URIs to the frontier.
     * 
     * @param uriIterator
     *            an iterator used to iterate over all new URIs
     */
    public void sendNewUris(Iterator<byte[]> uriIterator) {
        List<CrawleableUri> newUris = new ArrayList<>(MAX_URIS_PER_MESSAGE);
        CrawleableUri newUri;
        int packageCount = 0;
        while (uriIterator.hasNext()) {
            try {
                newUri = serializer.deserialize(uriIterator.next());
                uriProcessor.recognizeUriType(newUri);
                newUris.add(newUri);
                if ((newUris.size() >= (packageCount + 1) * MAX_URIS_PER_MESSAGE) && uriIterator.hasNext()) {
                    frontier.addNewUris(
                            new ArrayList<>(newUris.subList(packageCount * MAX_URIS_PER_MESSAGE, newUris.size())));
                    packageCount++;
                }
            } catch (Exception e) {
                LOGGER.warn("Couldn't handle the (de-)serialization of a URI. It will be ignored.", e);
            }
        }
        frontier.addNewUris(newUris);
    }

    @Override
    public void close() {
        Closer.close(fetcher, LOGGER);
        Closer.close(sink, LOGGER);
    }

    public void setTerminateFlag(boolean terminateFlag) {
        this.terminateFlag = terminateFlag;
    }

}