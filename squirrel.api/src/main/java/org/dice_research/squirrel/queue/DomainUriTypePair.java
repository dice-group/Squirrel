package org.dice_research.squirrel.queue;

import org.dice_research.squirrel.data.uri.UriType;

/**
 * Pair comprising a domain and a type.
 *
 */
@SuppressWarnings("deprecation")
public class DomainUriTypePair implements Comparable<DomainUriTypePair> {
    
    private String domain;
    private UriType type;
    
    
    public  DomainUriTypePair(String domain, UriType type) {
        this.domain = domain;
        this.type = type;
    }
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        DomainUriTypePair other = (DomainUriTypePair) obj;
        if (domain == null) {
            if (other != null)
                return false;
        } else if (!domain.equals(other.domain))
            return false;
       
        return (type != other.type);
    }
    
    @Override
    public int compareTo(DomainUriTypePair o) {
        int diff = this.type.ordinal() - o.type.ordinal();
        if (diff == 0) {
            diff = this.domain.compareTo(o.getDomain());
        }
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public String getDomain() {
        return domain;
    }


    public UriType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "DomainUriTypePair{" +
            "domain=" + domain +
            ", type=" + type +
            '}';
    }


}
