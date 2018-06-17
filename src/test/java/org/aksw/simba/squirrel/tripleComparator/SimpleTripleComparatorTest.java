package org.aksw.simba.squirrel.tripleComparator;

import org.aksw.simba.squirrel.deduplication.hashing.TripleComparator;
import org.aksw.simba.squirrel.deduplication.hashing.impl.SimpleTripleComparator;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Tests for {@link SimpleTripleComparator}.
 */
public class SimpleTripleComparatorTest {

    private TripleComparator tripleComparator = new SimpleTripleComparator();

    @Test
    public void testSetsOnlyWithBlankNodes() {
        // two lists only with blank-node-triples, but with different labels for the nodes
        // => lists must be equal
        Set<Triple> set1 = new HashSet<>();
        Set<Triple> set2 = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            set1.add(getTriple(true, true, "labelS" + i, "labelP" + i, "labelO" + i));
            set2.add(getTriple(true, true, "labelSu" + i, "labelP" + i, "labelOb" + i));
        }

        Assert.assertTrue(tripleComparator.triplesAreEqual(set1, set2));
    }

    @Test
    public void testSetsWithoutBlankNodes() {
        Set<Triple> set1 = new HashSet<>();
        Set<Triple> set2 = new HashSet<>();

        // all labels are equal
        set1.add(getTriple(false, false, "labelS", "labelP", "labelO"));
        set2.add(getTriple(false, false, "labelS", "labelP", "labelO"));
        Assert.assertTrue(tripleComparator.triplesAreEqual(set1, set2));

        // different labels for subjects
        set1.add(getTriple(false, false, "labelS1", "labelP", "labelO"));
        set2.add(getTriple(false, false, "labelS2", "labelP", "labelO"));
        Assert.assertFalse(tripleComparator.triplesAreEqual(set1, set2));
        set1.clear();
        set2.clear();

        // different labels for objects
        set1.add(getTriple(false, false, "labelS", "labelP", "labelO1"));
        set2.add(getTriple(false, false, "labelS", "labelP", "labelO2"));
        Assert.assertFalse(tripleComparator.triplesAreEqual(set1, set2));
        set1.clear();
        set2.clear();

        // different labels for predicates
        set1.add(getTriple(false, false, "labelS", "labelP1", "labelO"));
        set2.add(getTriple(false, false, "labelS", "labelP2", "labelO"));
        Assert.assertFalse(tripleComparator.triplesAreEqual(set1, set2));
        set1.clear();
        set2.clear();
    }

    @Test
    public void testSetsWithMiscellaneousNodes() {
        Set<Triple> set1 = new HashSet<>();
        Set<Triple> set2 = new HashSet<>();

        set1.add(getTriple(true, false, "s", "p", "o"));
        set1.add(getTriple(false, false, "s1", "p1", "o1"));

        set2.add(getTriple(true, false, "s2", "p", "o"));
        set2.add(getTriple(false, false, "s1", "p1", "o1"));
        Assert.assertFalse(tripleComparator.triplesAreEqual(set1, set2));
    }

    /**
     * Create and return a triple with configurable properties.
     *
     * @param subjectIsBlank Indicates whether the subject should be blank.
     * @param objectIsBlank  Indicates whether the object should be blank.
     * @param labelSubject   The label of the subject.
     * @param labelPredicate The label of the predicate.
     * @param labelObject    The label of the object.
     * @return The created triple.
     */
    private Triple getTriple(boolean subjectIsBlank, boolean objectIsBlank, String labelSubject, String labelPredicate, String labelObject) {
        return Triple.create(
            subjectIsBlank ? NodeFactory.createBlankNode(labelSubject) : NodeFactory.createURI(labelSubject),
            NodeFactory.createURI(labelPredicate),
            objectIsBlank ? NodeFactory.createBlankNode(labelObject) : NodeFactory.createURI(labelObject));
    }


}
