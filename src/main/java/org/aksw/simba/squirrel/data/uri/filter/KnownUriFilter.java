package org.aksw.simba.squirrel.data.uri.filter;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.util.List;

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
     */
    void add(CrawleableUri uri);

    /**
     * Adds the given URI to the list of already known URIs together with the the time at which it has been crawled.
     *
     * @param uri       the URI that should be added to the list.
     * @param timestamp the time at which the given URI has eben crawled.
     */
    void add(CrawleableUri uri, long timestamp);

    /**
     * Adds the given URI to the list of already known URIs together with the the time at which it has been crawled.
     * In addition, it adds a list of uris, that were found by crawling the certain uri. ATTENTION: these uris have no to be crawled at this point of time!
     *
     * @param uri       the URI that should be added to the list.
     * @param urisFound uris, that were found by crawling the uri
     * @param timestamp the time at which the given URI has eben crawled.
     */
    void add(CrawleableUri uri, List<CrawleableUri> urisFound, long timestamp);

    /**
     * Close RDB connection, destroy the database.
     */
    void close();

    /**
     * Open RDB connection, init the database.
     */
    void open();

    /**
     * count the numbers of known URIs
     * @return the number of lines in that database
     */
    long count();
}
