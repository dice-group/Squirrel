package org.aksw.simba.squirrel.worker.impl;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.analyzer.compress.impl.FileManager;
import org.aksw.simba.squirrel.analyzer.manager.SimpleOrderedAnalyzerManager;
import org.aksw.simba.squirrel.collect.SqlBasedUriCollector;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.fetcher.Fetcher;
import org.aksw.simba.squirrel.fetcher.ftp.FTPFetcher;
import org.aksw.simba.squirrel.fetcher.http.HTTPFetcher;
import org.aksw.simba.squirrel.fetcher.manage.SimpleOrderedFetcherManager;
import org.aksw.simba.squirrel.fetcher.sparql.SparqlBasedFetcher;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.metadata.CrawlingActivity;
import org.aksw.simba.squirrel.robots.RobotsManager;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.uri.processing.UriProcessor;
import org.aksw.simba.squirrel.uri.processing.UriProcessorInterface;
import org.aksw.simba.squirrel.utils.TempPathUtils;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Standard implementation of the {@link Worker} interface.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class WorkerImpl implements Worker, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerImpl.class);

    private static final int MAX_URIS_PER_MESSAGE = 20;

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
    private final int id = (int)Math.floor(Math.random()*100000);
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
                new HTTPFetcher(), new FTPFetcher());

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
            IOUtils.closeQuietly(this);
        }
    }

    @Override
    public void crawl(List<CrawleableUri> uris) {
        // perform work
        List<CrawleableUri> newUris = new ArrayList<>();
        List<CrawleableUri> crawledUris = new ArrayList<>();
        for (CrawleableUri uri : uris) {
            if (uri == null) {
                LOGGER.error("Got null as CrawleableUri object. It will be ignored.");
            } else if (uri.getUri() == null) {
                LOGGER.error("Got a CrawleableUri object with getUri()=null. It will be ignored.");
            } else {
                try {
                    performCrawling(uri, newUris);
                    crawledUris.add(uri);
                    //TODO crawlingActivity.setState(uri, CrawlingActivity.CrawlingURIState.SUCCESSFUL);
                } catch (Exception e) {
                    LOGGER.error("Unhandled exception while crawling \"" + uri.getUri().toString()
                            + "\". It will be ignored.", e);
                }
            }
        }
        // classify URIs
        for (CrawleableUri uri : newUris) {
            uriProcessor.recognizeUriType(uri);
        }
        //TODO
//        crawlingActivity.finishActivity();
//        if (sink instanceof RDFSink) {
//            ((RDFSink) sink).addMetadata(crawlingActivity);
//        } else {
//            //TODO ADD METADATA IF SINK IS NOT RDFSINK
//        }
        // send results to the Frontier
        frontier.crawlingDone(crawledUris, newUris);
    }

    @Override
    public void performCrawling(CrawleableUri uri, List<CrawleableUri> newUris) {
        // check robots.txt

    	uri.addData(Constants.URI_CRAWLING_ACTIVITY_URI, uri.getUri().toString() + "_" + System.currentTimeMillis() );
    	LOGGER.warn(uri.getUri().toString());
        Integer count = 0;
        //TODO: find out the timestamp from the uri, not yet clear how to do that
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


            FileManager fm = new FileManager();

            File fetched = null;

            try {
            	fetched = fetcher.fetch(uri);
            } catch (Exception e) {
                LOGGER.error("Exception while Fetching Data. Skipping...", e);
            }

            List<File> fetchedFiles = new ArrayList<File>();
            if(fetched != null && fetched.isDirectory()) {
            	fetchedFiles.addAll(TempPathUtils.searchPath4Files(fetched));
            } else {
            	fetchedFiles.add(fetched);
            }

            timeStampLastUriFetched = System.currentTimeMillis();
            List<File> fileList = null;


            for(File data: fetchedFiles){
	            if (data != null) {
	                fileList = fm.decompressFile(data);
	                for (File file : fileList) {
	                    try {
	                        // open the sink only if a fetcher has been found
	                        sink.openSinkForUri(uri);
	                        collector.openSinkForUri(uri);
	                        Iterator<byte[]> resultUris = analyzer.analyze(uri, file, sink);
	                        sink.closeSinkForUri(uri);
	                        sendNewUris(resultUris);
	                        collector.closeSinkForUri(uri);
	                    } catch (Exception e) {
	                        // We don't want to handle the exception. Just make sure that sink and collector
	                        // do not handle this uri anymore.
	                        sink.closeSinkForUri(uri);
	                        collector.closeSinkForUri(uri);
	                        throw e;
	                    }
	                }
	            }
            }
        } else {
            LOGGER.info("Crawling {} is not allowed by the RobotsManager.", uri);
        }
        LOGGER.debug("Fetched {} triples", count);
        setSpecificRecrawlTime(uri);

    }

    private void setSpecificRecrawlTime(CrawleableUri uri) {
        //TODO: implement special cases

        //else set everytime to default
        uri.setTimestampNextCrawl(System.currentTimeMillis() + FrontierImpl.getGeneralRecrawlTime());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean sendsAliveMessages() {
        return sendAliveMessages;
    }

    public void sendNewUris(Iterator<byte[]> uriIterator) {
        List<CrawleableUri> uris = new ArrayList<>(10);
        CrawleableUri uri;
        while (uriIterator.hasNext()) {
            try {
                uri = serializer.deserialize(uriIterator.next());
                uriProcessor.recognizeUriType(uri);
                uris.add(uri);
                if ((uris.size() >= MAX_URIS_PER_MESSAGE) && uriIterator.hasNext()) {
                    frontier.addNewUris(uris);
                    uris.clear();
                }
            } catch (Exception e) {
                LOGGER.warn("Couldn't handle the (de-)serialization of a URI. It will be ignored.", e);
            }
        }
        frontier.addNewUris(uris);
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(fetcher);
        IOUtils.closeQuietly(sink);
    }

    public void setTerminateFlag(boolean terminateFlag) {
        this.terminateFlag = terminateFlag;
    }

}
