package org.dice_research.squirrel.queue;

import java.net.InetAddress;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * This extension of the {@link UriQueue} interface defines additional methods
 * enabling the queue to manage the retrieving of chunks of URIs based on Domains.
 *  If a chunk is returned by this queue, the Domains are marked
 * as blocked. No other chunk will contain URIs of these Domains until the
 * method {@link #markDomainAsAccessible(String)} is called to free the
 * Domain.
 * 
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */
public interface DomainBasedQueue extends UriQueue {
    
    /**
     * Marks the given Domain as accessible.
     * 
     * @param domainName
     *            the Domain that should be marked as accessible.
     */
    public void markDomainAsAccessible(String domainName);

    /**
     * Returns the number of Domains that are currently blocked.
     * 
     * @return the number of Domains that are currently blocked.
     */
    public int getNumberOfBlockedDomains();
    /**
     * Goes through the queue und collects all Domains with their URIs
     *
     * @return a Domain-iterator with the list of uris for each Domain
     */
    Iterator<AbstractMap.SimpleEntry<String, List<CrawleableUri>>> getDomainIterator();

}
