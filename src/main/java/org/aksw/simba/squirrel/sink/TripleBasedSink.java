package org.aksw.simba.squirrel.sink;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.apache.jena.graph.Triple;

/**
 * A sink that can handle triples.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface TripleBasedSink extends SinkBase {

    public void addTriple(CrawleableUri uri, Triple triple);

}
