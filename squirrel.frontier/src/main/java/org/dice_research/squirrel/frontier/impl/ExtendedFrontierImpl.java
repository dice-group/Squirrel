package org.dice_research.squirrel.frontier.impl;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.filter.KnownOutDatedUriFilter;
import org.dice_research.squirrel.data.uri.filter.KnownUriFilter;
import org.dice_research.squirrel.data.uri.filter.OutDatedUris;
import org.dice_research.squirrel.data.uri.filter.UriFilter;
import org.dice_research.squirrel.data.uri.info.URIReferences;
import org.dice_research.squirrel.data.uri.norm.UriNormalizer;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.frontier.ExtendedFrontier;
import org.dice_research.squirrel.queue.IpAddressBasedQueue;
import org.dice_research.squirrel.queue.UriQueue;

public class ExtendedFrontierImpl extends FrontierImpl implements ExtendedFrontier {

    /**
     * Constructor.
     *
     * @param normalizer     {@link UriNormalizer} used to transform given URIs into a normal form
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
    public ExtendedFrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, UriQueue queue, boolean doesRecrawling,
<<<<<<< HEAD
                                long generalRecrawlTime, long timerPeriod, UriHashCustodian uriHashCustodian, KnownOutDatedUriFilter knownOutDatedUriFilter) {
        super(normalizer, knownUriFilter, queue, doesRecrawling, generalRecrawlTime, timerPeriod, uriHashCustodian, knownOutDatedUriFilter);
=======
                                long generalRecrawlTime, long timerPeriod, UriHashCustodian uriHashCustodian, OutDatedUris outDatedUris) {
        super(normalizer, knownUriFilter, queue, doesRecrawling, generalRecrawlTime, timerPeriod, uriHashCustodian, outDatedUris);
>>>>>>> bb00ad4b8e0cfdb89738f43afc01ce482e016bd6
    }

    /**
     * Constructor.
     *
     * @param normalizer     {@link UriNormalizer} used to transform given URIs into a normal form
     * @param knownUriFilter   {@link UriFilter} used to identify URIs that already have been
     *                         crawled.
     * @param queue            {@link UriQueue} used to manage the URIs that should be
     *                         crawled.
     * @param doesRecrawling   used to select if URIs should be recrawled.
     */
<<<<<<< HEAD
    public ExtendedFrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, IpAddressBasedQueue queue, boolean doesRecrawling, KnownOutDatedUriFilter knownOutDatedUriFilter) {
=======
    public ExtendedFrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, IpAddressBasedQueue queue, boolean doesRecrawling, OutDatedUris knownOutDatedUriFilter) {
>>>>>>> bb00ad4b8e0cfdb89738f43afc01ce482e016bd6
        super(normalizer,  knownUriFilter, queue, doesRecrawling, knownOutDatedUriFilter);
    }

    /**
     * Constructor.
     *
     * @param normalizer     {@link UriNormalizer} used to transform given URIs into a normal form
     * @param knownUriFilter {@link UriFilter} used to identify URIs that already have been
     *                       crawled.
     * @param uriReferences  {@link URIReferences} used to manage URI references
     * @param queue          {@link UriQueue} used to manage the URIs that should be
     *                       crawled.
     * @param doesRecrawling used to select if URIs should be recrawled.
     */
<<<<<<< HEAD
    public ExtendedFrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, URIReferences uriReferences, UriQueue queue, boolean doesRecrawling, KnownOutDatedUriFilter knownOutDatedUriFilter) {
=======
    public ExtendedFrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, URIReferences uriReferences, UriQueue queue, boolean doesRecrawling, OutDatedUris knownOutDatedUriFilter) {
>>>>>>> bb00ad4b8e0cfdb89738f43afc01ce482e016bd6
        super(normalizer, knownUriFilter, uriReferences, queue, doesRecrawling,knownOutDatedUriFilter);
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
