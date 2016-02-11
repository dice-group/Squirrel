package org.aksw.simba.squirrel.sink;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public interface Sink {

    public void addTriple(CrawleableUri uri, String data);
    
    public void openSinkForUri(CrawleableUri uri);
    
    public void closeSinkForUri(CrawleableUri uri);
}
