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
            if (!frontierDoesRecrawling){
                return false;
            }
            return uris.get(uri).nextCrawlTimestamp < System.currentTimeMillis();
        } else {
            return true;
        }
    }

    @Override
    public void open() {
    }

    @Override
    public List<CrawleableUri> getOutdatedUris() {
        // get all uris with the following property:
        // (nextCrawlTimestamp has passed) AND (crawlingInProcess==false OR lastCrawlTimestamp is 3 times older than generalRecrawlTime)

        List<CrawleableUri> urisToRecrawl = new ArrayList<>();
        long generalRecrawlTime = Math.max(FrontierImpl.DEFAULT_GENERAL_RECRAWL_TIME, FrontierImpl.getGeneralRecrawlTime());

        for (CrawleableUri uri : uris.keys) {
            if (uris.get(uri).nextCrawlTimestamp < System.currentTimeMillis() &&
                (!uris.get(uri).crawlingInProcess || uris.get(uri).lastCrawlTimestamp < System.currentTimeMillis() - generalRecrawlTime * 3)) {
                urisToRecrawl.add(uri);
                uris.get(uri).crawlingInProcess = true;
            }
        }
        return urisToRecrawl;
    }

    @Override
    public void close() {
    }

    @Override
    public long count() {
        return uris.size();
    }
}
