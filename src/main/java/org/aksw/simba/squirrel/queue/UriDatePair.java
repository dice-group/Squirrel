package org.aksw.simba.squirrel.queue;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.util.ArrayList;
import java.util.List;

public class UriDatePair {

    private CrawleableUri uri;

    private long dateToCrawl;

    public UriDatePair(CrawleableUri uri, long dateToCrawl) {
        this.uri = uri;
        this.dateToCrawl = dateToCrawl;
    }

    public static List<CrawleableUri> extractUrisFromPairs(List<UriDatePair> pairs) {
        List<CrawleableUri> uris = new ArrayList<>();
        pairs.forEach(pair -> uris.add(pair.getUri()));
        return uris;
    }

    public CrawleableUri getUri() {
        return uri;
    }

    public long getDateToCrawl() {
        return dateToCrawl;
    }
}
