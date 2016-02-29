package org.aksw.simba.squirrel.worker.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.fetcher.dump.DumpFetcher;
import org.aksw.simba.squirrel.fetcher.sparql.SparqlBasedFetcher;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.robots.RobotsManager;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.collect.SimpleUriCollector;
import org.aksw.simba.squirrel.sink.collect.UriCollector;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard implementation of the {@link Worker} interface.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class WorkerImpl implements Worker {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerImpl.class);

    private static final long DEFAULT_WAITING_TIME = 10000;

    protected Frontier frontier;
    protected UriCollector sink;
    protected RobotsManager manager;
    protected long waitingTime = DEFAULT_WAITING_TIME;

    public WorkerImpl(Frontier frontier, Sink sink, RobotsManager manager, long waitingTime) {
        this.frontier = frontier;
        this.sink = new SimpleUriCollector(sink);
        this.manager = manager;
        this.waitingTime = waitingTime;
    }

    @Override
    public void run() {
        List<CrawleableUri> urisToCrawl;
        while (true) {
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
    }

    @Override
    public void crawl(List<CrawleableUri> uris) {
        // perform work
        List<CrawleableUri> newUris = new ArrayList<CrawleableUri>();
        for (CrawleableUri uri : uris) {
            performCrawling(uri, newUris);
        }
        // send results to the Frontier
        frontier.crawlingDone(uris, newUris);
    }

    @Override
    public void performCrawling(CrawleableUri uri, List<CrawleableUri> newUris) {
        // check robots.txt
        if (manager.isUriCrawlable(uri.getUri())) {
            // download/analyze URI (based on the URI type)
            LOGGER.debug("I start crawling {} now...", uri);
            if(uri.getType() == UriType.DUMP) {
                DumpFetcher dumpFetcher = new DumpFetcher();
                dumpFetcher.fetch(uri, this.sink);
            } else if (uri.getType() == UriType.SPARQL) {
                SparqlBasedFetcher sparqlBasedFetcher = new SparqlBasedFetcher();
                sparqlBasedFetcher.fetch(uri, this.sink);
                newUris.addAll(UriUtils.createCrawleableUriList(this.sink.getUris()));
            } else if (uri.getType() == UriType.DEREFERENCEABLE) {
                LOGGER.error("Uri {} has DEREFERENCEABLE Type. Not implemented. Skipping", uri);
            } else if (uri.getType() == UriType.UNKNOWN) {
                LOGGER.warn("Uri {} has UNKNOWN Type. Skipping", uri);
            } else {
                LOGGER.error("Uri {} has no type. Skipping", uri);
            }
            // TODO
            // Create fetchers for different type of URIs
            // Dereferenceable --> Jena
            // Sparql --> SPARQL client which crawl with queries --> Claus
            // library: https://github.com/AKSW/jena-sparql-api
            // No dumps downloading
            //// create a stream from the file
            //// if archived --> unarchive on fly
            //// if archive got several files --> throw away
            //// Convert to Jena RDF stream --> write to the sink

            // Create Analyzer which looks at RDF streams and push it to the
            // frontier
            // Output data to FileBasedSink
        } else {
            LOGGER.debug("Crawling {} is not allowed by the RobotsManager.", uri);
        }
    }

}
