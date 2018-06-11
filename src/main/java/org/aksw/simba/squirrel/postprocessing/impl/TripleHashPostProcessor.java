package org.aksw.simba.squirrel.postprocessing.impl;

import org.aksw.simba.squirrel.components.DeduplicatorComponent;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.deduplication.hashing.HashValue;
import org.aksw.simba.squirrel.deduplication.hashing.impl.IntervalBasedMinHashFunction;
import org.aksw.simba.squirrel.deduplication.hashing.impl.TripleHashFunction;
import org.aksw.simba.squirrel.postprocessing.PostProcessor;
import org.apache.jena.graph.Triple;

import java.util.List;

/**
 * An implementation of {@link PostProcessor} which computes {@link HashValue}s for {@link Triple}s.
 * It hat a list {@link #triples} which it computes the hashes for and it has a reference {@link #uri} which is linked to
 * the triples.
 * It has also a reference to {@link #deduplicatorComponent} in order to send him the computed hash values.
 */
public class TripleHashPostProcessor implements PostProcessor {

    private List<Triple> triples;
    private CrawleableUri uri;
    private DeduplicatorComponent deduplicatorComponent;
    private TripleHashFunction tripleHashFunction;


    /**
     * Constructor.
     *
     * @param deduplicatorComponent  Value for {@link #deduplicatorComponent}.
     * @param triples Value for {@link #triples}.
     * @param uri     Value for {@link #uri}.
     * @param tripleHashFunction The hash used to computes hashes for single triples.
     */
    public TripleHashPostProcessor(DeduplicatorComponent deduplicatorComponent, List<Triple> triples, CrawleableUri uri, TripleHashFunction tripleHashFunction) {
        this.deduplicatorComponent = deduplicatorComponent;
        this.triples = triples;
        this.uri = uri;
        this.tripleHashFunction = tripleHashFunction;
    }

    @Override
    public void postprocess() {
        HashValue value = (new IntervalBasedMinHashFunction(1, tripleHashFunction).hash(triples));
        uri.setHashValue(value);
        deduplicatorComponent.recognizeUriWithComputedHashValue(uri);
    }
}
