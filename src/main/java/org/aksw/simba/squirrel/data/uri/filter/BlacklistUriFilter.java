package org.aksw.simba.squirrel.data.uri.filter;

import java.util.HashSet;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public class BlacklistUriFilter implements KnownUriFilter {

    protected Set<String> set;

    public BlacklistUriFilter() {
        this.set = new HashSet<String>();
    }

    public BlacklistUriFilter(Set<String> set) {
        this.set = set;
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        return !set.contains(uri.getUri().toString());
    }

    @Override
    public void add(CrawleableUri uri) {
        set.add(uri.getUri().toString());
    }
}
