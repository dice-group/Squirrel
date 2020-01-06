package org.dice_research.squirrel.sink.tripleBased;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.data.uri.CrawleableUri;

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
     * This method drops the graph of the uri
     *
     * @param uri the uri of the graph must be dropped
     */
    void dropGraph(CrawleableUri uri);

    /**
     * This method updates the graph id of the uriNew ro the graph id of uriOld
     *
     * @param uriNew the uri of which the graph id must be updated
     * @param uriOld the uri with graph id which must be used to update uriNew
     */
    void updateGraphForUri(CrawleableUri uriNew, CrawleableUri uriOld);

    /**
     * This method adds the Graph ids of the uris in the metadata
     *
     * @param uris list of uris
     */
    void addGraphIdForURIs(List<CrawleableUri> uris);
}
