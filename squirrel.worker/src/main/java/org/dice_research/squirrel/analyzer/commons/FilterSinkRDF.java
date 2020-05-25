package org.dice_research.squirrel.analyzer.commons;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.core.Quad;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.encoder.TripleEncoder;
import org.dice_research.squirrel.sink.Sink;

/**
 * 
 * RDF Filter to parse RDF Streams
 * 
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 *
 */

public class FilterSinkRDF extends StreamRDFBase {
    

    private CrawleableUri curi;
    private Sink sink;
    private UriCollector collector;
    private TripleEncoder encoder;

    public FilterSinkRDF(CrawleableUri curi, Sink sink, UriCollector collector,TripleEncoder encoder) {
        this.curi = curi;
        this.sink = sink;
        this.collector = collector;
        this.encoder = encoder;
    }

    @Override
    public void triple(Triple triple) {
    	Triple t = encoder.encodeTriple(triple);
        sink.addTriple(curi, t);
        collector.addTriple(curi, t);
    }

    @Override
    public void quad(Quad quad) {
    	Triple t = encoder.encodeTriple(quad.asTriple());
        sink.addTriple(curi, t);
        collector.addTriple(curi, t);
    }

}