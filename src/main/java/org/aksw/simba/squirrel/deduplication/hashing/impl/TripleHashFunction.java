package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.apache.jena.graph.Triple;

/**
 * This interface can compute a hash for a single {@link Triple}.
 */
public interface TripleHashFunction {

    int hash(Triple triple);
}
