package org.aksw.simba.squirrel.deduplication.hashing;

import org.apache.jena.graph.Triple;

import java.util.List;

/**
 * A component for comparing two sets of triples and telling whether they are equal.
 * The idea is that this could be done by the {@link org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink} efficiently.
 */
public interface TripleComparator {

    /**
     * Tests whether the given sets of triples are equal.
     * Note: Triples which contain a blank node may be handled differently.
     *
     * @param tripleList1
     * @param tripleList2
     * @return True iff the given sets of triples are equal.
     */
    boolean triplesAreEqual(List<Triple> tripleList1, List<Triple> tripleList2);
}
