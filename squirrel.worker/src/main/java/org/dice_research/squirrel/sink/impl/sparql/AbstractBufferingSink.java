package org.dice_research.squirrel.sink.impl.sparql;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.sink.quadbased.QuadBasedSink;
import org.dice_research.squirrel.sink.triplebased.TripleBasedSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An abstract implementation for TripleBasedSinks and QuadBasedSinks
 *
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 */

public abstract class AbstractBufferingSink implements TripleBasedSink, QuadBasedSink {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBufferingSink.class);

    /**
     * Interval that specifies how many triples are to be buffered at once until
     * they are sent to the sink.
     */
    protected static final int DEFAULT_BUFFER_SIZE = 50;
    /**
     * The data structure (map) in which the triples are buffered.
     */
    private Map<CrawleableUri, TripleBuffer> tripleBuffer = Collections.synchronizedMap(new HashMap<>());
    private Map<CrawleableUri, QuadBuffer> quadBuffer = Collections.synchronizedMap(new HashMap<>());


    protected int bufferSize = DEFAULT_BUFFER_SIZE;

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        tripleBuffer.put(uri, new TripleBuffer());
        quadBuffer.put(uri, new QuadBuffer());

    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        TripleBuffer status = tripleBuffer.get(uri);
        if (status == null) {
            LOGGER.warn("Sink has not been opened for the uri, sink will be opened.");
            openSinkForUri(uri);
            status = tripleBuffer.get(uri);
        }
        status.addTriple(this, uri, triple);
    }
    
    @Override
    public void addQuad(CrawleableUri uri, Quad quad) {
        QuadBuffer status = quadBuffer.get(uri);
        if (status == null) {
            LOGGER.warn("Sink has not been opened for the uri, sink will be opened.");
            openSinkForUri(uri);
            status = quadBuffer.get(uri);
        }
        status.addQuad(this, uri, quad);
    }

    protected abstract void sendTriples(CrawleableUri uri, Collection<Triple> buffer);
    protected abstract void sendQuads(CrawleableUri uri, Collection<Quad> buffer);


    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        TripleBuffer status = tripleBuffer.remove(uri);
        if (status == null) {
            LOGGER.info("Try to close Sink for an uri, without open it before. Do nothing.");
            return;
        }
        status.sendTriples(this, uri);
        CrawlingActivity activity = (CrawlingActivity) uri.getData(Constants.URI_CRAWLING_ACTIVITY);
        if(activity != null) {
            activity.setNumberOfTriples(status.getNumberOfTriples());
        }
    }

}
