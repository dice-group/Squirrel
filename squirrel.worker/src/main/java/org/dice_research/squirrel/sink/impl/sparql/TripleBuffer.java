package org.dice_research.squirrel.sink.impl.sparql;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.CrawlingActivity;

import java.util.ArrayList;
import java.util.List;

public class TripleBuffer {

    protected final List<Triple> buffer;
    protected final int bufferSize;
    protected long numberOfTriples = 0;

    public TripleBuffer() {
        this(AbstractBufferingTripleBasedSink.DEFAULT_BUFFER_SIZE);
    }

    public TripleBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = new ArrayList<>(bufferSize);
    }

    public void addTriple(AbstractBufferingTripleBasedSink sink, CrawleableUri uri, Triple triple) {
        synchronized (buffer) {
            buffer.add(triple);
            if (buffer.size() >= bufferSize) {
                sendTriples(sink, uri);
            }
        }
    }

    public void sendTriples(AbstractBufferingTripleBasedSink sink, CrawleableUri uri) {
        synchronized (buffer) {
            sink.sendTriples(uri, buffer);
            if(numberOfTriples == 0) {
                //in case of adding a triple at a later stage, numberOfTriples will be 0. So fetching it from the CrawlingActivity
                numberOfTriples = ((CrawlingActivity) uri.getData().get(Constants.URI_CRAWLING_ACTIVITY)).getNumberOfTriples();
            }
            numberOfTriples += buffer.size();
            buffer.clear();
        }
    }

    public long getNumberOfTriples() {
        return numberOfTriples;
    }
}
