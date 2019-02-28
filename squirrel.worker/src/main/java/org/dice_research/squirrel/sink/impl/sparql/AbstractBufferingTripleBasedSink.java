package org.dice_research.squirrel.sink.impl.sparql;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.sink.tripleBased.TripleBasedSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBufferingTripleBasedSink implements TripleBasedSink {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBufferingTripleBasedSink.class);

    /**
     * Interval that specifies how many triples are to be buffered at once until
     * they are sent to the sink.
     */
    protected static final int DEFAULT_BUFFER_SIZE = 200;
    /**
     * The data structure (map) in which the triples are buffered.
     */
    private Map<CrawleableUri, TripleBuffer> buffers = Collections.synchronizedMap(new HashMap<>());

    protected int bufferSize = DEFAULT_BUFFER_SIZE;

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        buffers.put(uri, new TripleBuffer());
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        TripleBuffer status = buffers.get(uri);
        if (status == null) {
            LOGGER.warn("Sink has not been opened for the uri, sink will be opened.");
            openSinkForUri(uri);
            status = buffers.get(uri);
        }
        status.addTriple(this, uri, triple);
    }

    protected abstract void sendTriples(CrawleableUri uri, Collection<Triple> buffer);

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        TripleBuffer status = buffers.remove(uri);
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
