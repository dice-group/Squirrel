package org.aksw.simba.squirrel.worker.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.fetcher.deref.DereferencingFetcher;
import org.aksw.simba.squirrel.fetcher.dump.DumpFetcher;
import org.aksw.simba.squirrel.fetcher.sparql.SparqlBasedFetcher;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.robots.RobotsManager;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.collect.SimpleUriCollector;
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

    protected Frontier frontier;
    protected UriCollector sink;
    protected RobotsManager manager;
    protected DereferencingFetcher dereferencingFetcher = new DereferencingFetcher();
    protected SparqlBasedFetcher sparqlBasedFetcher = new SparqlBasedFetcher();
    protected DumpFetcher dumpFetcher = new DumpFetcher();
    protected UriProcessorInterface uriProcessor = new UriProcessor();
    protected long waitingTime = DEFAULT_WAITING_TIME;
    protected boolean terminateFlag;

    public WorkerImpl(Frontier frontier, Sink sink, RobotsManager manager, long waitingTime) {
        this.frontier = frontier;
        this.sink = new SimpleUriCollector(sink);
        this.manager = manager;
        this.waitingTime = waitingTime;
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
			sink.openSinkForUri(uri);
			LOGGER.debug("I start crawling {} now...", uri);
			if (uri.getType() == UriType.DUMP) {
				LOGGER.debug("Uri {} has DUMP Type. Processing", uri);
				count = dumpFetcher.fetch(uri, this.sink);
				newUris.addAll(UriUtils.createCrawleableUriList(this.sink.getUris()));
			} else if (uri.getType() == UriType.SPARQL) {
				LOGGER.debug("Uri {} has SPARQL Type. Processing", uri);
				count = sparqlBasedFetcher.fetch(uri, this.sink);
				newUris.addAll(UriUtils.createCrawleableUriList(this.sink.getUris()));
			} else if (uri.getType() == UriType.DEREFERENCEABLE) {
				LOGGER.debug("Uri {} has DEREFERENCEABLE Type. Processing", uri);
				count = dereferencingFetcher.fetch(uri, this.sink);
				newUris.addAll(UriUtils.createCrawleableUriList(this.sink.getUris()));
			} else if (uri.getType() == UriType.UNKNOWN) {
				LOGGER.warn("Uri {} has UNKNOWN Type. Skipping", uri);
			} else {
				LOGGER.error("Uri {} has no type. Skipping", uri);
			}
			sink.closeSinkForUri(uri);
		} else {
			LOGGER.debug("Crawling {} is not allowed by the RobotsManager.", uri);
		}
		LOGGER.debug("Fetched {} triples", count);
	}

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(dereferencingFetcher);
    }

    public void setTerminateFlag(boolean terminateFlag) {
        this.terminateFlag = terminateFlag;
    }

}
