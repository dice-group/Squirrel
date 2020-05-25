package org.dice_research.squirrel.sink.impl.sparql;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.data.uri.CrawleableUri;

public class TripleBuffer {

    protected final List<Triple> buffer;
    protected final int bufferSize;
    protected long numberOfTriples = 0;

    public TripleBuffer() {
        this(AbstractBufferingSink.DEFAULT_BUFFER_SIZE);
    }

    public TripleBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = new ArrayList<>(bufferSize);
    }

    public void addTriple(AbstractBufferingSink sink, CrawleableUri uri, Triple triple) {
        synchronized (buffer) {
            buffer.add(triple);
            if (buffer.size() >= bufferSize) {
                sendTriples(sink, uri);
            }
        }
    }

    public void sendTriples(AbstractBufferingSink sink, CrawleableUri uri) {
        synchronized (buffer) {
            sink.sendTriples(uri, buffer);
            numberOfTriples += buffer.size();
            buffer.clear();
        }
    }

    public long getNumberOfTriples() {
        return numberOfTriples;
    }
}
