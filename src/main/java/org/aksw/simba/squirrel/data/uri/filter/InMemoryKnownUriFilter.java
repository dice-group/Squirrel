package org.aksw.simba.squirrel.data.uri.filter;

import com.carrotsearch.hppc.ObjectLongOpenHashMap;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;

/**
 * A simple in-memory implementation of the {@link KnownUriFilter} interface.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class InMemoryKnownUriFilter implements KnownUriFilter {

    protected ObjectLongOpenHashMap<CrawleableUri> uris;
    protected long timeBeforeRecrawling;

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
            long nextCrawlingAt = uris.get(uri) + timeBeforeRecrawling;
            return nextCrawlingAt < System.currentTimeMillis();
        }
        return true;
    }
}
