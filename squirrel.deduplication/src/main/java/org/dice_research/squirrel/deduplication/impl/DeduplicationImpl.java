package org.dice_research.squirrel.deduplication.impl;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.TripleComparator;
import org.dice_research.squirrel.deduplication.hashing.TripleHashFunction;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.deduplication.hashing.impl.IntervalBasedMinHashFunction;
import org.dice_research.squirrel.sink.tripleBased.AdvancedTripleBasedSink;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

public class DeduplicationImpl {

    private UriHashCustodian uriHashCustodian;

    private AdvancedTripleBasedSink sink;

    private TripleComparator tripleComparator;

    private TripleHashFunction tripleHashFunction;

    public DeduplicationImpl(UriHashCustodian uriHashCustodian ,AdvancedTripleBasedSink sink,
                             TripleComparator tripleComparator,TripleHashFunction tripleHashFunction) {
        this.sink = sink;
        this.tripleComparator = tripleComparator;
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
            Set<CrawleableUri> oldUris = uriHashCustodian.getUrisWithSameHashValues(String.valueOf(uriNew.getData(Constants.URI_HASH_KEY)));
            oldUris.remove(uriNew);
            if(!CollectionUtils.isEmpty(oldUris)) {
                sink.dropGraph(uriNew);
                CrawleableUri oldUri = oldUris.iterator().next();
                sink.updateGraphForUri(uriNew, oldUri);
            }
        }
    }

    public void handleNewUris(List<CrawleableUri> uris) {
        for (CrawleableUri nextUri : uris) {
            List<Triple> triples = sink.getTriplesForGraph(nextUri);
            HashValue value = (new IntervalBasedMinHashFunction(2, tripleHashFunction).hash(triples));
            nextUri.addData(Constants.URI_HASH_KEY, value.encodeToString());
        }
        uriHashCustodian.addHashValuesForUris(uris);
        sink.addGraphIdForURIs(uris);
        compareNewUrisWithOldUris(uris);
    }
}
