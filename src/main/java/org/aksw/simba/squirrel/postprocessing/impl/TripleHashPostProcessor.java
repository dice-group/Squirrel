package org.aksw.simba.squirrel.postprocessing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.impl.IntervalBasedMinHashFunction;
import org.aksw.simba.squirrel.postprocessing.PostProcessor;
import org.apache.jena.graph.Triple;

import java.util.List;

public class TripleHashPostProcessor implements PostProcessor {

    private List<Triple> triples;

    public TripleHashPostProcessor(List<Triple> triples) {
        this.triples = triples;
    }

    @Override
    public void postprocess() {
        new IntervalBasedMinHashFunction().hash(triples);
    }
}
