package org.dice_research.squirrel.queue;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * This extension of the {@link UriQueue} interface defines additional methods
 * enabling the queue to manage the retrieving of chunks of URIs based on a defined key which is used to group the URIs.
 * If a chunk is returned by this queue, the key value they have in common is marked
 * as blocked. No other chunk will contain URIs with this key value until the
 * method {@link #markUrisAsAccessible(Collection<CrawleableUri>)} is called to free the
 * keys of these URIs.
 * 
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 * @author Michael R&ouml;der (michael.roeder@mail.uni-paderborn.de)
 *
 */
public interface BlockingQueue<T> extends UriQueue {
    
    /**
     * Marks the key values of the URIs as accessible.
     * 
     * @param uris
     *            the Domain that should be marked as accessible.
     */
    public void markUrisAsAccessible(Collection<CrawleableUri> uris);

    /**
     * Returns the number of key values that are currently blocked.
     * 
     * @return the number of key values that are currently blocked.
     */
    public int getNumberOfBlockedKeys();
    
    /**
     * Goes through the queue und collects all Domains with their URIs
     *
     * @return a Domain-iterator with the list of uris for each Domain
     */
    public Iterator<AbstractMap.SimpleEntry<T, List<CrawleableUri>>> getIterator();

}
