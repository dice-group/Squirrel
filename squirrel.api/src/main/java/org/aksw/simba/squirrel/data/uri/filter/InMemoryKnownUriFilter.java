package org.aksw.simba.squirrel.data.uri.filter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

/**
 * A simple in-memory implementation of the {@link KnownUriFilter} interface.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class InMemoryKnownUriFilter implements KnownUriFilter {
    /**
     * - key: the crawled (known) uri
     * - value: the info about the URI (see {@link UriInfo}), including the reference list
     */
    protected Hashtable<CrawleableUri, UriInfo> uris;
    /**
     * Indicates whether the {@link org.aksw.simba.squirrel.frontier.Frontier} using this filter does recrawling.
     */
    private boolean frontierDoesRecrawling;
    protected long defaultRecrawlTime;

    /**
     * Constructor.
     *
     * @param frontierDoesRecrawling Value for {@link #frontierDoesRecrawling}.
     */
    public InMemoryKnownUriFilter(boolean frontierDoesRecrawling, long defaultRecrawlTime) {
        uris = new Hashtable<>();
        this.frontierDoesRecrawling = frontierDoesRecrawling;
        this.defaultRecrawlTime = defaultRecrawlTime;
    }

    /**
     * Constructor.
     */
    public InMemoryKnownUriFilter() {
        this(false, 0L);
    }

    /**
     * Constructor.
     *
     * @param uris                   Value for {@link #uris}.
     * @param frontierDoesRecrawling Value for {@link #frontierDoesRecrawling}.
     */
    public InMemoryKnownUriFilter(Hashtable<CrawleableUri, UriInfo> uris, boolean frontierDoesRecrawling) {
        this.uris = uris;
        this.frontierDoesRecrawling = frontierDoesRecrawling;
    }

    @Override
    public void add(CrawleableUri uri, long nextCrawlTimestamp) {
        add(uri, System.currentTimeMillis(), nextCrawlTimestamp);
    }

    @Override
    public void add(CrawleableUri uri, long lastCrawlTimestamp, long nextCrawlTimestamp) {
        UriInfo uriInfo = new UriInfo(lastCrawlTimestamp, nextCrawlTimestamp, false);
        uris.put(uri, uriInfo);
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
    public List<CrawleableUri> getOutdatedUris() {
        // get all uris with the following property:
        // (nextCrawlTimestamp has passed) AND (crawlingInProcess==false OR lastCrawlTimestamp is 3 times older than generalRecrawlTime)

        List<CrawleableUri> urisToRecrawl = new ArrayList<>();

        for (CrawleableUri uri : uris.keySet()) {
            if (uris.get(uri).nextCrawlTimestamp < System.currentTimeMillis() &&
                (!uris.get(uri).crawlingInProcess || uris.get(uri).lastCrawlTimestamp < System.currentTimeMillis() - defaultRecrawlTime * 3)) {
                urisToRecrawl.add(uri);
                uris.get(uri).crawlingInProcess = true;
            }
        }
        return urisToRecrawl;
    }

    @Override
    public long count() {
        return uris.size();
    }


    private class UriInfo {
        long lastCrawlTimestamp;
        long nextCrawlTimestamp;
        boolean crawlingInProcess;

        UriInfo(long lastCrawlTimestamp, long nextCrawlTimestamp, boolean crawlingInProcess) {
            this.lastCrawlTimestamp = lastCrawlTimestamp;
            this.nextCrawlTimestamp = nextCrawlTimestamp;
            this.crawlingInProcess = crawlingInProcess;
        }
    }
}