package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.output.ByteArrayOutputStream;
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
     * The uri for the crawling activity.
     */
    private CrawleableUri uri;

    /**
     * The crawling state of the uri.
     */
    private CrawlingURIState state;

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
     * @param uri
     * @param worker
     * @param sink
     */
    public CrawlingActivity(CrawleableUri uri, Worker worker, Sink sink) {
        this.worker = worker;
        this.dateStarted = new Date();
        this.uri = uri;
        this.state = CrawlingURIState.UNKNOWN;
        id = UUID.randomUUID();
        this.sink = sink;
    }

    public void setState(CrawleableUri uri, CrawlingURIState state) {

    }

    public void finishActivity() {
        countTriples();
        dateEnded = new Date();

    }

    /**
     * count the triples of the activity.
     */
    private void countTriples() {
        int sum = 0;
        if (sink instanceof SparqlBasedSink) {

            //sum += ((SparqlBasedSink) sink).getNumberOfTriplesForGraph(uri);

            numTriples = sum;
        } else {
            numTriples = -1;
        }
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

    public int getNumTriples() {
        return numTriples;
    }

    public Worker getWorker() {
        return worker;
    }

    public enum CrawlingURIState {SUCCESSFUL, UNKNOWN, FAILED;}

    public CrawlingURIState getState() {
        return state;
    }

    public CrawleableUri getUri() {
        return uri;
    }
}
