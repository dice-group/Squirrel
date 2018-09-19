package org.aksw.simba.squirrel.analyzer.impl.html.scraper;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class HTMLScraperAnalyzer implements Analyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTMLScraperAnalyzer.class);

    private UriCollector collector;
    private HtmlScraper htmlScraper = new HtmlScraper();

    public HTMLScraperAnalyzer(UriCollector collector, HtmlScraper htmlScraper) {
        this.collector = collector;
        this.htmlScraper = htmlScraper;
    }

    public HTMLScraperAnalyzer(UriCollector collector) {
        this.collector = collector;
    }

    @Override
    public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
        try {
            List<Triple> listTriples = htmlScraper.scrape(curi.getUri().toString(), data);
            for (Triple triple : listTriples) {
                sink.addTriple(curi, triple);
                collector.addTriple(curi, triple);
            }
            return collector.getUris(curi);

        } catch (Exception e) {
            LOGGER.error("Exception while analyzing. Aborting. ", e);
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }


}
