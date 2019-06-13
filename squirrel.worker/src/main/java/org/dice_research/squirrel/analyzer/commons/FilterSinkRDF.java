package org.dice_research.squirrel.analyzer.commons;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.core.Quad;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * RDF Filter to parse RDF Streams
 * 
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 *
 */

public class FilterSinkRDF extends StreamRDFBase {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterSinkRDF.class);


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
        collector.addTriple(curi, triple);    }

    @Override
    public void quad(Quad quad) {
        sink.addTriple(curi, quad.asTriple());
        collector.addTriple(curi, quad.asTriple());
    }

}