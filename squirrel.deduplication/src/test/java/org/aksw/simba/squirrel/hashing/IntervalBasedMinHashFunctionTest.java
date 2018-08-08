package org.aksw.simba.squirrel.hashing;

import org.aksw.simba.squirrel.deduplication.hashing.impl.IntervalBasedMinHashFunction;
import org.aksw.simba.squirrel.deduplication.hashing.impl.SimpleTripleHashFunction;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for {@link IntervalBasedMinHashFunction}.
 */
public class IntervalBasedMinHashFunctionTest {

    /**
     * An array of hash functions to test different amounts of intervals.
     */
    private IntervalBasedMinHashFunction[] hashFunctions;

    @Before
    public void setUp() {
        hashFunctions = new IntervalBasedMinHashFunction[5];
        for (int i = 0; i < hashFunctions.length; i++) {
            hashFunctions[i] = new IntervalBasedMinHashFunction(i, new SimpleTripleHashFunction());
        }
    }

    @Test
    public void testListOrder() {

        // create two lists of triples with same triples but in different order
        // => hash values must be equal
        List<Triple> tripleList1 = generateNonBlankTriples(100);
        List<Triple> tripleList2 = new ArrayList<>();
        for (int i = tripleList1.size() - 1; i >= 0; i--) {
            tripleList2.add(tripleList1.get(i));
        }

        for (IntervalBasedMinHashFunction hashFunction : hashFunctions) {
            Assert.assertEquals(hashFunction.hash(tripleList1), hashFunction.hash(tripleList2));
        }
    }

    @Test
    public void testBlankNode() {

        // create two lists of triples with same content, but list 2 has one additional triple with blank node
        // => triple with blank node must be ignored, so hash values must be equal

        List<Triple> tripleList1 = generateNonBlankTriples(1);
        List<Triple> tripleList2 = new ArrayList<>(tripleList1);
        Triple tripleWithBlankNode = Triple.create(
            NodeFactory.createBlankNode("subject blank"),
            NodeFactory.createURI("predicate"),
            NodeFactory.createURI("object"));
        tripleList2.add(tripleWithBlankNode);

        for (IntervalBasedMinHashFunction hashFunction : hashFunctions) {
            Assert.assertEquals(hashFunction.hash(tripleList1), hashFunction.hash(tripleList2));
        }
    }

    /**
     * Generate some triples (without blank nodes).
     *
     * @param amount The amount of the triples to create.
     * @return The generated list of triples.
     */
    private List<Triple> generateNonBlankTriples(int amount) {
        List<Triple> triples = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Triple t = Triple.create(
                NodeFactory.createURI("subject " + i),
                NodeFactory.createURI("predicate " + i),
                NodeFactory.createURI("object " + i));
            triples.add(t);
        }
        return triples;
    }

}
