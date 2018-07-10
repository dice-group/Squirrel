package org.aksw.simba.squirrel.frontier.impl;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.SchemeBasedUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.UriFilter;
import org.aksw.simba.squirrel.data.uri.info.URIReferences;
import org.aksw.simba.squirrel.deduplication.hashing.UriHashCustodian;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.graph.GraphLogger;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.queue.UriQueue;
import org.aksw.simba.squirrel.uri.processing.UriProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Standard implementation of the {@link Frontier} interface containing a
 * {@link #queue} and a {@link #knownUriFilter}.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class FrontierImpl implements Frontier {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierImpl.class);

    /**
     * {@link KnownUriFilter} used to identify URIs that already have been
     * crawled.
     */
    protected KnownUriFilter knownUriFilter;

    /**
     * {@link org.aksw.simba.squirrel.data.uri.info.URIReferences} used to identify URIs that already have been
     * crawled.
     */
    protected URIReferences uriReferences = null;

    /**
     * {@link SchemeBasedUriFilter} used to identify URIs with known protocol.
     */
    protected SchemeBasedUriFilter schemeUriFilter = new SchemeBasedUriFilter();
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
     * Indicates whether recrawling is active.
     */
    private boolean doesRecrawling;

    /**
     * The timer that schedules the recrawling.
     */
    private Timer timerRecrawling;

    /**
     * Time (in milliseconds) after which uris will be recrawled (only used if no specific time is configured for a URI).
     */
    private static long generalRecrawlTime;

    /**
     * Time interval(in milliseconds) at which the check for outdated uris is performed.
     */
    private long timerPeriod;

    /**
     * Default value for {@link #generalRecrawlTime} (one week).
     */
    public static final long DEFAULT_GENERAL_RECRAWL_TIME = 1000 * 60 * 60 * 24 * 7;

    /**
     * Default value for {@link #timerPeriod}.
     */
    private static final long DEFAULT_TIMER_PERIOD = 1000 * 60 * 60;

    /**
     * Constructor.
     *
     * @param knownUriFilter     {@link UriFilter} used to identify URIs that already have been
     *                           crawled.
     * @param queue              {@link UriQueue} used to manage the URIs that should be
     *                           crawled.
     * @param graphLogger        {@link GraphLogger} used to log graphs.
     * @param doesRecrawling     used to select if URIs should be recrawled.
     * @param generalRecrawlTime used to select the general Time after URIs should be recrawled. If Value is null the default Time is used.
     * @param timerPeriod        used to select if URIs should be recrawled.
     */
    public FrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue, GraphLogger graphLogger, boolean doesRecrawling, long generalRecrawlTime, long timerPeriod) {
        this(knownUriFilter, null, queue, graphLogger, doesRecrawling, generalRecrawlTime, timerPeriod);
    }
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
    public FrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue, boolean doesRecrawling, long generalRecrawlTime, long timerPeriod, UriHashCustodian uriHashCustodian) {
        this(knownUriFilter, queue, null, doesRecrawling, generalRecrawlTime, timerPeriod);
    }

    /**
     * Constructor.
     *
     * @param knownUriFilter {@link UriFilter} used to identify URIs that already have been
     *                       crawled.
     * @param uriReferences  {@link URIReferences} used to manage URI references
     * @param queue          {@link UriQueue} used to manage the URIs that should be
     *                       crawled.
     * @param doesRecrawling Value for {@link #doesRecrawling}.
     */
    public FrontierImpl(KnownUriFilter knownUriFilter, URIReferences uriReferences, UriQueue queue, boolean doesRecrawling) {
        this(knownUriFilter, uriReferences, queue, null, doesRecrawling, DEFAULT_GENERAL_RECRAWL_TIME, DEFAULT_TIMER_PERIOD);
    }

    /**
     * Constructor.
     *
     * @param knownUriFilter {@link UriFilter} used to identify URIs that already have been
     *                       crawled.
     * @param queue          {@link UriQueue} used to manage the URIs that should be
     *                       crawled.
     * @param doesRecrawling Value for {@link #doesRecrawling}.
     */
    public FrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue, boolean doesRecrawling) {
        this(knownUriFilter, queue, null, doesRecrawling, DEFAULT_GENERAL_RECRAWL_TIME, DEFAULT_TIMER_PERIOD);
    }

    /**
     * Constructor.
     *
     * @param knownUriFilter {@link UriFilter} used to identify URIs that already have been
     *                       crawled.
     * @param queue          {@link UriQueue} used to manage the URIs that should be
     *                       crawled.
     */
    public FrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue) {
        this(knownUriFilter, queue, null, false, DEFAULT_GENERAL_RECRAWL_TIME, DEFAULT_TIMER_PERIOD);
    }


    /**
     * Constructor.
     *
     * @param knownUriFilter {@link UriFilter} used to identify URIs that already have been
     *                       crawled.
     * @param uriReferences  {@link URIReferences} used to manage URI references
     * @param queue          {@link UriQueue} used to manage the URIs that should be
     *                       crawled.
     * @param graphLogger    {@link GraphLogger} used to log graphs.
     * @param doesRecrawling used to select if URIs should be recrawled.
     * @param generalRecrawlTime used to select the general Time after URIs should be recrawled. If Value is null the default Time is used.
     * @param timerPeriod        used to select if URIs should be recrawled.
     */
    public FrontierImpl(KnownUriFilter knownUriFilter, URIReferences uriReferences, UriQueue queue, GraphLogger graphLogger, boolean doesRecrawling, long generalRecrawlTime, long timerPeriod) {
        this.knownUriFilter = knownUriFilter;
        this.uriReferences = uriReferences;
        this.queue = queue;
        this.uriProcessor = new UriProcessor();
        this.graphLogger = graphLogger;

        this.queue.open();
        this.knownUriFilter.open();
        this.doesRecrawling = doesRecrawling;
        this.timerPeriod = timerPeriod;
        FrontierImpl.generalRecrawlTime = generalRecrawlTime;

        if (this.doesRecrawling) {
            timerRecrawling = new Timer();
            timerRecrawling.schedule(new TimerTask() {
                @Override
                public void run() {
                    List<CrawleableUri> urisToRecrawl = knownUriFilter.getOutdatedUris();
                    urisToRecrawl.forEach(uri -> queue.addUri(uriProcessor.recognizeUriType(uri)));
                }
            }, this.timerPeriod, this.timerPeriod);
        }
    }


    @Override
    public List<CrawleableUri> getNextUris() {
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
        if (knownUriFilter.isUriGood(uri)) {
            LOGGER.debug("addNewUri(" + uri + "): URI is good [" + knownUriFilter + "]");
            if (schemeUriFilter.isUriGood(uri)) {
                LOGGER.trace("addNewUri(" + uri.getUri() + "): URI schemes is OK [" + schemeUriFilter + "]");
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
            } else {
                LOGGER.warn("addNewUri(" + uri + "): " + uri.getUri().getScheme() + " is not supported, only " + schemeUriFilter.getSchemes() + ". Will not added!");
            }
        } else {
            LOGGER.info("addNewUri(" + uri + "): URI is not good [" + knownUriFilter + "]. Will not added!");
        }
    }

    @Override
    public void crawlingDone(Dictionary<CrawleableUri, List<CrawleableUri>> uriMap) {
        LOGGER.info("One worker finished his work and crawled " + uriMap.size() + " URIs.");

        List<CrawleableUri> crawledUris = Collections.list(uriMap.keys());

        List<CrawleableUri> newUris = new ArrayList<>(uriMap.size());
        Enumeration<CrawleableUri> newUrisEnumeration = uriMap.keys();
        while (newUrisEnumeration.hasMoreElements()) {
            CrawleableUri uri = newUrisEnumeration.nextElement();
            newUris.addAll(uriMap.get(uri));
            knownUriFilter.add(uri, System.currentTimeMillis(), uri.getTimestampNextCrawl());
            if (uriReferences != null) {
                uriReferences.add(uri, uriMap.get(uri));
            }
        }

        // If there is a graph logger, log the data
        if (graphLogger != null) {
            graphLogger.log(crawledUris, newUris);
        }
        // If we should give the crawled IPs to the queue
        if (queue instanceof IpAddressBasedQueue) {
            Set<InetAddress> ips = new HashSet<>();
            InetAddress ip;
            for (CrawleableUri uri : crawledUris) {
                ip = uri.getIpAddress();
                if (ip != null) {
                    ips.add(ip);
                }
            }
            ips.forEach(_ip -> ((IpAddressBasedQueue) queue).markIpAddressAsAccessible(_ip));
        }
        // send list of crawled URIs to the knownUriFilter
        for (CrawleableUri uri : crawledUris) {
            Long recrawlOn = (Long) uri.getData(Constants.URI_PREFERRED_RECRAWL_ON);
            // If a recrawling is defined, check whether we can directly add it back to the queue
            if ((recrawlOn != null) && (recrawlOn < System.currentTimeMillis())) {
                // Create a new uri object reusing only meta data that is useful
                CrawleableUri recrawlUri = new CrawleableUri(uri.getUri(), uri.getIpAddress());
                recrawlUri.addData(Constants.URI_TYPE_KEY, uri.getData(Constants.URI_TYPE_KEY));
                addNewUri(recrawlUri);
            }
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
    public boolean doesRecrawling() {
        return doesRecrawling;
    }

    @Override
    public void close() {
        timerRecrawling.cancel();
    }


    public static long getGeneralRecrawlTime() {
        return generalRecrawlTime;
    }

    /**
     * Getter for the {@link #queue}.
     *
     * @return The waiting queue for the URIs.
     */
    public UriQueue getQueue() {
        return queue;
    }
}

