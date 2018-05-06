package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.RDFHashFunction;
import org.apache.jena.graph.Triple;

import java.util.List;


/**
 * Use Min hash algorithm to compute hash values for triples.
 */
public class MinHashFunction implements RDFHashFunction {


    @Override
    public int hash(List<Triple> triples) {

        //return DigestUtils.sha256Hex(s);
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
