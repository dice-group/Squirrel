package org.aksw.simba.squirrel.rabbit.msgs;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

public class CrawlingResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public final Hashtable<CrawleableUri, List<CrawleableUri>> uriMap;
    public final int idOfWorker;

    public CrawlingResult(Hashtable<CrawleableUri, List<CrawleableUri>> uriMap, int idOfWorker) {
        this.uriMap = (uriMap == null) ? new Hashtable<>(0, 1) : uriMap;
        this.idOfWorker = idOfWorker;
    }

    public CrawlingResult(Hashtable<CrawleableUri, List<CrawleableUri>> uriMap) {
        this(uriMap, -1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + uriMap.hashCode();
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
        if (uriMap == null) {
            return other.uriMap == null;
        } else return uriMap.equals(other.uriMap);

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CrawlingResult [uriMap=");
        uriMap.forEach((key, value) -> builder.append("[" + key + ": [" + value + "]]"));
        return builder.toString();
    }
}