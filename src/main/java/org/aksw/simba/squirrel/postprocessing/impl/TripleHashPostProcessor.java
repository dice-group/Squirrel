package org.aksw.simba.squirrel.postprocessing.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.deduplication.hashing.HashValue;
import org.aksw.simba.squirrel.deduplication.hashing.impl.IntervalBasedMinHashFunction;
import org.aksw.simba.squirrel.postprocessing.PostProcessor;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.jena.graph.Triple;

import java.util.List;

/**
 * An implementation of {@link PostProcessor} which computes {@link HashValue}s for {@link Triple}s.
 * It hat a list {@link #triples} which it computes the hashes for and it has a reference {@link #uri} which is linked to
 * the triples.
 * It has also a reference to {@link #worker} in order to send him the computed hash values.
 */
public class TripleHashPostProcessor implements PostProcessor {

    private List<Triple> triples;
    private CrawleableUri uri;
    private Worker worker;


    /**
     * Constructor.
     *
     * @param worker  Value for {@link #worker}.
     * @param triples Value for {@link #triples}.
     * @param uri     Value for {@link #uri}.
     */
    public TripleHashPostProcessor(Worker worker, List<Triple> triples, CrawleableUri uri) {
        this.worker = worker;
        this.triples = triples;
        this.uri = uri;
    }

    @Override
    public void postprocess() {
        HashValue value = (new IntervalBasedMinHashFunction(1).hash(triples));
        worker.sendHashValue(value, uri);
    }
}
