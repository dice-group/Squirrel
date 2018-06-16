package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.TripleComparator;
import org.apache.jena.graph.Triple;
import sun.font.TrueTypeGlyphMapper;

import java.util.Set;

/**
 * A simple implementation of {@link TripleComparator} which just iterates over the sets of triples and compares them one by one.
 */
public class SimpleTripleComparator implements TripleComparator {


    @Override
    public boolean triplesAreEqual(Set<Triple> tripleSet1, Set<Triple> tripleSet2) {
        if (tripleSet1.size() != tripleSet2.size()) {
            return false;
        }
        for (Triple triple : tripleSet1) {
            if (containsBlankNode(triple)) {
                return containsTripleIgnoringBlankNodes(tripleSet2, triple);
            } else if (!tripleSet2.contains(triple)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the set contains the triple whereby blank nodes are ignored.
     *
     * @param tripleSet The set of triples
     * @param triple    The triple for comparison
     * @return true if the set contains the triple whereby blank nodes are ignored.
     */
    private boolean containsTripleIgnoringBlankNodes(Set<Triple> tripleSet, Triple triple) {
        boolean subjectIsBlank = triple.getSubject().isBlank();
        boolean objectIsBlank = triple.getObject().isBlank();

        for (Triple tripleInSet : tripleSet) {
            if ((!subjectIsBlank || subjectIsBlank && !tripleInSet.getSubject().isBlank())) {
                if (tripleInSet.getSubject().equals(triple.getSubject())) {
                    continue;
                }

            } else if (!tripleInSet.getPredicate().equals(triple.getPredicate())) {
                continue;

            } else if ((!objectIsBlank || objectIsBlank && !tripleInSet.getObject().isBlank())) {
                if (tripleInSet.getSubject().equals(triple.getSubject())) {
                    continue;
                }

            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a triple contains a blank node.
     *
     * @param triple
     * @return true if the triple contains a blank node.
     */
    private boolean containsBlankNode(Triple triple) {
        return triple.getSubject().isBlank() || triple.getObject().isBlank();
    }

}
