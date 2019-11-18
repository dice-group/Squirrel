package org.dice_research.squirrel.sink.tripleBased;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.data.uri.CrawleableUri;

import java.util.Collection;
import java.util.List;

/**
 * A specialization of {@link TripleBasedSink} which has the capability to give back all {@link Triple}s stored behind
 * a given {@link CrawleableUri}.
 */
public interface AdvancedTripleBasedSink extends TripleBasedSink {

    /**
     * Get all {@link Triple}s behind the given uri.
     *
     * @param uri The given uri.
     * @return All {@link Triple}s behind the given uri.
     */
    List<Triple> getTriplesForGraph(CrawleableUri uri);

    /**
     * Send all {@link Triple}s to the database.
     *
     * @param uri The given uri.
     * @param triples {@link Triple}s to be sent to the database.
     */
    void sendTriples(CrawleableUri uri, Collection<Triple> triples);
}
