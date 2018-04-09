package org.aksw.simba.squirrel.worker.impl;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.analyzer.impl.RDFAnalyzer;
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
import org.aksw.simba.squirrel.metadata.CrawlingActivity;
import org.aksw.simba.squirrel.robots.RobotsManager;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.uri.processing.UriProcessor;
import org.aksw.simba.squirrel.uri.processing.UriProcessorInterface;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
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

    private static final long DEFAULT_WAITING_TIME = 10000;
    private static final int MAX_URIS_PER_MESSAGE = 20;

    protected Frontier frontier;
    protected Sink sink;
    protected UriCollector collector;
    protected RobotsManager manager;
    protected SparqlBasedFetcher sparqlBasedFetcher = new SparqlBasedFetcher();
    protected Fetcher fetcher;
    protected UriProcessorInterface uriProcessor = new UriProcessor();
    protected Serializer serializer;
    protected String domainLogFile = null;
    protected long waitingTime;
    protected boolean terminateFlag;
    private final int id = (int)Math.floor(Math.random()*100000);

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
     *
     * @deprecated Because a default configuration of the UriCollector is created.
     *             Please use
     *             {@link #WorkerImpl(Frontier, Sink, RobotsManager, Serializer, String)}
     *             or
     *             {@link #WorkerImpl(Frontier, Sink, RobotsManager, Serializer, UriCollector, long, String)}
     *             instead.
     */
    @Deprecated
    public WorkerImpl(Frontier frontier, Sink sink, RobotsManager manager, Serializer serializer) {
        this(frontier, sink, manager, serializer, null, DEFAULT_WAITING_TIME, null);
    }

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
     *
     * @param logDir
     *            The directory to which a domain log will be written (or
     *            {@code null} if no log should be written).
     */
    public WorkerImpl(Frontier frontier, Sink sink, RobotsManager manager, Serializer serializer, String logDir) {
        this(frontier, sink, manager, serializer, null, DEFAULT_WAITING_TIME, logDir);
    }

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
     *
     * @deprecated Because a default configuration of the UriCollector is created.
     *             Please use
     *             {@link #WorkerImpl(Frontier, Sink, RobotsManager, Serializer, String)}
     *             or
     *             {@link #WorkerImpl(Frontier, Sink, RobotsManager, Serializer, UriCollector, long, String)}
     *             instead.
     */
    @Deprecated
    public WorkerImpl(Frontier frontier, Sink sink, RobotsManager manager, Serializer serializer,
                      UriCollector collector) {
        this(frontier, sink, manager, serializer, collector, DEFAULT_WAITING_TIME, null);
    }

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
                      UriCollector collector, long waitingTime, String logDir) {
        this.frontier = frontier;
        this.sink = sink;
        this.manager = manager;
        this.serializer = serializer;
        this.waitingTime = waitingTime;
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
        CrawlingActivity crawlingActivity = new CrawlingActivity(uris, this, sink);
        // perform work
        List<CrawleableUri> newUris = new ArrayList<CrawleableUri>();
        for (CrawleableUri uri : uris) {
            if (uri == null) {
                LOGGER.error("Got null as CrawleableUri object. It will be ignored.");
                crawlingActivity.setState(uri, CrawlingActivity.CrawlingURIState.FAILED);
            } else if (uri.getUri() == null) {
                LOGGER.error("Got a CrawleableUri object with getUri()=null. It will be ignored.");
                crawlingActivity.setState(uri, CrawlingActivity.CrawlingURIState.FAILED);
            } else {
                try {
                    performCrawling(uri, newUris);
                    crawlingActivity.setState(uri, CrawlingActivity.CrawlingURIState.SUCCESSFUL);
                } catch (Exception e) {
                    crawlingActivity.setState(uri, CrawlingActivity.CrawlingURIState.FAILED);
                    LOGGER.error("Unhandled exception whily crawling \"" + uri.getUri().toString()
                        + "\". It will be ignored.", e);
                }
            }
        }
        // classify URIs
        for (CrawleableUri uri : newUris) {
            uriProcessor.recognizeUriType(uri);
        }
        // send results to the Frontier
        crawlingActivity.finishActivity();
        if (sink instanceof SparqlBasedSink) {
            ((SparqlBasedSink) sink).addMetadata(crawlingActivity);
        } else {
            //TODO ADD METADATA IF SINK IS NOT RDFSINK
        }
        frontier.crawlingDone(uris, newUris);
    }

    @Override
    public void performCrawling(CrawleableUri uri, List<CrawleableUri> newUris) {
        // check robots.txt
        Integer count = 0;
        if (manager.isUriCrawlable(uri.getUri())) {
            LOGGER.debug("I start crawling {} now...", uri);

            Analyzer analyzer = new RDFAnalyzer(collector);

            File data = null;

            try {
                data = fetcher.fetch(uri);
            } catch (Exception e) {
                LOGGER.error("Exception while Fetching Data. Skipping...", e);
            }

            if (data != null) {
                try {
                    // open the sink only if a fetcher has been found
                    sink.openSinkForUri(uri);
                    collector.openSinkForUri(uri);
                    Iterator<byte[]> result = analyzer.analyze(uri, data, sink);
                    sink.closeSinkForUri(uri);
                    sendNewUris(result);
                    collector.closeSinkForUri(uri);
                } catch (Exception e) {
                    // We don't want to handle the exception. Just make sure that sink and collector
                    // do not handle this uri anymore.
                    sink.closeSinkForUri(uri);
                    collector.closeSinkForUri(uri);
                    throw e;
                }
            }
        } else {
            LOGGER.info("Crawling {} is not allowed by the RobotsManager.", uri);
        }
        LOGGER.debug("Fetched {} triples", count);
    }

    @Override
    public int getId() {
        return id;
    }

    public void sendNewUris(Iterator<byte[]> uriIterator) {
        List<CrawleableUri> uris = new ArrayList<CrawleableUri>(10);
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
    public void close() throws IOException {
        IOUtils.closeQuietly(fetcher);
    }

    public void setTerminateFlag(boolean terminateFlag) {
        this.terminateFlag = terminateFlag;
    }

}
