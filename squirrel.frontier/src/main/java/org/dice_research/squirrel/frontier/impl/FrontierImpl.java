package org.dice_research.squirrel.frontier.impl;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.filter.KnownUriFilter;
import org.dice_research.squirrel.data.uri.filter.SchemeBasedUriFilter;
import org.dice_research.squirrel.data.uri.filter.UriFilter;
import org.dice_research.squirrel.data.uri.filter.UriFilterComposer;
import org.dice_research.squirrel.data.uri.info.URIReferences;
import org.dice_research.squirrel.data.uri.norm.UriGenerator;
import org.dice_research.squirrel.data.uri.norm.UriNormalizer;
import org.dice_research.squirrel.frontier.Frontier;
import org.dice_research.squirrel.frontier.recrawling.OutDatedUriRetriever;
import org.dice_research.squirrel.graph.GraphLogger;
import org.dice_research.squirrel.queue.BlockingQueue;
import org.dice_research.squirrel.queue.UriQueue;
import org.dice_research.squirrel.uri.processing.UriProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard implementation of the {@link Frontier} interface containing a
 * {@link #queue} and a {@link #uriFilter}.
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
	protected UriFilterComposer uriFilter;
	
	/**
     * {@link OutDatedUriRetriever} used to collect all the outdated URIs (URIs crawled a week ago) to recrawl.
     */
    protected OutDatedUriRetriever outDatedUriRetriever;

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
	 * {@link UriGenerator} used to generate additional domain variants of a URI
	 */
	protected List<UriGenerator> uriGenerator;
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
	 * Constructor.
	 *
	 * @param normalizer         {@link UriNormalizer} used to transform given URIs
	 *                           into a normal form
	 * @param knownUriFilter     {@link UriFilter} used to identify URIs that
	 *                           already have been crawled.
	 * @param queue              {@link UriQueue} used to manage the URIs that
	 *                           should be crawled.
	 * @param graphLogger        {@link GraphLogger} used to log graphs.
	 * @param doesRecrawling     used to select if URIs should be recrawled.
	 * @param generalRecrawlTime used to select the general Time after URIs should
	 *                           be recrawled. If Value is null the default Time is
	 *                           used.
	 * @param timerPeriod        used to select if URIs should be recrawled.
	 */
	public FrontierImpl(UriNormalizer normalizer, UriFilterComposer relationalUriFilter, UriQueue queue,
			List<UriGenerator> uriGenerators, GraphLogger graphLogger, boolean doesRecrawling, long generalRecrawlTime,
			long timerPeriod) {
		this(normalizer, relationalUriFilter, null, queue, uriGenerators, graphLogger, doesRecrawling,
				generalRecrawlTime, timerPeriod,null);
	}

	/**
	 * Constructor.
	 *
	 * @param normalizer         {@link UriNormalizer} used to transform given URIs
	 *                           into a normal form
	 * @param knownUriFilter     {@link UriFilter} used to identify URIs that
	 *                           already have been crawled.
	 * @param queue              {@link UriQueue} used to manage the URIs that
	 *                           should be crawled.
	 * @param doesRecrawling     used to select if URIs should be recrawled.
	 * @param generalRecrawlTime used to select the general Time after URIs should
	 *                           be recrawled. If Value is null the default Time is
	 *                           used.
	 * @param timerPeriod        used to select if URIs should be recrawled.
	 */
	public FrontierImpl(UriNormalizer normalizer, UriFilterComposer relationalUriFilter, UriQueue queue,
			List<UriGenerator> uriGenerators, boolean doesRecrawling, long generalRecrawlTime, long timerPeriod) {
		this(normalizer, relationalUriFilter, queue, uriGenerators, null, doesRecrawling, generalRecrawlTime,
				timerPeriod);
	}

	/**
	 * Constructor.
	 *
	 * @param normalizer     {@link UriNormalizer} used to transform given URIs into
	 *                       a normal form
	 * @param knownUriFilter {@link UriFilter} used to identify URIs that already
	 *                       have been crawled.
	 * @param uriReferences  {@link URIReferences} used to manage URI references
	 * @param queue          {@link UriQueue} used to manage the URIs that should be
	 *                       crawled.
	 * @param doesRecrawling Value for {@link #doesRecrawling}.
	 */
	public FrontierImpl(UriNormalizer normalizer, UriFilterComposer relationalUriFilter, URIReferences uriReferences,
			UriQueue queue, List<UriGenerator> uriGenerators,boolean doesRecrawling, OutDatedUriRetriever outDatedUriRetriever) {
		this(normalizer, relationalUriFilter, uriReferences, queue, uriGenerators, null, doesRecrawling,
				DEFAULT_GENERAL_RECRAWL_TIME, DEFAULT_TIMER_PERIOD,outDatedUriRetriever);
	}

	/**
	 * Constructor.
	 *
	 * @param normalizer     {@link UriNormalizer} used to transform given URIs into
	 *                       a normal form
	 * @param knownUriFilter {@link UriFilter} used to identify URIs that already
	 *                       have been crawled.
	 * @param queue          {@link UriQueue} used to manage the URIs that should be
	 *                       crawled.
	 * @param doesRecrawling Value for {@link #doesRecrawling}.
	 */
	public FrontierImpl(UriNormalizer normalizer, UriFilterComposer relationalUriFilter, UriQueue queue,
			List<UriGenerator> uriGenerators, boolean doesRecrawling) {
		this(normalizer, relationalUriFilter, queue, uriGenerators, null, doesRecrawling, DEFAULT_GENERAL_RECRAWL_TIME,
				DEFAULT_TIMER_PERIOD);
	}

	/**
	 * Constructor.
	 *
	 * @param normalizer     {@link UriNormalizer} used to transform given URIs into
	 *                       a normal form
	 * @param knownUriFilter {@link UriFilter} used to identify URIs that already
	 *                       have been crawled.
	 * @param queue          {@link UriQueue} used to manage the URIs that should be
	 *                       crawled.
	 */
	public FrontierImpl(UriNormalizer normalizer, UriFilterComposer relationalUriFilter, UriQueue queue,
			List<UriGenerator> uriGenerators) {
		this(normalizer, relationalUriFilter, queue, uriGenerators, null, false, DEFAULT_GENERAL_RECRAWL_TIME,
				DEFAULT_TIMER_PERIOD);
	}

	/**
	 * Constructor.
	 *
	 * @param normalizer         {@link UriNormalizer} used to transform given URIs
	 *                           into a normal form
	 * @param knownUriFilter     {@link UriFilter} used to identify URIs that
	 *                           already have been crawled.
	 * @param uriReferences      {@link URIReferences} used to manage URI references
	 * @param queue              {@link UriQueue} used to manage the URIs that
	 *                           should be crawled.
	 * @param graphLogger        {@link GraphLogger} used to log graphs.
	 * @param doesRecrawling     used to select if URIs should be recrawled.
	 * @param generalRecrawlTime used to select the general Time after URIs should
	 *                           be recrawled. If Value is null the default Time is
	 *                           used.
	 * @param timerPeriod        used to select if URIs should be recrawled.
	 */
	public FrontierImpl(UriNormalizer normalizer, UriFilterComposer relationalUriFilter, URIReferences uriReferences,
			UriQueue queue, List<UriGenerator> uriGenerators, GraphLogger graphLogger, boolean doesRecrawling,
			long generalRecrawlTime, long timerPeriod,OutDatedUriRetriever outDatedUriRetriever) {
		this.normalizer = normalizer;
		this.uriFilter = relationalUriFilter;
		this.uriReferences = uriReferences;
		this.uriGenerator = uriGenerators;
		this.queue = queue;
		this.uriProcessor = new UriProcessor();
		this.graphLogger = graphLogger;
		this.outDatedUriRetriever = outDatedUriRetriever;
		this.queue.open();
		this.doesRecrawling = doesRecrawling;
		this.timerPeriod = timerPeriod;
		FrontierImpl.generalRecrawlTime = generalRecrawlTime;

		if (this.doesRecrawling) {
			timerRecrawling = new Timer();
			timerRecrawling.schedule(new TimerTask() {
				@Override
				public void run() {
					List<CrawleableUri> urisToRecrawl = outDatedUriRetriever.getUriToRecrawl();
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
		// After knownUriFilter uri should be classified according to
		// UriProcessor
		uri = normalizer.normalize(uri);
		addNormalizedUri(uri);

		try {
			for (UriGenerator u : uriGenerator) {
				if (u.getUriVariant(uri) != null)
					addNormalizedUri(normalizer.normalize(u.getUriVariant(uri)));
			}
		} catch (Exception e) {
			LOGGER.info(
					"Exception happened while generating additional URI variant for URI: " + uri.getUri().toString());
		}
	}

	protected void addNormalizedUri(CrawleableUri uri) {
		CrawleableUri curi = uri;
		if (uriFilter.isUriGood(curi)) {
			LOGGER.debug("addNewUri(" + curi + "): URI is good [" + uriFilter + "]");
			if (schemeUriFilter.isUriGood(curi)) {
				LOGGER.trace("addNewUri(" + curi.getUri() + "): URI schemes is OK [" + schemeUriFilter + "]");
				// Make sure that the IP is known
				try {
					curi = this.uriProcessor.recognizeInetAddress(curi);

				} catch (UnknownHostException e) {
					LOGGER.error("Could not recognize IP for {}, unknown host", curi.getUri());
				}
				if (curi.getIpAddress() != null) {
					queue.addUri(this.uriProcessor.recognizeUriType(curi));
				} else {
					LOGGER.error("Couldn't determine the Inet address of \"{}\". It will be ignored.", curi.getUri());
				}
				uriFilter.getKnownUriFilter().add(curi, System.currentTimeMillis());
			} else {
				LOGGER.warn("addNewUri(" + curi + "): " + curi.getUri().getScheme() + " is not supported, only "
						+ schemeUriFilter.getSchemes() + ". Will not added!");
			}

		} else {
			LOGGER.debug("addNewUri(" + curi + "): URI is not good [" + uriFilter + "]. Will not be added!");
		}
	}

	@Override
	public void crawlingDone(List<CrawleableUri> uris) {
		LOGGER.info("One worker finished his work and crawled " + uris.size() + " URIs.");

		// List<CrawleableUri> newUris = new ArrayList<>(uriMap.size());
		// for (CrawleableUri uri : uriMap.keySet()) {
		// newUris.addAll(uriMap.get(uri));
		// knownUriFilter.add(uri, System.currentTimeMillis(),
		// uri.getTimestampNextCrawl());
		// if (uriReferences != null) {
		// uriReferences.add(uri, uriMap.get(uri));
		// }
		// }

		// // If there is a graph logger, log the data
		// if (graphLogger != null) {
		// graphLogger.log(new ArrayList<>(uriMap.keySet()), newUris);
		// }
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
				uriFilter.getKnownUriFilter().add(uri, System.currentTimeMillis());
			}
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