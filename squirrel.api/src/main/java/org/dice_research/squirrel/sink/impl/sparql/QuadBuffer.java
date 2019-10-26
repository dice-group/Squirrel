package org.dice_research.squirrel.sink.impl.sparql;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.sparql.core.Quad;
import org.dice_research.squirrel.data.uri.CrawleableUri;

public class QuadBuffer {

    protected final List<Quad> buffer;
    protected final int bufferSize;
    protected long numberOfQuads = 0;
    
    public QuadBuffer() {
        this(AbstractBufferingSink.DEFAULT_BUFFER_SIZE);
    }

    public QuadBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = new ArrayList<>(bufferSize);
    }

    public void addQuad(AbstractBufferingSink sink, CrawleableUri uri, Quad quad) {
        synchronized (buffer) {
            buffer.add(quad);
            if (buffer.size() >= bufferSize) {
                sendQuads(sink, uri);
            }
        }
    }

    public void sendQuads(AbstractBufferingSink sink, CrawleableUri uri) {
        synchronized (buffer) {
            sink.sendQuads(uri, buffer);
            numberOfQuads += buffer.size();
            buffer.clear();
        }
    }

    public long getNumberOfQuads() {
        return numberOfQuads;
    }
}