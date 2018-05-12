package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.HashValue;
import org.aksw.simba.squirrel.deduplication.hashing.RDFHashFunction;
import org.apache.jena.graph.Triple;

import java.util.ArrayList;
import java.util.List;


/**
 * Use Min hash algorithm to compute hash values for triples.
 */
public class IntervalBasedMinHashFunction implements RDFHashFunction {

    private int numberOfIntervals;

    public IntervalBasedMinHashFunction(int numberOfIntervals) {
        this.numberOfIntervals = numberOfIntervals;
    }

    @Override
    public HashValue hash(List<Triple> triples) {

        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        for (Triple triple : triples) {
            int hash = triple.hashCode();

            if ((hash >> 32 - numberOfIntervals) == 0) {
                // move to interval1
                list1.add(hash);
            } else {
                // move to interval2
                list2.add(hash);
            }
        }

        int min1 = Integer.MAX_VALUE;
        for (int i : list1) {
            if (i <= min1) {
                min1 = i;
            }
        }

        int min2 = Integer.MAX_VALUE;
        for (int i : list2) {
            if (i <= min2) {
                min2 = i;
            }
        }
        List<Integer> listHashValues = new ArrayList<>();
        listHashValues.add(min1);
        listHashValues.add(min2);
        return new ListHashValue(listHashValues);
    }

    public class IntTuple {
        int x, y;

        public IntTuple(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            return x == x && y == y;
        }
    }

}
