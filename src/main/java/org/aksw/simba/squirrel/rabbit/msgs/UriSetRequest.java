package org.aksw.simba.squirrel.rabbit.msgs;

public class UriSetRequest {

    // No content until now. The request could contain some additional values,
    // e.g., types of URIs that can be crawled.
    
    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }
}
