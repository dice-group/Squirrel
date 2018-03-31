package org.aksw.simba.squirrel.data.uri;

import java.util.Date;

public class UriInfo {

    private CrawleableUri uri;

    private Date timeStampNextCrawling;

    public UriInfo(CrawleableUri uri, Date timeStampNextCrawling) {
        this.uri = uri;
        this.timeStampNextCrawling = timeStampNextCrawling;
    }

    public CrawleableUri getUri() {
        return uri;
    }

    public Date getTimeStampNextCrawling() {
        return timeStampNextCrawling;
    }
}
