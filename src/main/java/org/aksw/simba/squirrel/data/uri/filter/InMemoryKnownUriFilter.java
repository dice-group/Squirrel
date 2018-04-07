package org.aksw.simba.squirrel.data.uri.filter;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * A simple in-memory implementation of the {@link KnownUriFilter} interface.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class InMemoryKnownUriFilter implements KnownUriFilter {
    protected Hashtable<CrawleableUri, AbstractMap.SimpleEntry<List<CrawleableUri>, Long>> uris;
    protected long timeBeforeRecrawling;

    /**
     * Constructor.
     *
     * @param timeBeforeRecrawling
     *            time in milliseconds before a URI is crawled again. A negative
     *            values turns disables recrawling.
     */
    public InMemoryKnownUriFilter(long timeBeforeRecrawling) {
        this.uris = new Hashtable<>();
        this.timeBeforeRecrawling = timeBeforeRecrawling;
    }

    public InMemoryKnownUriFilter(Hashtable<CrawleableUri, AbstractMap.SimpleEntry<List<CrawleableUri>, Long>> uris, long timeBeforeRecrawling) {
        this.uris = uris;
        this.timeBeforeRecrawling = timeBeforeRecrawling;
    }

    @Override
    public void add(CrawleableUri uri) {
        uris.put(uri, new AbstractMap.SimpleEntry<>(Collections.EMPTY_LIST, System.currentTimeMillis()));
    }

    @Override
    public void add(CrawleableUri uri, long timestamp) {
        uris.put(uri, new AbstractMap.SimpleEntry<>(Collections.EMPTY_LIST, timestamp));
    }

    @Override
    public void add(CrawleableUri uri, List<CrawleableUri> urisFound, long timestamp) {
        uris.put(uri, new AbstractMap.SimpleEntry<>(urisFound, timestamp));
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        if (uris.containsKey(uri)) {
            // if recrawling is disabled
            if (timeBeforeRecrawling < 0) {
                return false;
            }
            long nextCrawlingAt = uris.get(uri).getValue() + timeBeforeRecrawling;
            return nextCrawlingAt < System.currentTimeMillis();
        } else {
            return true;
        }
    }

    @Override
    public void open() {}

    @Override
    public void close() {}

    @Override
    public long count() {
        return uris.size();
    }
}
