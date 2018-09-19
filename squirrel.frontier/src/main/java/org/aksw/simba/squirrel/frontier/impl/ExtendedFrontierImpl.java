package org.aksw.simba.squirrel.frontier.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.UriFilter;
import org.aksw.simba.squirrel.data.uri.info.URIReferences;
import org.aksw.simba.squirrel.deduplication.hashing.UriHashCustodian;
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
     * @param uriHashCustodian   used to access and write hash values for uris.
     */
    @SuppressWarnings("unused")
    public ExtendedFrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue, boolean doesRecrawling,
                                long generalRecrawlTime, long timerPeriod, UriHashCustodian uriHashCustodian) {
        super(knownUriFilter, queue, doesRecrawling, generalRecrawlTime, timerPeriod, uriHashCustodian);
    }

    /**
     * Constructor.
     *
     * @param knownUriFilter   {@link UriFilter} used to identify URIs that already have been
     *                         crawled.
     * @param queue            {@link UriQueue} used to manage the URIs that should be
     *                         crawled.
     * @param doesRecrawling   used to select if URIs should be recrawled.
     */
    public ExtendedFrontierImpl(KnownUriFilter knownUriFilter, IpAddressBasedQueue queue, boolean doesRecrawling) {
        super(knownUriFilter, queue, doesRecrawling);
    }

    /**
     * Constructor.
     *
     * @param knownUriFilter {@link UriFilter} used to identify URIs that already have been
     *                       crawled.
     * @param uriReferences  {@link URIReferences} used to manage URI references
     * @param queue          {@link UriQueue} used to manage the URIs that should be
     *                       crawled.
     * @param doesRecrawling used to select if URIs should be recrawled.
     */
    public ExtendedFrontierImpl(KnownUriFilter knownUriFilter, URIReferences uriReferences, IpAddressBasedQueue queue, boolean doesRecrawling) {
        super(knownUriFilter, uriReferences, queue, doesRecrawling);
    }

    @Override
    public void informAboutDeadWorker(String idOfWorker, List<CrawleableUri> lstUrisToReassign) {
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