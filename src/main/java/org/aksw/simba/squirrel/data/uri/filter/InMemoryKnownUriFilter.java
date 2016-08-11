package org.aksw.simba.squirrel.data.uri.filter;

import com.carrotsearch.hppc.ObjectLongOpenHashMap;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.seed.generator.impl.AbstractSeedGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple in-memory implementation of the {@link KnownUriFilter} interface.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class InMemoryKnownUriFilter implements KnownUriFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryKnownUriFilter.class);

    protected ObjectLongOpenHashMap<CrawleableUri> uris = new ObjectLongOpenHashMap<>();
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
        LOGGER.debug("Adding {} to the queue", uri);
        if (uris.containsKey(uri)) {
            long nextCrawlingAt = uris.get(uri) + timeBeforeRecrawling;
            return nextCrawlingAt < System.currentTimeMillis();
        }
        return true;
    }
}
