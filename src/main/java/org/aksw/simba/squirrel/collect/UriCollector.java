package org.aksw.simba.squirrel.collect;

import java.util.Iterator;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.SinkBase;
import org.apache.jena.graph.Triple;

public interface UriCollector extends SinkBase {

    public void addTriple(CrawleableUri uri, Triple triple);

    public Iterator<String> getUris(CrawleableUri uri);

}
