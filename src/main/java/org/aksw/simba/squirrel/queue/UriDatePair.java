package org.aksw.simba.squirrel.queue;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UriDatePair implements Serializable {

    private CrawleableUri uri;

    private long timestampNextCrawl;

    public UriDatePair(CrawleableUri uri, long timestampNextCrawl) {
        this.uri = uri;
        this.timestampNextCrawl = timestampNextCrawl;
    }

    public static List<CrawleableUri> extractUrisFromPairs(List<UriDatePair> pairs) {
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
