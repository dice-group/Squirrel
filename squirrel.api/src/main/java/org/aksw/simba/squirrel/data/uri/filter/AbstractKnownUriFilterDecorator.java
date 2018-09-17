package org.aksw.simba.squirrel.data.uri.filter;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public abstract class AbstractKnownUriFilterDecorator implements KnownUriFilterDecorator {

    protected KnownUriFilter decorated;
    
    protected AbstractKnownUriFilterDecorator() {
        this.decorated = null;
    }
    
    public AbstractKnownUriFilterDecorator(KnownUriFilter decorated) {
        this.decorated = decorated;
    }

    @Override
    public void add(CrawleableUri uri, long nextCrawlTimestamp) {
        decorated.add(uri, nextCrawlTimestamp);
    }

    @Override
    public void add(CrawleableUri uri, long lastCrawlTimestamp, long nextCrawlTimestamp) {
        decorated.add(uri, nextCrawlTimestamp);
    }

    @Override
    public List<CrawleableUri> getOutdatedUris() {
        return decorated.getOutdatedUris();
    }

    @Override
    public long count() {
        return decorated.count();
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        return decorated.isUriGood(uri);
    }

    @Override
    public KnownUriFilter getDecorated() {
        return decorated;
    }
    
    @Override
    public void close() throws IOException {
        if(decorated instanceof Closeable) {
            ((Closeable) decorated).close();
        }
    }

}
