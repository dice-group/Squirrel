package org.aksw.simba.squirrel.frontier.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.SchemeBasedUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.UriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.graph.GraphLogger;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.queue.UriQueue;
import org.aksw.simba.squirrel.uri.processing.UriProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Standard implementation of the {@link Frontier} interface containing a
 * {@link #queue} and a {@link #knownUriFilter}.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class FrontierImpl implements Frontier {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierImpl.class);

    /**
     * {@link KnownUriFilter} used to identify URIs that already have been
     * crawled.
     */
    protected KnownUriFilter knownUriFilter;

    /**
     * {@link SchemeBasedUriFilter} used to identify URIs with known protocol.
     */
    protected SchemeBasedUriFilter schemeUriFilter = new SchemeBasedUriFilter();

    public UriQueue getQueue() {
        return queue;
    }

    /**
     * {@link UriQueue} used to manage the URIs that should be crawled.
     */
    protected UriQueue queue;

    /**
     * {@link UriProcessor} used to identify the type of incoming URIs: DUMP,
     * SPARQL, DEREFERENCEABLE or UNKNOWN
     */
    protected UriProcessor uriProcessor;
    /**
     * {@link GraphLogger} that can be added to log the crawled graph.
     */
    protected GraphLogger graphLogger;

    /**
     * Constructor.
     *
     * @param knownUriFilter
     *            {@link UriFilter} used to identify URIs that already have been
     *            crawled.
     * @param queue
     *            {@link UriQueue} used to manage the URIs that should be
     *            crawled.
     */
    public FrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue) {
        this(knownUriFilter, queue, null);
    }

    /**
     * Constructor.
     *
     * @param knownUriFilter
     *            {@link UriFilter} used to identify URIs that already have been
     *            crawled.
     * @param queue
     *            {@link UriQueue} used to manage the URIs that should be
     *            crawled.
     */
    public FrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue, GraphLogger graphLogger) {
        this.knownUriFilter = knownUriFilter;
        this.queue = queue;
        this.uriProcessor = new UriProcessor();
        this.graphLogger = graphLogger;

        this.queue.open();
        this.knownUriFilter.open();
    }

    @Override
    public List<CrawleableUri> getNextUris() {
        LOGGER.info("Klasse: " + queue.getClass());
        return queue.getNextUris();
    }

    @Override
    public void addNewUris(List<CrawleableUri> uris) {
        for (CrawleableUri uri : uris) {
            addNewUri(uri);
        }
    }

    @Override
    public void addNewUri(CrawleableUri uri) {
        // After knownUriFilter uri should be classified according to
        // UriProcessor
        if (knownUriFilter.isUriGood(uri) && schemeUriFilter.isUriGood(uri)) {
            // Make sure that the IP is known
            try {
                uri = this.uriProcessor.recognizeInetAddress(uri);
            } catch (UnknownHostException e) {
                LOGGER.error("Could not recognize IP for {}, unknown host", uri.getUri());
            }
            if (uri.getIpAddress() != null) {
                queue.addUri(this.uriProcessor.recognizeUriType(uri));
            } else {
                LOGGER.error("Couldn't determine the Inet address of \"{}\". It will be ignored.", uri.getUri());
            }
        }
    }

    @Override
    public void crawlingDone(List<CrawleableUri> crawledUris, List<CrawleableUri> newUris) {
        // If there is a graph logger, log the data
        if (graphLogger != null) {
            graphLogger.log(crawledUris, newUris);
        }
        // If we should give the crawled IPs to the queue
        if (queue instanceof IpAddressBasedQueue) {
            Set<InetAddress> ips = new HashSet<InetAddress>();
            InetAddress ip;
            for (CrawleableUri uri : crawledUris) {
                ip = uri.getIpAddress();
                if (ip != null) {
                    ips.add(ip);
                }
            }
            Iterator<InetAddress> iterator = ips.iterator();
            while (iterator.hasNext()) {
                ((IpAddressBasedQueue) queue).markIpAddressAsAccessible(iterator.next());
            }
        }
        // send list of crawled URIs to the knownUriFilter
        for (CrawleableUri uri : crawledUris) {
            knownUriFilter.add(uri);
        }

        // Add the new URIs to the Frontier
        addNewUris(newUris);
    }

    @Override
    public int getNumberOfPendingUris() {
        if (queue instanceof IpAddressBasedQueue) {
            return ((IpAddressBasedQueue) queue).getNumberOfBlockedIps();
        } else {
            return 0;
        }
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

