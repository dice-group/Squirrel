package org.aksw.simba.squirrel.sink;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.apache.jena.graph.Triple;

public interface Sink {

    public void addTriple(CrawleableUri uri, Triple triple);
    
    public void openSinkForUri(CrawleableUri uri);
    
    public void closeSinkForUri(CrawleableUri uri);
}
