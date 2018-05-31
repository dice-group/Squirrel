package org.aksw.simba.squirrel.rabbit.msgs;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class CrawlingResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<CrawleableUri> crawledUris;
    public List<CrawleableUri> newUris;
    public int idOfWorker;

    public CrawlingResult(List<CrawleableUri> crawledUris, List<CrawleableUri> newUris, int idOfWorker) {
        this.newUris = (newUris == null) ? this.newUris = Collections.emptyList() : newUris;
        this.crawledUris = crawledUris;
        this.idOfWorker = idOfWorker;
    }

    public CrawlingResult(List<CrawleableUri> crawledUris) {
        this(crawledUris, Collections.emptyList(), -1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((crawledUris == null) ? 0 : crawledUris.hashCode());
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
        if (crawledUris == null) {
            if (other.crawledUris != null)
                return false;
        } else if (!crawledUris.equals(other.crawledUris))
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
        String ret = "CrawlingResult [crawledUriDatePairs=" +
            crawledUris +
            ", newUris=" +
            newUris +
            "]";
        return ret;
    }
}
