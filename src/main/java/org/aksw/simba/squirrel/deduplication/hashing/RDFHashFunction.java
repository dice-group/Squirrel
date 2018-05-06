package org.aksw.simba.squirrel.deduplication.hashing;

import org.apache.jena.graph.Triple;

import java.util.List;

/**
 * A component hat computes hash values for lists of {@link Triple}s. These hash values can for example be used to find duplicated data.
 */
public interface RDFHashFunction {

    /**
     * Compute hash value for the given list of {@ink Triple}s.
     *
     * @param triples The given list of {@ink Triple}s.
     * @return The hash value.
     */
    int hash(List<Triple> triples);
}
