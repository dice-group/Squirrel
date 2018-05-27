package deduplication.hashing;

import org.aksw.simba.squirrel.deduplication.hashing.HashValue;
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
        hashFunction = new IntervalBasedMinHashFunction(2);
    }

    @Test
    public void testHashing() {

        // create two lists of triples with same triples but in different order
        // => hash values must be equal
        String subject = "s";
        String predicate = "p";
        String object = "o";
        Triple t = Triple.create(
            NodeFactory.createURI(subject),
            NodeFactory.createURI(predicate),
            NodeFactory.createURI(object));

        List<Triple> tripleList1 = new ArrayList<>();
        tripleList1.add(t);
        List<Triple> tripleList2 = new ArrayList<>();

        HashValue hashValue1 = hashFunction.hash(tripleList1);
        HashValue hashValue2 = hashFunction.hash(tripleList2);

        Assert.assertTrue(hashValue1.equals(hashValue2));

    }


}
