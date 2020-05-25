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
    public void addUri(CrawleableUri uri);
    
    /**
     * Returns the next chunk of URIs that should be crawled or null. Note that
     * this method removes the URIs from the queue.
     * 
     * @return the next chunk of URIs that should be crawled or null if no URIs
     *         are available
     */
    public List<CrawleableUri> getNextUris();
    
    
    /**
     * Returns true if the queue is empty
     * 
     * @return
     */
    public boolean isEmpty();

    /**
     * Closes the queue and frees all resources.
     */
    public void close();

    /**
     * Opens the queue and allocates necessary resources.
     */
    public void open();
    
}
