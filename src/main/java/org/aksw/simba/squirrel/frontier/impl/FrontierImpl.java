package org.aksw.simba.squirrel.frontier.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.aksw.simba.squirrel.Constants;
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
     * @param doesRecrawling     used to select if URIs should be recrawled.
     * @param generalRecrawlTime used to select the general Time after URIs should be recrawled. If Value is null the default Time is used.
     * @param timerPeriod        used to select if URIs should be recrawled.
     */
    public FrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue, boolean doesRecrawling, long generalRecrawlTime, long timerPeriod) {
        this(knownUriFilter, queue, null, doesRecrawling, generalRecrawlTime, timerPeriod);
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
        this(knownUriFilter, queue, doesRecrawling, DEFAULT_GENERAL_RECRAWL_TIME, DEFAULT_TIMER_PERIOD);
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
        this(knownUriFilter, queue, false, DEFAULT_GENERAL_RECRAWL_TIME, DEFAULT_TIMER_PERIOD);
    }


    /**
     * Constructor.
     *
     * @param knownUriFilter {@link UriFilter} used to identify URIs that already have been
     *                       crawled.
     * @param queue          {@link UriQueue} used to manage the URIs that should be
     *                       crawled.
     * @param graphLogger    {@link GraphLogger} used to log graphs.
     * @param doesRecrawling used to select if URIs should be recrawled.
     * @param generalRecrawlTime used to select the general Time after URIs should be recrawled. If Value is null the default Time is used.
     * @param timerPeriod        used to select if URIs should be recrawled.
     */
    public FrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue, GraphLogger graphLogger, boolean doesRecrawling, long generalRecrawlTime, long timerPeriod) {
        this.knownUriFilter = knownUriFilter;
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

    	
    	boolean addErrorCheck = true;
    	

        if (knownUriFilter.isUriGood(uri) && schemeUriFilter.isUriGood(uri)) {
            // Make sure that the IP is known
            try {
                uri = this.uriProcessor.recognizeInetAddress(uri);
            } catch (UnknownHostException e) {
                LOGGER.error("Could not recognize IP for {}, unknown host", uri.getUri());
            }
            if (uri.getIpAddress() != null) {

            	while(addErrorCheck) {
	            	try {
	            		queue.addUri(this.uriProcessor.recognizeUriType(uri));
	            		addErrorCheck = false;
	            	}catch(Exception e) {
	            		LOGGER.warn(e.getMessage());
	            	}
            	}

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
        for (CrawleableUri uri : crawledUris) {
            Long recrawlOn = (Long) uri.getData(Constants.URI_PREFERRED_RECRAWL_ON);
            // If a recrawling is defined, check whether we can directly add it back to the queue
            if((recrawlOn != null) && (recrawlOn < System.currentTimeMillis())) {
                // Create a new uri object reusing only meta data that is useful
                CrawleableUri recrawlUri = new CrawleableUri(uri.getUri(), uri.getIpAddress());
                recrawlUri.addData(Constants.URI_TYPE_KEY, uri.getData(Constants.URI_TYPE_KEY));
                addNewUri(recrawlUri);
            } else {
                // send list of crawled URIs to the knownUriFilter
                knownUriFilter.add(uri, uri.getTimestampNextCrawl());
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

