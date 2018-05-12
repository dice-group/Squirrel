package org.aksw.simba.squirrel.data.uri.filter;

import com.carrotsearch.hppc.ObjectObjectOpenHashMap;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple in-memory implementation of the {@link KnownUriFilter} interface.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class InMemoryKnownUriFilter implements KnownUriFilter {
    protected ObjectObjectOpenHashMap<CrawleableUri, UriInfo> uris = new ObjectObjectOpenHashMap<>();
    /**
     * Indicates whether the {@link org.aksw.simba.squirrel.frontier.Frontier} using this filter does recrawling.
     */
    private boolean frontierDoesRecrawling;

    /**
     * Constructor.
     *
     * @param frontierDoesRecrawling Value for {@link #frontierDoesRecrawling}.
     */
    public InMemoryKnownUriFilter(boolean frontierDoesRecrawling) {
        this.frontierDoesRecrawling = frontierDoesRecrawling;
    }

    /**
     * Constructor.
     */
    public InMemoryKnownUriFilter() {
        this(false);
    }

    /**
     * Constructor.
     *
     * @param uris                   Value for {@link #uris}.
     * @param frontierDoesRecrawling Value for {@link #frontierDoesRecrawling}.
     */
    public InMemoryKnownUriFilter(ObjectObjectOpenHashMap<CrawleableUri, UriInfo> uris, boolean frontierDoesRecrawling) {
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
