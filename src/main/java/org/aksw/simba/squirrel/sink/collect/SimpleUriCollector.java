package org.aksw.simba.squirrel.sink.collect;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.AbstractSinkDecorator;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

public class SimpleUriCollector extends AbstractSinkDecorator implements UriCollector {

    protected Set<String> uris = new HashSet<String>();

    public SimpleUriCollector(Sink decorated) {
        super(decorated);
    }

    @Override
    public Iterator<String> getUris() {
        return uris.iterator();
    }

    @Override
    public void reset() {
        uris.clear();
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        addUri(triple.getSubject());
        addUri(triple.getPredicate());
        addUri(triple.getObject());
        super.addTriple(uri, triple);
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        reset();
        super.closeSinkForUri(uri);
    }

    protected void addUri(Node node) {
        if (node.isURI()) {
            uris.add(node.getURI());
        }
    }
}
