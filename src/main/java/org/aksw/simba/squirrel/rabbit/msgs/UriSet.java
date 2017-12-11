package org.aksw.simba.squirrel.rabbit.msgs;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public class UriSet implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public List<CrawleableUri> uris;

    public UriSet(List<CrawleableUri> uris) {
        this.uris = (uris == null) ? this.uris = Collections.emptyList() : uris;
    }

    public UriSet() {
        this.uris = Collections.emptyList();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uris == null) ? 0 : uris.hashCode());
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
        UriSet other = (UriSet) obj;
        if (uris == null) {
            if (other.uris != null)
                return false;
        } else if (!uris.equals(other.uris))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UriSet [uris=");
        builder.append(uris);
        builder.append("]");
        return builder.toString();
    }

}
