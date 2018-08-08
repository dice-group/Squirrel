package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.TripleComparator;
import org.apache.jena.graph.Triple;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple implementation of {@link TripleComparator} which just iterates over the sets of triples and compares them one by one.
 * Note: The comparator can possibly return true for two lists of triples that are in fact not equal.
 * If the two lists contain duplicates of different triples, there sizes can be equal, and they can still be equal after converting the lists to sets.
 * By converting the lists to sets, duplicates are removed and therefore precision is lost. But we chose to use sets for better performance.
 * A more precise comparison could be done in future, it can get quite complex if a good performance is desired.
 */
public class SimpleTripleComparator implements TripleComparator {


    @Override
    public boolean triplesAreEqual(List<Triple> tripleList1, List<Triple> tripleList2) {
        if (tripleList1.size() != tripleList2.size()) {
            return false;
        }

        //Use Set for faster performance
        Set<Triple> tripleSet1 = new HashSet<>(tripleList1);
        Set<Triple> tripleSet2 = new HashSet<>(tripleList2);
        if (tripleSet1.size() != tripleSet2.size()) {
            return false;
        }
        for (Triple triple : tripleSet1) {
            if (containsBlankNode(triple)) {
                return containsTripleIgnoringBlankNodes(tripleSet2, triple);
            } else if (!tripleList2.contains(triple)) {
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
