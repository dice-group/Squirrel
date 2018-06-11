package org.aksw.simba.squirrel.deduplication.hashing;

import org.apache.jena.graph.Triple;

import java.util.List;

/**
 * A component hat computes a {@link HashValue} for lists of {@link Triple}s. These hash values can for example be used to find
 * duplicated data.
 */
public interface TripleSetHashFunction {

    /**
     * Compute hash value for the given list of {@link Triple}s.
     *
     * @param triples The given list of {@link Triple}s.
     * @return The hash value.
     */
    HashValue hash(List<Triple> triples);
}
