package org.aksw.simba.squirrel.queue;

import java.net.InetAddress;

import org.aksw.simba.squirrel.data.uri.UriType;

public class IpUriTypePair implements Comparable<IpUriTypePair> {
    public InetAddress ip;
    public UriType type;

    public IpUriTypePair(InetAddress ip, UriType type) {
        this.ip = ip;
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
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
        IpUriTypePair other = (IpUriTypePair) obj;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public int compareTo(IpUriTypePair o) {
        int diff = this.type.ordinal() - o.type.ordinal();
        if (diff == 0) {
            diff = this.ip.getHostAddress().compareTo(o.ip.getHostAddress());
        }
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "IpUriTypePair{" +
            "ip=" + ip +
            ", type=" + type +
            '}';
    }
}
