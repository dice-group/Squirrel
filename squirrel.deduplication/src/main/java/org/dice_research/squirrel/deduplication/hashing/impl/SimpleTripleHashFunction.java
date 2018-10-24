package org.dice_research.squirrel.deduplication.hashing.impl;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.deduplication.hashing.TripleHashFunction;

/**
 * A simple implementation of {@link TripleHashFunction} which uses {@link Triple#hashCode()}.
 */
public class SimpleTripleHashFunction implements TripleHashFunction {

    @Override
    public int hash(Triple triple) {
        return triple.hashCode();
    }
}
