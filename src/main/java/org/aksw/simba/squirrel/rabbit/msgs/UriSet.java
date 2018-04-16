package org.aksw.simba.squirrel.rabbit.msgs;

import org.aksw.simba.squirrel.queue.UriDatePair;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class UriSet implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<UriDatePair> uriDatePairs;

    public UriSet(List<UriDatePair> uriDatePairs) {
        this.uriDatePairs = (uriDatePairs == null) ? this.uriDatePairs = Collections.emptyList() : uriDatePairs;
    }

    public UriSet() {
        this.uriDatePairs = Collections.emptyList();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uriDatePairs == null) ? 0 : uriDatePairs.hashCode());
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
        if (uriDatePairs == null) {
            if (other.uriDatePairs != null)
                return false;
        } else if (!uriDatePairs.equals(other.uriDatePairs))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UriSet [uriDatePairs=");
        builder.append(uriDatePairs);
        builder.append("]");
        return builder.toString();
    }

}
