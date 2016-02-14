package org.aksw.simba.squirrel.fetcher;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;

public interface Fetcher {

    /**
     * 
     * 
     * @param uri
     * @param sink
     * @return number of fetched triples. -1, if an error occurred.
     */
    public int fetch(CrawleableUri uri, Sink sink);
}
