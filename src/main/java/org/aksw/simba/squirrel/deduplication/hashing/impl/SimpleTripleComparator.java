package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.TripleComparator;
import org.apache.jena.graph.Triple;

import java.util.Set;

/**
 * A simple implementation of {@link TripleComparator} which just iterates over the sets of triples and compares them one by one.
 */
public class SimpleTripleComparator implements TripleComparator {


    @Override
    public boolean triplesAreEqual(Set<Triple> tripleSet1, Set<Triple> tripleSet2) {
        for (Triple triple : tripleSet1) {
            if (!tripleSet2.contains(triple)) {
                return false;
            }
        }
        return true;
    }
}
