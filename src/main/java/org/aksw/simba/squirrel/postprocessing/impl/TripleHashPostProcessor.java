package org.aksw.simba.squirrel.postprocessing.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.deduplication.hashing.HashValue;
import org.aksw.simba.squirrel.deduplication.hashing.impl.IntervalBasedMinHashFunction;
import org.aksw.simba.squirrel.postprocessing.PostProcessor;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;
import org.apache.jena.graph.Triple;

import java.util.List;

public class TripleHashPostProcessor implements PostProcessor {

    private List<Triple> triples;
    private CrawleableUri uri;
    private Worker worker;


    public TripleHashPostProcessor(Worker worker, List<Triple> triples, CrawleableUri uri, HashValue hashValue) {
        this.worker = worker;
        this.triples = triples;
        this.uri = uri;
    }

    @Override
    public void postprocess() {
        HashValue value = (new IntervalBasedMinHashFunction(2).hash(triples));
        sendHashValue(value);
    }

    private void sendHashValue(HashValue value) {
        //TODO: send hash value to frontier so that he can store them in rethinkDb
        ((WorkerImpl) worker).sendHashValue(value, uri);
    }
}
