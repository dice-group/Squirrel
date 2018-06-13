package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.HashValue;
import org.aksw.simba.squirrel.deduplication.hashing.TripleHashFunction;
import org.aksw.simba.squirrel.deduplication.hashing.TripleSetHashFunction;
import org.apache.jena.graph.Triple;

import java.util.List;


/**
 * Use Min hash algorithm to compute hash values for triples.
 */
public class IntervalBasedMinHashFunction implements TripleSetHashFunction {

    /**
     * The number n for the 2^n intervals for the Hashing.
     */
    private int powerNumberOfIntervals;

    private TripleHashFunction tripleHashFunction;

    /**
     * Constructor.
     *
     * @param powerNumberOfIntervals The powered number of intervals for the hashing.
     */
    public IntervalBasedMinHashFunction(int powerNumberOfIntervals, TripleHashFunction tripleHashFunction) {
        this.powerNumberOfIntervals = powerNumberOfIntervals;
        this.tripleHashFunction = tripleHashFunction;
    }

    public static void main(String[] arhs) {

        // so bekommt man die letzten k bits aus der Zahl n

        int n = 1;
        int k = 5;

        int x = (k & (1 << n) - 1);
        System.out.println(x);

    }

    @Override
    public HashValue hash(List<Triple> triples) {
        Integer[] hashValues = new Integer[(int) Math.pow(2, powerNumberOfIntervals)];

        for (Triple triple : triples) {
            if (triple.getObject().isBlank() || triple.getSubject().isBlank()) {
                continue;
            }
            int hash = tripleHashFunction.hash(triple);
            int bitShiftedNumber = hash >>> (32 - powerNumberOfIntervals);

            // add leading zeros so that length is 32
//            String extendBitShiftedNumber = String.format("%032d", bitShiftedNumber);
//
//            String lastBits = extendBitShiftedNumber.substring(0, powerNumberOfIntervals);

            int lastBits = (powerNumberOfIntervals & (1 << bitShiftedNumber) - 1);

            //if we have only one interval, the zero is the desired interval
//            if (lastBits.equals("")) {
//                lastBits = "0";
//            }

//            int intervalNumber = Integer.parseInt(lastBits, 2);
//
//            if (hashValues[intervalNumber] == null || hashValues[intervalNumber] > hash) {
//                hashValues[intervalNumber] = hash;
//            }
        }
        return new ArrayHashValue(hashValues);
    }

}
