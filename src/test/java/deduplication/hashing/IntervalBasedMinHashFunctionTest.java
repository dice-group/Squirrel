package deduplication.hashing;

import org.aksw.simba.squirrel.deduplication.hashing.impl.IntervalBasedMinHashFunction;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class IntervalBasedMinHashFunctionTest {

    private IntervalBasedMinHashFunction hashFunction;

    @Before
    public void setUp() {
        hashFunction = new IntervalBasedMinHashFunction(0);
    }

    @Test
    public void testListOrder() {

        // create two lists of triples with same triples but in different order
        // => hash values must be equal
        List<Triple> tripleList1 = generateNonBlankTriples(5000);
        List<Triple> tripleList2 = new ArrayList<>();
        for (int i = tripleList1.size() - 1; i >= 0; i--) {
            tripleList2.add(tripleList1.get(i));
        }

        Assert.assertTrue(hashFunction.hash(tripleList1).equals(hashFunction.hash(tripleList2)));
    }

    @Test
    public void testBlankNode() {

        // create two lists of triples with same content, but list 2 has one blank node
        // => blank node must be ignored, so hash values must be equal

        List<Triple> tripleList1 = generateNonBlankTriples(1);
        List<Triple> tripleList2 = new ArrayList<>(tripleList1);
        Triple tripleWithBlankNode = Triple.create(
            NodeFactory.createBlankNode("subject blank"),
            NodeFactory.createURI("predicate"),
            NodeFactory.createURI("object"));
        tripleList2.add(tripleWithBlankNode);

        Assert.assertTrue(hashFunction.hash(tripleList1).equals(hashFunction.hash(tripleList2)));
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
