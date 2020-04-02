package org.dice_research.squirrel.sink;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;

public abstract class AbstractSinkDecorator implements SinkDecorator {

    protected Sink decorated;

    public AbstractSinkDecorator(Sink decorated) {
        this.decorated = decorated;
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        decorated.addTriple(uri, triple);
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        decorated.openSinkForUri(uri);
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        decorated.closeSinkForUri(uri);
    }

    @Override
    public Sink getDecorated() {
        return decorated;
    }

}
