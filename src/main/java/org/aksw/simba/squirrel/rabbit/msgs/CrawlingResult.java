package org.aksw.simba.squirrel.rabbit.msgs;

import org.aksw.simba.squirrel.queue.UriDatePair;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class CrawlingResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<UriDatePair> crawledUriDatePairs;
    public List<UriDatePair> newUriDatePairs;
    public int idOfWorker;

    public CrawlingResult(List<UriDatePair> crawledUriDatePairs, List<UriDatePair> newUriDatePairs, int idOfWorker) {
        this.newUriDatePairs = (newUriDatePairs == null) ? this.newUriDatePairs = Collections.emptyList() : newUriDatePairs;
        this.crawledUriDatePairs = crawledUriDatePairs;
        this.idOfWorker = idOfWorker;
    }

    public CrawlingResult(List<UriDatePair> crawledUriDatePairs) {
        this(crawledUriDatePairs, Collections.emptyList(), -1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((crawledUriDatePairs == null) ? 0 : crawledUriDatePairs.hashCode());
        result = prime * result + ((newUriDatePairs == null) ? 0 : newUriDatePairs.hashCode());
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
        if (newUriDatePairs == null) {
            if (other.newUriDatePairs != null)
                return false;
        } else if (!newUriDatePairs.equals(other.newUriDatePairs))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CrawlingResult [crawledUriDatePairs=");
        builder.append(crawledUriDatePairs);
        builder.append(", newUriDatePairs=");
        builder.append(newUriDatePairs);
        builder.append("]");
        return builder.toString();
    }
}
