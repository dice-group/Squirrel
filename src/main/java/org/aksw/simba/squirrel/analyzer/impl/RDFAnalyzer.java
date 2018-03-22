package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDFAnalyzer implements Analyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDFAnalyzer.class);

    private UriCollector collector;

    public RDFAnalyzer(UriCollector collector) {
        this.collector = collector;
    }

    @Override
    public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
        FileInputStream fin = null;
        try {
            // First, try to get the language of the data
            Lang lang = null;
            String contentType = (String) curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);
            if (contentType != null) {
                lang = RDFLanguages.contentTypeToLang(contentType);
            } else {
                lang = RDFLanguages.filenameToLang(data.getName(), null);
            }
            FilterSinkRDF filtered = new FilterSinkRDF(curi, sink, collector);
            RDFDataMgr.parse(filtered, data.getAbsolutePath(), lang);
        } catch (Exception e) {
            LOGGER.error("Exception while analyzing. Aborting. ", e);
        } finally {
            IOUtils.closeQuietly(fin);
        }
        return collector.getUris(curi);
    }

    protected class FilterSinkRDF extends StreamRDFBase {

        private CrawleableUri curi;
        private Sink sink;
        private UriCollector collector;

        public FilterSinkRDF(CrawleableUri curi, Sink sink, UriCollector collector) {
            this.curi = curi;
            this.sink = sink;
            this.collector = collector;
        }

        @Override
        public void triple(Triple triple) {
            sink.addTriple(curi, triple);
            collector.addTriple(curi, triple);
        }

    }

}
