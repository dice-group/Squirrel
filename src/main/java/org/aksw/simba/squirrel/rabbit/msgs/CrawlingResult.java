package org.aksw.simba.squirrel.rabbit.msgs;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.queue.UriTimestampPair;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class CrawlingResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<UriTimestampPair> crawledUriDatePairs;
    public List<CrawleableUri> newUris;
    public int idOfWorker;

    public CrawlingResult(List<UriTimestampPair> crawledUriDatePairs, List<CrawleableUri> newUris, int idOfWorker) {
        this.newUris = (newUris == null) ? this.newUris = Collections.emptyList() : newUris;
        this.crawledUriDatePairs = crawledUriDatePairs;
        this.idOfWorker = idOfWorker;
    }

    public CrawlingResult(List<UriTimestampPair> crawledUriDatePairs) {
        this(crawledUriDatePairs, Collections.emptyList(), -1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((crawledUriDatePairs == null) ? 0 : crawledUriDatePairs.hashCode());
        result = prime * result + ((newUris == null) ? 0 : newUris.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CrawlingResult other = (CrawlingResult) obj;
        if (crawledUriDatePairs == null) {
            if (other.crawledUriDatePairs != null)
                return false;
        } else if (!crawledUriDatePairs.equals(other.crawledUriDatePairs))
            return false;
        if (newUris == null) {
            if (other.newUris != null)
                return false;
        } else if (!newUris.equals(other.newUris))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CrawlingResult [crawledUriDatePairs=");
        builder.append(crawledUriDatePairs);
        builder.append(", newUris=");
        builder.append(newUris);
        builder.append("]");
        return builder.toString();
    }
}
