package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.TripleHashFunction;
import org.apache.jena.graph.Triple;

/**
 * A simple implementation of {@link TripleHashFunction} which uses {@link Triple#hashCode()}.
 */
public class SimpleTripleHashFunction implements TripleHashFunction {

    @Override
    public int hash(Triple triple) {
        return triple.hashCode();
    }
}
