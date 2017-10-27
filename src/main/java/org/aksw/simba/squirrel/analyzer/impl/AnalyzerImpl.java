package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.collect.UriCollector;
import org.apache.http.HttpHeaders;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyzerImpl implements Analyzer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzerImpl.class);

    private UriCollector collector;

    public AnalyzerImpl(UriCollector collector) {
        this.collector = collector;
    }

    @Override
    public Iterator<String> analyze(CrawleableUri curi, File data, Sink sink) {
        FileInputStream fin = null;
        try {
            // First, try to get the language of the data
            Lang lang = null;
            String contentType = (String) curi.getData(HttpHeaders.CONTENT_TYPE);
            if (contentType != null) {
                lang = RDFLanguages.contentTypeToLang(contentType);
            } else {
                lang = RDFLanguages.filenameToLang(data.getName(), null);
            }
            // Read the file and iterate its triples
            fin = new FileInputStream(data);
            Iterator<Triple> tripleIter = RDFDataMgr.createIteratorTriples(fin, null, "");
            Triple t;
            while (tripleIter.hasNext()) {
                t = tripleIter.next();
                sink.addTriple(curi, t);
                collector.addTriple(curi, t);
            }
        } catch (Exception e) {
            LOGGER.error("Exception while analyzing. Aborting.");
        } finally {
            IOUtils.closeQuietly(fin);
        }

        return collector.getUris();
    }

}
