package org.aksw.simba.squirrel.tripleComparator;

import org.aksw.simba.squirrel.deduplication.hashing.TripleComparator;
import org.aksw.simba.squirrel.deduplication.hashing.impl.SimpleTripleComparator;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for {@link SimpleTripleComparator}.
 */
public class SimpleTripleComparatorTest {

    private TripleComparator tripleComparator = new SimpleTripleComparator();

    @Test
    public void testListsOnlyWithBlankNodes() {
        // two lists only with blank-node-triples, but with different labels for the nodes
        // => lists must be equal
        List<Triple> list1 = new ArrayList<>();
        List<Triple> list2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list1.add(getTriple(true, true, "labelS" + i, "labelP" + i, "labelO" + i));
            list2.add(getTriple(true, true, "labelSu" + i, "labelP" + i, "labelOb" + i));
        }

        Assert.assertTrue(tripleComparator.triplesAreEqual(list1, list2));
    }

    @Test
    public void testListsWithDifferentDuplicateTriples() {
        // two lists with the same size, but with different duplicated triples
        // => lists must be equal
        List<Triple> list1 = new ArrayList<>();
        List<Triple> list2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                list1.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
                list1.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
                list2.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
            } else {
                list1.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
                list2.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
                list2.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
            }
        }

        Assert.assertTrue(tripleComparator.triplesAreEqual(list1, list2));
    }

    @Test
    public void testListsWithDuplicateTriples() {
        // two lists with the same content, but one list contains a duplicate
        // => lists must be equal
        List<Triple> list1 = new ArrayList<>();
        List<Triple> list2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                list1.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
                list1.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
                list2.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
            } else {
                list1.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
                list2.add(getTriple(false, false, "labelS" + i, "labelP" + i, "labelO" + i));
            }
        }

        Assert.assertFalse(tripleComparator.triplesAreEqual(list1, list2));
    }

    @Test
    public void testListsWithoutBlankNodes() {
        List<Triple> list1 = new ArrayList<>();
        List<Triple> list2 = new ArrayList<>();

        // all labels are equal
        list1.add(getTriple(false, false, "labelS", "labelP", "labelO"));
        list2.add(getTriple(false, false, "labelS", "labelP", "labelO"));
        Assert.assertTrue(tripleComparator.triplesAreEqual(list1, list2));

        // different labels for subjects
        list1.add(getTriple(false, false, "labelS1", "labelP", "labelO"));
        list2.add(getTriple(false, false, "labelS2", "labelP", "labelO"));
        Assert.assertFalse(tripleComparator.triplesAreEqual(list1, list2));
        list1.clear();
        list2.clear();

        // different labels for objects
        list1.add(getTriple(false, false, "labelS", "labelP", "labelO1"));
        list2.add(getTriple(false, false, "labelS", "labelP", "labelO2"));
        Assert.assertFalse(tripleComparator.triplesAreEqual(list1, list2));
        list1.clear();
        list2.clear();

        // different labels for predicates
        list1.add(getTriple(false, false, "labelS", "labelP1", "labelO"));
        list2.add(getTriple(false, false, "labelS", "labelP2", "labelO"));
        Assert.assertFalse(tripleComparator.triplesAreEqual(list1, list2));
        list1.clear();
        list2.clear();
    }

    @Test
    public void testListsWithMiscellaneousNodes() {
        List<Triple> list1 = new ArrayList<>();
        List<Triple> list2 = new ArrayList<>();

        list1.add(getTriple(true, false, "s", "p", "o"));
        list1.add(getTriple(false, false, "s1", "p1", "o1"));

        list2.add(getTriple(true, false, "s2", "p", "o"));
        list2.add(getTriple(false, false, "s1", "p1", "o1"));
        Assert.assertFalse(tripleComparator.triplesAreEqual(list1, list2));
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
