package org.dice_research.squirrel.analyzer.impl.html.scraper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.tika.Tika;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    // @Override
    public boolean isElegible(CrawleableUri curi, File data) {
        String contentType = (String) curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);
        if ((contentType != null && contentType.equals("text/html"))) {
            return true;
        }
        Tika tika = new Tika();
        try (InputStream is = new FileInputStream(data)) {
            String mimeType = tika.detect(is);
            if (mimeType.equals("text/html")) {
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("An error was found when trying to analyze ", e);
        }
        return false;
    }
}
