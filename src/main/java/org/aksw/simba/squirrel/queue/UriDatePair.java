package org.aksw.simba.squirrel.queue;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.util.Date;

public class UriDatePair {

    private CrawleableUri uri;

    private Date dateToCrawl;

    public UriDatePair(CrawleableUri uri, Date dateToCrawl) {
        this.uri = uri;
        this.dateToCrawl = dateToCrawl;
    }

    public CrawleableUri getUri() {
        return uri;
    }

    public Date getDateToCrawl() {
        return dateToCrawl;
    }
}
