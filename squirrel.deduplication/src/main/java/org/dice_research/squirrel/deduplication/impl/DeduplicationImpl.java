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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeduplicationImpl {

    private UriHashCustodian uriHashCustodian;

    private AdvancedTripleBasedSink sink;

    private TripleComparator tripleComparator;

    private TripleHashFunction tripleHashFunction;

    public DeduplicationImpl(AdvancedTripleBasedSink sink,
                             TripleComparator tripleComparator,TripleHashFunction tripleHashFunction){
        this.sink = sink;
        this.tripleComparator = tripleComparator;
        this.tripleHashFunction = tripleHashFunction;
    }

    /**
     * Compare the hash values of the uris in  with the hash values of all uris contained
     * in {@link #uriHashCustodian}.
     * @param uris
     */
    private void compareNewUrisWithOldUris(List<CrawleableUri> uris) {
//  FIXME fix this part!
//        if (uriHashCustodian instanceof RDBKnownUriFilter) {
//            ((RDBKnownUriFilter) uriHashCustodian).openConnector();
//        }

        Set<HashValue> hashValuesOfNewUris = new HashSet<>();
        for (CrawleableUri uri : uris) {
            hashValuesOfNewUris.add((HashValue) uri.getData(Constants.URI_HASH_KEY));
        }
        Set<CrawleableUri> oldUrisForComparison = new HashSet<>();
        //TODO: Implement Sparql based solution to fetch olduriswithsamehashvalues
        for(CrawleableUri uri:uris){
            oldUrisForComparison.add(uri);
        }
        for (CrawleableUri uriNew : uris) {
            for (CrawleableUri uriOld : oldUrisForComparison) {
                if (!uriOld.equals(uriNew)) {
                    // get triples from pair1 and pair2 and compare them
                    List<Triple> listOld = sink.getTriplesForGraph(uriOld);
                    List<Triple> listNew = sink.getTriplesForGraph(uriNew);

                    if (tripleComparator.triplesAreEqual(listOld, listNew)) {
                        // TODO: delete duplicate, this means Delete the triples from the new uris and
                        // replace them by a link to the old uris which has the same content
                        sink.removeTriplesForGraph(uriNew);
                        sink.linkDuplicateUri(uriNew, uriOld);
                        break;
                    }
                }
            }
        }
    }

    public void handleNewUris(List<CrawleableUri> uris) {
        for (CrawleableUri nextUri : uris) {
            List<Triple> triples = sink.getTriplesForGraph(nextUri);
            HashValue value = (new IntervalBasedMinHashFunction(2, tripleHashFunction).hash(triples));
            nextUri.addData(Constants.URI_HASH_KEY, value);
        }
        compareNewUrisWithOldUris(uris);
    }
}
