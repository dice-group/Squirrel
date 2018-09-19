package org.aksw.simba.squirrel.rabbit.msgs;

import java.io.Serializable;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public class CrawlingResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public final List<CrawleableUri> uris;
    public final String idOfWorker;

    public CrawlingResult(List<CrawleableUri> uris, String idOfWorker) {
        this.uris = uris;
        this.idOfWorker = idOfWorker;
    }

    public CrawlingResult(List<CrawleableUri> uris) {
        this(uris, null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + uris.hashCode();
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
        if (uris == null) {
            return other.uris == null;
        } else return uris.equals(other.uris);

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CrawlingResult [uris=");
        uris.toString();
        return builder.toString();
    }
}