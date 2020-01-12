package org.dice_research.squirrel.deduplication.impl;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.TripleHashFunction;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.deduplication.hashing.impl.IntervalBasedMinHashFunction;
import org.dice_research.squirrel.deduplication.sink.DeduplicationSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * This class is a responsible to deduplicate the crawled URIs. The newly crawled URIs are checked for duplicity, the
 * graph associated with it are deleted in such case and the metadata is updated.
 *
 */
public class DeduplicationImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeduplicationImpl.class);

    private UriHashCustodian uriHashCustodian;

    private DeduplicationSink sink;

    private TripleHashFunction tripleHashFunction;

    public DeduplicationImpl(UriHashCustodian uriHashCustodian ,DeduplicationSink sink, TripleHashFunction tripleHashFunction) {
        this.sink = sink;
        this.tripleHashFunction = tripleHashFunction;
        this.uriHashCustodian = uriHashCustodian;
    }

    /**
     * Compare the hash values of the uris in  with the hash values of all uris contained
     * in {@link #uriHashCustodian}.
     * @param uris
     */
    private void compareNewUrisWithOldUris(List<CrawleableUri> uris) {
        for(CrawleableUri uriNew:uris) {
            Set<String> oldUris = uriHashCustodian.getUrisWithSameHashValues(String.valueOf(uriNew.getData(Constants.URI_HASH_KEY)));
            oldUris.remove(uriNew.getUri().toString());
            if(!CollectionUtils.isEmpty(oldUris)) {
                sink.dropGraph(uriNew);
                sink.updateGraphForUri(uriNew, sink.getGraphIdFromSparql(oldUris.iterator().next()));
            }
        }
    }

    /**
     * This method handles crawled URIs. It adds graph ids and hashes to metadata graph and calls on the compare method
     * for further processing.
     *
     * @param uris the newly crawled uris to be handled.
     */
    public void handleNewUris(List<CrawleableUri> uris) {
        sink.addGraphIdForURIs(uris);
        for (CrawleableUri nextUri : uris) {
            List<Triple> triples = sink.getTriplesForGraph(nextUri);
            HashValue value = (new IntervalBasedMinHashFunction(2, tripleHashFunction).hash(triples));
            nextUri.addData(Constants.URI_HASH_KEY, value.encodeToString());
        }
        uriHashCustodian.addHashValuesForUris(uris);
        compareNewUrisWithOldUris(uris);
    }
}
