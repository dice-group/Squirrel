package org.aksw.simba.squirrel.sink.tripleBased;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.SinkBase;
import org.apache.jena.graph.Triple;

/**
 * A sink that can handle triples.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 */
public interface TripleBasedSink extends SinkBase {

    /**
     * Add a triple for the given uri.
     *
     * @param uri    The given uri.
     * @param triple The triple to add.
     */
    void addTriple(CrawleableUri uri, Triple triple);

}
