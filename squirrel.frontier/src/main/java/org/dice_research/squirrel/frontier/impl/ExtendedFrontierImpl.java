package org.dice_research.squirrel.frontier.impl;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.filter.UriFilter;
import org.dice_research.squirrel.data.uri.filter.UriFilterComposer;
import org.dice_research.squirrel.data.uri.info.URIReferences;
import org.dice_research.squirrel.data.uri.norm.UriGenerator;
import org.dice_research.squirrel.data.uri.norm.UriNormalizer;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.frontier.ExtendedFrontier;
import org.dice_research.squirrel.frontier.recrawling.OutDatedUriRetriever;
import org.dice_research.squirrel.queue.IpAddressBasedQueue;
import org.dice_research.squirrel.queue.UriQueue;

@SuppressWarnings("deprecation")
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

    public ExtendedFrontierImpl(UriNormalizer normalizer, UriFilterComposer relationalUriFilter, UriQueue queue,List<UriGenerator> uriGenerators, boolean doesRecrawling,
                                long generalRecrawlTime, long timerPeriod, UriHashCustodian uriHashCustodian) {
        super(normalizer, relationalUriFilter, queue, uriGenerators,doesRecrawling, generalRecrawlTime, timerPeriod);
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

    public ExtendedFrontierImpl(UriNormalizer normalizer, UriFilterComposer relationalUriFilter, IpAddressBasedQueue queue, List<UriGenerator> uriGenerators, boolean doesRecrawling) {
        super(normalizer, relationalUriFilter, queue, uriGenerators, doesRecrawling);
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
    public ExtendedFrontierImpl(UriNormalizer normalizer, UriFilterComposer relationalUriFilter, URIReferences uriReferences, UriQueue queue,List<UriGenerator> uriGenerators, boolean doesRecrawling,
    		OutDatedUriRetriever outDatedUriRetriever) {
        super(normalizer, relationalUriFilter, uriReferences, queue,uriGenerators, doesRecrawling,outDatedUriRetriever);
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
