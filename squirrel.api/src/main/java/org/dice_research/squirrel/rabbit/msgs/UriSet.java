package org.dice_research.squirrel.rabbit.msgs;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * Simple structure represents a set of URIs.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class UriSet implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * The URIs of this set.
     */
    public List<CrawleableUri> uris;

    /**
     * Constructor.
     * 
     * @param uris the URIs of this set
     */
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
