package org.aksw.simba.squirrel.deduplication.hashing;

import org.apache.jena.graph.Triple;

/**
 * This interface can compute a hash for a single {@link Triple}.
 */
public interface TripleHashFunction {

    /**
     * Calculate a hash for the given triple.
     *
     * @param triple The given triple.
     * @return A hash for the given triple.
     */
    int hash(Triple triple);
}
