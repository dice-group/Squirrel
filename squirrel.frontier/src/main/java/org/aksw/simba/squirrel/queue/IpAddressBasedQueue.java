package org.aksw.simba.squirrel.queue;

import java.net.InetAddress;

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
}
