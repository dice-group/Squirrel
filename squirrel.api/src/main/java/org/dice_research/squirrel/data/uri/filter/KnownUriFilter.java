package org.dice_research.squirrel.data.uri.filter;

import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * A {@link UriFilter} that works like a blacklist filter and contains only those
 * URIs on its blacklist that the crawler already has seen before.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public interface KnownUriFilter extends UriFilter {

    /**
     * Adds the given URI to the list of already known URIs. Works like calling {@link #add(CrawleableUri, long)} with the current system time.
     *
     * @param uri the URI that should be added to the list.
     * @param nextCrawlTimestamp The time at which the given URI should be crawled next.
     */
    public void add(CrawleableUri uri, long nextCrawlTimestamp);

    /**
     * Adds the given URI to the list of already known URIs together with the the time at which it has been crawled.
     *
     * @param uri       the URI that should be added to the list.
     * @param lastCrawlTimestamp the time at which the given URI has eben crawled.
     * @param nextCrawlTimestamp The time at which the given URI should be crawled next.
     */
    void add(CrawleableUri uri, long lastCrawlTimestamp, long nextCrawlTimestamp);
    
    public default void add(CrawleableUri uri) {
    	add(uri, System.currentTimeMillis());
    }

    /**
     * Returns all {@link CrawleableUri}s which have to be recrawled. This means their time to next crawl has passed.
     *
     * @return The outdated {@link CrawleableUri}s.
     */
    public List<CrawleableUri> getOutdatedUris();

    /**
     * count the numbers of known URIs
     * @return the number of entries from a open queue
     */
    long count();
    
    /**
     * Opens the queue and allocates necessary resources.
     */
    public void open();
}
