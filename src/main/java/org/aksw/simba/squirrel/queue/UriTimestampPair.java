package org.aksw.simba.squirrel.queue;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A tuple of a {@link CrawleableUri} and a timestamp. Mostly used in the context of recrawling.
 */
public class UriTimestampPair implements Serializable {

    private CrawleableUri uri;

    private long timestampNextCrawl;

    public UriTimestampPair(CrawleableUri uri, long timestampNextCrawl) {
        this.uri = uri;
        this.timestampNextCrawl = timestampNextCrawl;
    }

    public static List<CrawleableUri> extractUrisFromPairs(List<UriTimestampPair> pairs) {
        List<CrawleableUri> uris = new ArrayList<>();
        pairs.forEach(pair -> uris.add(pair.getUri()));
        return uris;
    }

    public CrawleableUri getUri() {
        return uri;
    }

    public long getTimestampNextCrawl() {
        return timestampNextCrawl;
    }
}
