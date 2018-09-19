package org.aksw.simba.squirrel.analyzer.manager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.analyzer.impl.RDFAnalyzer;
import org.aksw.simba.squirrel.analyzer.impl.html.scraper.HTMLScraperAnalyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleOrderedAnalyzerManager implements Analyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleOrderedAnalyzerManager.class);

    private static final String RDF = "RDF";
    private static final String HTML = "HTML";


    private Map<String, Analyzer> analyzers;


    public SimpleOrderedAnalyzerManager(UriCollector uriCollector) {
        analyzers = new HashMap<String, Analyzer>();
        analyzers.put(RDF, new RDFAnalyzer(uriCollector));
        analyzers.put(HTML, new HTMLScraperAnalyzer(uriCollector));
    }


    @Override
    public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
        Tika tika = new Tika();

        Iterator<byte[]> iterator = null;

        InputStream is = null;
        try {
            is = new FileInputStream(data);
            String mimeType = tika.detect(is);
            String contentType = (String) curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);
            if ((contentType != null && contentType.equals("text/html")) || mimeType.equals("text/html")) {
                iterator = analyzers.get(HTML).analyze(curi, data, sink);
            } else {
                iterator = analyzers.get(RDF).analyze(curi, data, sink);
            }

        } catch (Exception e) {
            LOGGER.error("An error was found whenSimpleOrderedAnalyzerManager", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("Was not possible to close File Input Stream in SimpleOrderedAnalyzerManager", e);
                }
            }
        }
        return iterator;
    }
}
