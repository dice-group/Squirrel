package org.dice_research.squirrel.queue;

import java.net.InetAddress;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * This extension of the {@link UriQueue} interface defines additional methods
 * enabling the queue to manage the retrieving of chunks of URIs based on IP
 * addresses. If a chunk is returned by this queue, the IP addresses are marked
 * as blocked. No other chunk will contain URIs of these IP addresses until the
 * method {@link #markIpAddressAsAccessible(InetAddress)} is called to free the
 * IP address.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface IpAddressBasedQueue extends UriQueue {

    /**
     * Marks the given IP address as accessible.
     * 
     * @param ip
     *            the IP address that should be marked as accessible.
     */
    public void markIpAddressAsAccessible(InetAddress ip);

    /**
     * Returns the number of IP addresses that are currently blocked.
     * 
     * @return the number of IP addresses that are currently blocked.
     */
    public int getNumberOfBlockedIps();
    /**
     * Goes through the queue und collects all IP-address with their URIs
     *
     * @return a IP-address-iterator with the list of uris for each IP-address
     */
    Iterator<AbstractMap.SimpleEntry<InetAddress, List<CrawleableUri>>> getIPURIIterator();
}
