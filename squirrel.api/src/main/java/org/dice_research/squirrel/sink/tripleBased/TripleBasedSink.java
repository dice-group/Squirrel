package org.dice_research.squirrel.sink.tripleBased;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.SinkBase;

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
