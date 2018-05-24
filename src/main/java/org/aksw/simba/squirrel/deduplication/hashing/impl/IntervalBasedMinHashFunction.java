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

    /**
     * The number n for the 2^n Intervals for the Hashing.
     */
    private int powerNumberOfIntervals;

    /**
     * Constructor.
     *
     * @param powerNumberOfIntervals The powered number of Intervalls for the hashing.
     */
    public IntervalBasedMinHashFunction(int powerNumberOfIntervals) {
        this.powerNumberOfIntervals = powerNumberOfIntervals;
    }

    @Override
    public HashValue hash(List<Triple> triples) {
        List<Integer> listHashValues = new ArrayList<>(powerNumberOfIntervals);
        for (int i = 0; i < powerNumberOfIntervals; i++) {
            listHashValues.add(i, null);
        }
        long intervalSize = 2 * Integer.MAX_VALUE / (int) Math.pow(2, powerNumberOfIntervals);
        for (Triple triple : triples) {
            int hash = triple.hashCode();
            int currentInterval = Integer.MIN_VALUE;
            final double numIntervals = Math.pow(2, powerNumberOfIntervals);
            for (int i = 0; i < numIntervals; i++) {
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

}
