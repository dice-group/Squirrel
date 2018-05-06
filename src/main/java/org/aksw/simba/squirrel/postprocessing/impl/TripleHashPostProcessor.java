package org.aksw.simba.squirrel.postprocessing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.impl.MinHashFunction;
import org.aksw.simba.squirrel.postprocessing.PostProcessor;
import org.apache.jena.graph.Triple;

import java.util.List;

public class TripleHashPostProcessor implements PostProcessor<Integer> {

    private List<Triple> triples;

    public TripleHashPostProcessor(List<Triple> triples) {
        this.triples = triples;
    }

    @Override
    public Integer postprocess() {
        return new MinHashFunction().hash(triples);
    }
}
