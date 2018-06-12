package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Representation of Crawling activity. A crawling activity is started by a single worker. So, it contains a bunch of Uris
 * and some meta data, like timestamps for the start and end of the crawling activity.
 */
public class CrawlingActivity {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlingActivity.class);
    /**
     * A unique id.
     */
    private UUID id;
    /**
     * When the activity has started.
     */
    private Date dateStarted;
    /**
     * When the activity has ended.
     */
    private Date dateEnded;
    /**
     * A mapping from uris to states indicating whether they have been crawled successfully.
     */
    private Map<CrawleableUri, CrawlingURIState> mapUri;
    /**
     * A state of the activity.
     */
    private CrawlingActivityState status;
    /**
     * The worker that has been assigned the activity.
     */
    private Worker worker;
    /**
     * Number of triples crawled by this activity.
     */
    private int numTriples;
    /**
     * The sink used for the activity.
     */
    private Sink sink;

    /**
     * Constructor
     *
     * @param listUri
     * @param worker
     * @param sink
     */
    public CrawlingActivity(List<CrawleableUri> listUri, Worker worker, Sink sink) {
        this.worker = worker;
        this.dateStarted = new Date();
        this.status = CrawlingActivityState.STARTED;
        mapUri = new HashedMap();
        for (CrawleableUri uri : listUri) {
            mapUri.put(uri, CrawlingURIState.UNKNOWN);
        }
        id = UUID.randomUUID();
        this.sink = sink;
    }

    public Map<CrawleableUri, CrawlingURIState> getMapUri() {
        return mapUri;
    }

    public int getNumTriples() {
        return numTriples;
    }

    public void setState(CrawleableUri uri, CrawlingURIState state) {

    }

    public void finishActivity() {
        countTriples();
        dateEnded = new Date();
        status = CrawlingActivityState.ENDED;
    }

    /**
     * TODO
     * count the triples of the activity.
     */
    private void countTriples() {
        /*int sum = 0;
        if (sink instanceof SparqlBasedSink) {
            for (CrawleableUri uri : mapUri.keySet()) {
                sum += ((SparqlBasedSink) sink).getNumberOfTriplesForGraph(uri);
            }
            numTriples = sum;
        } else {
            numTriples = -1;
        } */
    }

    public UUID getId() {
        return id;
    }

    public String getDateStarted() {
        String dateString = dateStarted.toString();
        dateString = dateString.replace(" ", "_");
        dateString = dateString.replace(":", "_");
        return dateString;
    }

    public String getDateEnded() {

        String dateString = dateEnded.toString();
        dateString = dateString.replace(" ", "_");
        dateString = dateString.replace(":", "_");
        return dateString;
    }

    public CrawlingActivityState getStatus() {
        return status;
    }

    public Worker getWorker() {
        return worker;
    }

    public enum CrawlingURIState {SUCCESSFUL, UNKNOWN, FAILED}

    public enum CrawlingActivityState {STARTED, ENDED, SUCCESSFUL, FAILED}
}
