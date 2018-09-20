package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.metadata.ActivityUtil;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDTAnalyzer implements Analyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDTAnalyzer.class);

    private UriCollector collector;

    public HDTAnalyzer(UriCollector collector) {
        this.collector = collector;
    }

    @Override
    public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
        // load the file and make sure that the HDT resource will be closed at the end
        try (HDT hdt = HDTManager.loadHDT(data.getAbsolutePath(), null)) {
            // Search pattern: Empty string means "any"
            IteratorTripleString it = hdt.search("", "", "");
            while (it.hasNext()) {
                TripleString ts = it.next();

                Node s = NodeFactory.createURI(ts.getSubject().toString());
                Node p = NodeFactory.createURI(ts.getPredicate().toString());
                Node o;

                try {
                    new URI("ts.getPredicate().toString()");
                    o = NodeFactory.createURI(ts.getPredicate().toString());
                } catch (URISyntaxException e) {
                    o = NodeFactory.createLiteral(ts.getPredicate().toString());
                }
                Triple t = new Triple(s, p, o);
                collector.addTriple(curi, t);
                sink.addTriple(curi, t);
            }
            ActivityUtil.addStep(curi, getClass());
            return collector.getUris(curi);
        } catch (IOException | org.rdfhdt.hdt.exceptions.NotFoundException e) {
            LOGGER.error("An error occured when processing the HDT file", e);
            ActivityUtil.addStep(curi, getClass(), e.getMessage());
            return null;
        }
    }

}
