package org.aksw.simba.squirrel.frontier;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * A Frontier is a central class of the crawler managing a queue of URIs that
 * should be crawled in the future. This includes to give access to the queue in
 * terms of 1) getting the next URIs to crawl and 2) adding new URIs to the
 * queue. Note that the Frontier has the ability to check whether a URI should
 * be crawled and, thus, should be added to the queue or not. For example, a
 * Frontier might not add a URI that has already been crawled.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface Frontier extends Closeable {

    /**
     * Returns the next chunk of URIs that should be crawled or null. Note that
     * if URIs are received from the Frontier using this method, the Frontier
     * should be notified if the crawling of these URIs is done using the
     * {@link #crawlingDone(Dictionary)} method.
     *
     * @return the next chunk of URIs that should be crawled or null if no URIs
     *         are available
     */
    List<CrawleableUri> getNextUris();

    /**
     * Add this URIs to the {@link Frontier}s internal queue if the internal
     * rules of the {@link Frontier} allow it.
     *
     * @param uri
     *            the URI that should be added to the {@link Frontier}
     */
    void addNewUri(CrawleableUri uri);

    /**
     * Adds the given list of URIs to the {@link Frontier}. It is like calling
     * {@link #addNewUri(CrawleableUri)} with every single URI.
     *
     * @param newUris
     *            the URIs that should be added to the {@link Frontier}
     */
    void addNewUris(List<CrawleableUri> newUris);

    /**
     * This method should be called after a list of URIs have been requested
     * using the {@link #getNextUris()} method and the crawling has been
     * finished. Internally, the {@link Frontier} marks the URIs as crawled and
     * adds the new URIs using the {@link #addNewUris(List)} method.
     *
     * @param uris crawled URIs
     */
    void crawlingDone(List<CrawleableUri> uris);

    /**
     * (optional) Returns the number of URIs that have been requested from the
     * Frontier using {@link Frontier#getNextUris()} and have not been marked as
     * crawled using {@link Frontier#crawlingDone(Map)}.
     *
     * @return the number of pending URIs.
     */
    int getNumberOfPendingUris();

    /**
     * Indicates whether this frontier does recrawling.
     *
     * @return True iff recrawling is active.
     */
    boolean doesRecrawling();
}