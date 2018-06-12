package org.aksw.simba.squirrel.frontier.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.UriFilter;
import org.aksw.simba.squirrel.frontier.ExtendedFrontier;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.queue.UriQueue;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtendedFrontierImpl extends FrontierImpl implements ExtendedFrontier {

    /**
     * Constructor.
     *
     * @param knownUriFilter     {@link UriFilter} used to identify URIs that already have been
     *                           crawled.
     * @param queue              {@link UriQueue} used to manage the URIs that should be
     *                           crawled.
     * @param doesRecrawling     used to select if URIs should be recrawled.
     * @param generalRecrawlTime used to select the general Time after URIs should be recrawled. If Value is null the default Time is used.
     * @param timerPeriod        used to select if URIs should be recrawled.
     */
    @SuppressWarnings("unused")
    public ExtendedFrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue, boolean doesRecrawling, long generalRecrawlTime, long timerPeriod) {
        super(knownUriFilter, queue, doesRecrawling, generalRecrawlTime, timerPeriod);
    }

    /**
     * Constructor.
     *
     * @param knownUriFilter {@link UriFilter} used to identify URIs that already have been
     *                       crawled.
     * @param queue          {@link UriQueue} used to manage the URIs that should be
     *                       crawled.
     * @param doesRecrawling used to select if URIs should be recrawled.
     */
    public ExtendedFrontierImpl(KnownUriFilter knownUriFilter, IpAddressBasedQueue queue, boolean doesRecrawling) {
        super(knownUriFilter, queue, doesRecrawling);
    }

    @Override
    public void informAboutDeadWorker(int idOfWorker, List<CrawleableUri> lstUrisToReassign) {
        if (queue instanceof IpAddressBasedQueue) {
            IpAddressBasedQueue ipQueue = (IpAddressBasedQueue) queue;

            Set<InetAddress> setIps = new HashSet<>();
            for (CrawleableUri uri : lstUrisToReassign) {
                InetAddress ip = uri.getIpAddress();
                setIps.add(ip);
            }
            setIps.forEach(ip -> ipQueue.markIpAddressAsAccessible(ip));
        }
    }
}
