package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.HashValue;
import org.aksw.simba.squirrel.deduplication.hashing.RDFHashFunction;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import java.util.ArrayList;
import java.util.List;


/**
 * Use Min hash algorithm to compute hash values for triples.
 */
public class IntervalBasedMinHashFunction implements RDFHashFunction {

    private int powerNumberOfIntervals;

    public IntervalBasedMinHashFunction(int powerNumberOfIntervals) {
        this.powerNumberOfIntervals = powerNumberOfIntervals;
    }

    @Override
    public HashValue hash(List<Triple> triples) {
        List<Integer> listHashValues = new ArrayList<>(powerNumberOfIntervals);
        for (int i = 0; i < powerNumberOfIntervals; i++) {
            listHashValues.add(i, null);
        }
        int intervalSize = 2 * Integer.MAX_VALUE / (int) Math.pow(2, powerNumberOfIntervals);
        for (Triple triple : triples) {
            Integer hash = triple.hashCode();
            System.out.println("Hash " + hash);
            int currentInterval = Integer.MIN_VALUE;
            for (int i = 0; i < Math.pow(2, powerNumberOfIntervals); i++) {
                if (hash >= currentInterval && hash < currentInterval + intervalSize) {
                    if (listHashValues.get(i) == null || listHashValues.get(i) > hash) {
                        listHashValues.add(i, hash);
                    }
                    break;
                }
                currentInterval += intervalSize;
            }
        }
        return new ListHashValue(listHashValues);
    }

    public static void main(String[] args) {
        Triple triple1 = new Triple(NodeFactory.createBlankNode("testSubject1"), NodeFactory.createBlankNode("testPredicate1"), NodeFactory.createBlankNode("testObject1"));
        Triple triple2 = new Triple(NodeFactory.createBlankNode("512335testSubject2"), NodeFactory.createBlankNode("testPredicate2"), NodeFactory.createBlankNode("testObject2"));
        Triple triple3 = new Triple(NodeFactory.createBlankNode("349rf0ejn4f90wj"), NodeFactory.createBlankNode("49f0j4efh"), NodeFactory.createBlankNode("30r9j3f9j"));
        List<Triple> list = new ArrayList<>();
        list.add(triple1);
        list.add(triple2);
        list.add(triple3);
        IntervalBasedMinHashFunction hashFunction = new IntervalBasedMinHashFunction(1);
        hashFunction.hash(list);


        /*
        int lastBits = ((Integer.MAX_VALUE >> 2) & 1);

        System.out.println(Integer.toBinaryString(Integer.MAX_VALUE));
        System.out.println((Integer.toBinaryString(Integer.MAX_VALUE >> 30)));
        System.out.println((Integer.toBinaryString(Integer.MAX_VALUE >> 29)));
        System.out.println((Integer.toBinaryString(Integer.MAX_VALUE >> 28)));
        System.out.println((Integer.toBinaryString(Integer.MAX_VALUE >> 27)));
        System.out.println((Integer.toBinaryString(Integer.MIN_VALUE >> 30)));
        System.out.println((Integer.toBinaryString(Integer.MIN_VALUE >> 29)));
        System.out.println((Integer.toBinaryString(Integer.MIN_VALUE >> 28)));
        System.out.println((Integer.toBinaryString(Integer.MIN_VALUE >> 28)));
*/
    }
}
