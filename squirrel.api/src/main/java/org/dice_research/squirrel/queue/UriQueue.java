package org.dice_research.squirrel.queue;

import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * Interface of a URI queue managing the URIs that should be crawled next.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface UriQueue {

    /**
     * Adds the given {@link CrawleableUri} instance to the queue.
     *
     * @param uri
     *            the {@link CrawleableUri} instance that should be added to the
     *            queue.
     */
     void addUri(CrawleableUri uri);

    /**
     * Returns the next chunk of URIs that should be crawled or null. Note that
     * this method removes the URIs from the queue.
     *
     * @return the next chunk of URIs that should be crawled or null if no URIs
     *         are available
     */
    List<CrawleableUri> getNextUris();


    /**
     * Returns true if the queue is empty
     *
     * @return
     */
    boolean isEmpty();

    /**
     * Closes the queue and frees all resources.
     */
    void close();

    /**
     * Opens the queue and allocates necessary resources.
     */
    void open();

    /**
     * Adds the given {@link CrawleableUri} instances to the queue.
     *
     * @param uris
     *            the {@link CrawleableUri} instances that should be added to the
     *            queue.
     */
    void addUris(List<CrawleableUri> uris);
}
