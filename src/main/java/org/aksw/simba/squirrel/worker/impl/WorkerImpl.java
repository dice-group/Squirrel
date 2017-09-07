package org.aksw.simba.squirrel.worker.impl;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.fetcher.Fetcher;
import org.aksw.simba.squirrel.fetcher.deref.DereferencingFetcher;
import org.aksw.simba.squirrel.fetcher.dump.DumpFetcher;
import org.aksw.simba.squirrel.fetcher.sparql.SparqlBasedFetcher;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.log.DomainLogger;
import org.aksw.simba.squirrel.robots.RobotsManager;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.collect.SqlBasedUriCollector;
import org.aksw.simba.squirrel.sink.collect.UriCollector;
import org.aksw.simba.squirrel.uri.processing.UriProcessor;
import org.aksw.simba.squirrel.uri.processing.UriProcessorInterface;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected UriCollector sink;
    protected RobotsManager manager;
    protected DereferencingFetcher dereferencingFetcher = new DereferencingFetcher();
    protected SparqlBasedFetcher sparqlBasedFetcher = new SparqlBasedFetcher();
    protected DumpFetcher dumpFetcher = new DumpFetcher();
    protected UriProcessorInterface uriProcessor = new UriProcessor();
    protected String domainLogFile = null;
    protected long waitingTime = DEFAULT_WAITING_TIME;
    protected boolean terminateFlag;

    public WorkerImpl(Frontier frontier, Sink sink, RobotsManager manager, long waitingTime) {
        this(frontier, sink, manager, waitingTime, null);
    }
    
    public WorkerImpl(Frontier frontier, Sink sink, RobotsManager manager, long waitingTime, String logDir) {
        this.frontier = frontier;
        // this.sink = new SimpleUriCollector(sink);
        this.sink = SqlBasedUriCollector.create(sink);
        if (this.sink == null) {
            throw new IllegalStateException("Couldn't create database for storing identified URIs.");
        }
        this.manager = manager;
        this.waitingTime = waitingTime;
        if(logDir != null) {
            domainLogFile = logDir + File.separator + "domain.log";
        }
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
            Fetcher fetcher = null;
            if (uri.getType() == UriType.DUMP) {
                LOGGER.debug("Uri {} has DUMP Type. Processing", uri);
                fetcher = dumpFetcher;
            } else if (uri.getType() == UriType.SPARQL) {
                LOGGER.debug("Uri {} has SPARQL Type. Processing", uri);
                fetcher = sparqlBasedFetcher;
            } else if (uri.getType() == UriType.DEREFERENCEABLE) {
                LOGGER.debug("Uri {} has DEREFERENCEABLE Type. Processing", uri);
                fetcher = dereferencingFetcher;
            } else if (uri.getType() == UriType.UNKNOWN) {
                LOGGER.warn("Uri {} has UNKNOWN Type. Skipping", uri);
            } else {
                LOGGER.error("Uri {} has no type. Skipping", uri);
            }
            if (fetcher != null) {
                // open the sink only if a fetcher has been found
                sink.openSinkForUri(uri);
                count = fetcher.fetch(uri, this.sink);
                Iterator<String> iterator = this.sink.getUris();
                iterator = DomainLogger.createIfPossible(uri, domainLogFile, iterator);
                sendNewUris(iterator);
                if (iterator instanceof Closeable) {
                    IOUtils.closeQuietly((Closeable) iterator);
                }
                sink.closeSinkForUri(uri);
            }
        } else {
            LOGGER.info("Crawling {} is not allowed by the RobotsManager.", uri);
        }
        LOGGER.debug("Fetched {} triples", count);
    }

    public void sendNewUris(Iterator<String> uriIterator) {
        List<CrawleableUri> uris = new ArrayList<CrawleableUri>(20);
        CrawleableUri uri;
        while (uriIterator.hasNext()) {
            try {
                uri = new CrawleableUri(new URI(uriIterator.next()));
                uriProcessor.recognizeUriType(uri);
                uris.add(uri);
                if ((uris.size() >= MAX_URIS_PER_MESSAGE) && uriIterator.hasNext()) {
                    frontier.addNewUris(uris);
                    uris.clear();
                }
            } catch (URISyntaxException e) {
                LOGGER.warn("Got a malformed URI. It will be ignored.", e);
            }
        }
        frontier.addNewUris(uris);
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(dereferencingFetcher);
    }

    public void setTerminateFlag(boolean terminateFlag) {
        this.terminateFlag = terminateFlag;
    }

}
