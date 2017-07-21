package org.aksw.simba.squirrel.data.uri.filter;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import com.carrotsearch.hppc.ObjectLongOpenHashMap;

/**
 * A simple in-memory implementation of the {@link KnownUriFilter} interface.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class InMemoryKnownUriFilter implements KnownUriFilter {
    protected ObjectLongOpenHashMap<CrawleableUri> uris = new ObjectLongOpenHashMap<>();
    protected long timeBeforeRecrawling;

    /**
     * Constructor.
     *
     * @param timeBeforeRecrawling
     *            time in milliseconds before a URI is crawled again. A negative
     *            values turns disables recrawling.
     */
    public InMemoryKnownUriFilter(long timeBeforeRecrawling) {
        this.timeBeforeRecrawling = timeBeforeRecrawling;
    }

    public InMemoryKnownUriFilter(ObjectLongOpenHashMap<CrawleableUri> uris, long timeBeforeRecrawling) {
        this.uris = uris;
        this.timeBeforeRecrawling = timeBeforeRecrawling;
    }

    @Override
    public void add(CrawleableUri uri) {
        add(uri, System.currentTimeMillis());
    }

    @Override
    public void add(CrawleableUri uri, long timestamp) {
        uris.put(uri, timestamp);
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        if (uris.containsKey(uri)) {
            // if recrawling is disabled
            if (timeBeforeRecrawling < 0) {
                return false;
            }
            long nextCrawlingAt = uris.get(uri) + timeBeforeRecrawling;
            return nextCrawlingAt < System.currentTimeMillis();
        } else {
            return true;
        }
    }

    @Override
    public void open() {}

    @Override
    public void close() {}
}
