package org.dice_research.squirrel.frontier.impl;


import de.jungblut.math.DoubleVector;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.filter.KnownUriFilter;
import org.dice_research.squirrel.data.uri.filter.SchemeBasedUriFilter;
import org.dice_research.squirrel.data.uri.filter.UriFilter;
import org.dice_research.squirrel.data.uri.info.URIReferences;
import org.dice_research.squirrel.data.uri.norm.UriNormalizer;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.frontier.Frontier;
import org.dice_research.squirrel.graph.GraphLogger;
import org.dice_research.squirrel.queue.BlockingQueue;
import org.dice_research.squirrel.queue.UriQueue;
import org.dice_research.squirrel.uri.processing.UriProcessor;
import org.dice_research.squirrel.components.FrontierComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dice_research.squirrel.predictor.*;

/**
 * Standard implementation of the {@link Frontier} interface containing a
 * {@link #queue} and a {@link #knownUriFilter}.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class FrontierImpl implements Frontier {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierImpl.class);

    /**
     * {@link UriNormalizer} used to transform given URIs into a normal form.
     */
    protected UriNormalizer normalizer;

    /**
     * {@link KnownUriFilter} used to identify URIs that already have been crawled.
     */
    protected KnownUriFilter knownUriFilter;

    /**
     * {@link org.dice_research.squirrel.data.uri.info.URIReferences} used to
     * identify URIs that already have been crawled.
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
     * Time (in milliseconds) after which uris will be recrawled (only used if no
     * specific time is configured for a URI).
     */
    private static long generalRecrawlTime;

    /**
     * Time interval(in milliseconds) at which the check for outdated uris is
     * performed.
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
     * {@link Predictor Used to predict the type of the URI}
     */
    protected Predictor predictor;


    /**
     * Constructor.
     *
     * @param normalizer
     *            {@link UriNormalizer} used to transform given URIs into a normal
     *            form
     * @param knownUriFilter
     *            {@link UriFilter} used to identify URIs that already have been
     *            crawled.
     * @param queue
     *            {@link UriQueue} used to manage the URIs that should be crawled.
     * @param graphLogger
     *            {@link GraphLogger} used to log graphs.
     * @param doesRecrawling
     *            used to select if URIs should be recrawled.
     * @param generalRecrawlTime
     *            used to select the general Time after URIs should be recrawled. If
     *            Value is null the default Time is used.
     * @param timerPeriod
     *            used to select if URIs should be recrawled.
     * @param predictor
     *            {@link Predictor}Used to predict the type of the URI
     */
    public FrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, UriQueue queue,
            GraphLogger graphLogger, boolean doesRecrawling, long generalRecrawlTime, long timerPeriod, Predictor predictor) {
        this(normalizer, knownUriFilter, null, queue, graphLogger, doesRecrawling, generalRecrawlTime, timerPeriod, predictor);
    }

    /**
     * Constructor.
     *
     * @param normalizer
     *            {@link UriNormalizer} used to transform given URIs into a normal
     *            form
     * @param knownUriFilter
     *            {@link UriFilter} used to identify URIs that already have been
     *            crawled.
     * @param queue
     *            {@link UriQueue} used to manage the URIs that should be crawled.
     * @param doesRecrawling
     *            used to select if URIs should be recrawled.
     * @param generalRecrawlTime
     *            used to select the general Time after URIs should be recrawled. If
     *            Value is null the default Time is used.
     * @param timerPeriod
     *            used to select if URIs should be recrawled.
     * @param predictor
     *            {@link Predictor}Used to predict the type of the URI
     */
    public FrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, UriQueue queue, boolean doesRecrawling,
            long generalRecrawlTime, long timerPeriod, UriHashCustodian uriHashCustodian, Predictor predictor) {
        this(normalizer, knownUriFilter, queue, null, doesRecrawling, generalRecrawlTime, timerPeriod, predictor);
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
     * @param doesRecrawling Value for {@link #doesRecrawling}.
     * @param  predictor     Used to predict the type of the URI.
     */
    public FrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, URIReferences uriReferences, UriQueue queue, boolean doesRecrawling, Predictor predictor) {
        this(normalizer, knownUriFilter, uriReferences, queue, null, doesRecrawling, DEFAULT_GENERAL_RECRAWL_TIME, DEFAULT_TIMER_PERIOD, predictor);
    }

    /**
     * Constructor.
     *
     * @param normalizer
     *            {@link UriNormalizer} used to transform given URIs into a normal
     *            form
     * @param knownUriFilter
     *            {@link UriFilter} used to identify URIs that already have been
     *            crawled.
     * @param queue
     *            {@link UriQueue} used to manage the URIs that should be crawled.
     * @param doesRecrawling
     *            Value for {@link #doesRecrawling}.
     * @param predictor
     *             {@link Predictor}Used to predict the type of the URI
     *
     */
    public FrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, UriQueue queue,
            boolean doesRecrawling, Predictor predictor) {
        this(normalizer, knownUriFilter, queue, null, doesRecrawling, DEFAULT_GENERAL_RECRAWL_TIME,
                DEFAULT_TIMER_PERIOD, predictor);
    }

    /**
     * Constructor.
     *
     * @param normalizer
     *            {@link UriNormalizer} used to transform given URIs into a normal
     *            form
     * @param knownUriFilter
     *            {@link UriFilter} used to identify URIs that already have been
     *            crawled.
     * @param queue
     *            {@link UriQueue} used to manage the URIs that should be crawled.
     * @param predictor
     *            {@link Predictor}Used to predict the type of the URI
     *
     */
    public FrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, UriQueue queue, Predictor predictor) {
        this(normalizer, knownUriFilter, queue, null, false, DEFAULT_GENERAL_RECRAWL_TIME, DEFAULT_TIMER_PERIOD, predictor);
    }

    /**
     * Constructor.
     *
     * @param normalizer
     *            {@link UriNormalizer} used to transform given URIs into a normal
     *            form
     * @param knownUriFilter
     *            {@link UriFilter} used to identify URIs that already have been
     *            crawled.
     * @param uriReferences
     *            {@link URIReferences} used to manage URI references
     * @param queue
     *            {@link UriQueue} used to manage the URIs that should be crawled.
     * @param graphLogger
     *            {@link GraphLogger} used to log graphs.
     * @param doesRecrawling
     *            used to select if URIs should be recrawled.
     * @param generalRecrawlTime
     *            used to select the general Time after URIs should be recrawled. If
     *            Value is null the default Time is used.
     * @param timerPeriod
     *            used to select if URIs should be recrawled.
     * @param predictor
     *            {@link Predictor}Used to predict the type of the URI
     */
    public FrontierImpl(UriNormalizer normalizer, KnownUriFilter knownUriFilter, URIReferences uriReferences,
            UriQueue queue, GraphLogger graphLogger, boolean doesRecrawling, long generalRecrawlTime,
            long timerPeriod, Predictor predictor) {
        this.normalizer = normalizer;
        this.knownUriFilter = knownUriFilter;
        this.uriReferences = uriReferences;
        this.queue = queue;
        this.uriProcessor = new UriProcessor();
        this.graphLogger = graphLogger;

        this.queue.open();
        this.doesRecrawling = doesRecrawling;
        this.timerPeriod = timerPeriod;
        this.predictor = predictor;
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

        // if(terminationCheck.shouldFrontierTerminate(this)) {
        // LOGGER.error("FRONTIER IS TERMINATING!", new Exception());
        // }

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
        // Normalize the URI
        uri = normalizer.normalize(uri);
        // Predict the URI type
        if(predictor != null && uri.getType().equals("UNKNOWN")) {
            try {
                //predict and update uri key with the predicted class
                String p = predictor.predict(uri);
                uri.addData(Constants.URI_PREDICTED_LABEL, p);
            } catch (Exception e) {
                LOGGER.info("Exception happened while predicting", e);
            }
        }
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
                knownUriFilter.add(uri, System.currentTimeMillis());
            } else {
                LOGGER.warn("addNewUri(" + uri + "): " + uri.getUri().getScheme() + " is not supported, only "
                        + schemeUriFilter.getSchemes() + ". Will not added!");
            }

        } else {
            LOGGER.debug("addNewUri(" + uri + "): URI is not good [" + knownUriFilter + "]. Will not be added!");
        }
    }


    @Override
    public void crawlingDone(List<CrawleableUri> uris) {
        LOGGER.info("One worker finished his work and crawled " + uris.size() + " URIs.");



//        List<CrawleableUri> newUris = new ArrayList<>(uriMap.size());
//        for (CrawleableUri uri : uriMap.keySet()) {
//            newUris.addAll(uriMap.get(uri));
//            knownUriFilter.add(uri, System.currentTimeMillis(), uri.getTimestampNextCrawl());
//            if (uriReferences != null) {
//                uriReferences.add(uri, uriMap.get(uri));
//            }
//        }

//        // If there is a graph logger, log the data
//        if (graphLogger != null) {
//            graphLogger.log(new ArrayList<>(uriMap.keySet()), newUris);
//        }

        // If we should give the crawled IPs to the queue
        if (queue instanceof BlockingQueue) {
            ((BlockingQueue<?>) queue).markUrisAsAccessible(uris);
        }
        // send list of crawled URIs to the knownUriFilter
        for (CrawleableUri uri : uris) {
            Long recrawlOn = (Long) uri.getData(Constants.URI_PREFERRED_RECRAWL_ON);
            // If a recrawling is defined, check whether we can directly add it back to the
            // queue
            if ((recrawlOn != null) && (recrawlOn < System.currentTimeMillis())) {
                // Create a new uri object reusing only meta data that is useful
                CrawleableUri recrawlUri = new CrawleableUri(uri.getUri(), uri.getIpAddress());
                recrawlUri.addData(Constants.URI_TYPE_KEY, uri.getData(Constants.URI_TYPE_KEY));
                addNewUri(recrawlUri);
            } else {
                knownUriFilter.add(uri, System.currentTimeMillis());
            }
        }

        // Update the URI type prediction model
        try {
            for (CrawleableUri uri : uris) {
                predictor.weightUpdate(uri);
            }
        } catch (Exception e) {
            LOGGER.warn("Exception happened while updating the weights for the URI type predictor model",e);
        }

    }

    @Override
    public int getNumberOfPendingUris() {
        // TODO this implementation does not fit to the semantics of the method name
        // since it returns the number of URI groups instead of the number of URIs
        if (queue instanceof BlockingQueue) {
            return ((BlockingQueue<?>) queue).getNumberOfBlockedKeys();
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

