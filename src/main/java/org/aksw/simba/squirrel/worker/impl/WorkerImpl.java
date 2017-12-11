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
import org.aksw.simba.squirrel.robots.RobotsManager;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Standard implementation of the {@link Worker} interface.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class WorkerImpl implements Worker, Closeable, Serializable {

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
    protected long waitingTime = DEFAULT_WAITING_TIME;
    protected boolean terminateFlag;

    private final int id = (int)Math.floor(Math.random()*100000);

    public WorkerImpl(Frontier frontier, Sink sink, RobotsManager manager, Serializer serializer, long waitingTime) {
        this(frontier, sink, manager, serializer, waitingTime, null);
    }

    public WorkerImpl(Frontier frontier, Sink sink, RobotsManager manager, Serializer serializer, long waitingTime,
                      String logDir) {
        this.frontier = frontier;
        this.sink = sink;
        this.manager = manager;
        this.serializer = serializer;
        this.waitingTime = waitingTime;
        if (logDir != null) {
            domainLogFile = logDir + File.separator + "domain.log";
        }
        collector = SqlBasedUriCollector.create(serializer);
        if (collector == null) {
            throw new IllegalStateException("Couldn't create collector for storing identified URIs.");
        }
        fetcher = new SimpleOrderedFetcherManager(
//                new SparqlBasedFetcher(),
            new HTTPFetcher(), new FTPFetcher());
    }

    @Override
    public void run() {
        terminateFlag = false;
        List<CrawleableUri> urisToCrawl;
        try {
            while (!terminateFlag) {
                // ask the Frontier for work
                LOGGER.info("worker " + id + " asks worker component");
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
        List<CrawleableUri> newUris = new ArrayList<CrawleableUri>();
        for (CrawleableUri uri : uris) {
            performCrawling(uri, newUris);
        }
        // classify URIs
        for (CrawleableUri uri : newUris) {
            uriProcessor.recognizeUriType(uri);
        }
        // send results to the Frontier
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
                LOGGER.error("Exception while Fetching Data. Skipping...");
            }

            if (data != null) {
                // open the sink only if a fetcher has been found
                sink.openSinkForUri(uri);
                Iterator<byte[]> result = analyzer.analyze(uri, data, sink);
                sink.closeSinkForUri(uri);
                sendNewUris(result);
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
