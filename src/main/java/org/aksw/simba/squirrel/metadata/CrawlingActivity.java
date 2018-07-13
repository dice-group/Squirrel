package org.aksw.simba.squirrel.metadata;


import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.fetcher.http.HTTPFetcher;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.*;


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
    private String id;
    /**
     * When the activity has started.
     */
    private LocalDateTime dateStarted;
    /**
     * When the activity has ended.
     */
    private LocalDateTime dateEnded;

    /**
     * The uri for the crawling activity.
     */
    private CrawleableUri uri;

    /**
     * The graph where the uri is stored.
     */
    private String graphId = null;

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

    private String hostedOn = null;

    /**
     * The sink used for the activity.
     */
    private Sink sink;

    /**
     * Constructor
     *
     * @param uri    the URI, which was crawled
     * @param worker the {@link Worker}, that crawled the URI
     * @param sink   the {@link Sink}, that was used to store the downloaded content from the URI
     */
    public CrawlingActivity(CrawleableUri uri, Worker worker, Sink sink) {
        this.worker = worker;
        this.dateStarted = getLocalDateTime();
        this.uri = uri;
        this.state = CrawlingURIState.UNKNOWN;
        if (sink instanceof SparqlBasedSink) {
            graphId = ((SparqlBasedSink) sink).getGraphId(uri);
            hostedOn = ((SparqlBasedSink) sink).getUpdateDatasetURI();
            id = "crawlingActivity_" + graphId;
        }
        else { id = "crawlingActivity_" + UUID.randomUUID();}
        this.sink = sink;
    }

    public void setState(CrawlingURIState state) {
        this.state = state;
    }

    public void finishActivity() {
        countTriples();
        dateEnded = getLocalDateTime();

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

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }

    public LocalDateTime getdateStarted()
    {
        return dateStarted;
    }
    public LocalDateTime getDateEnded()
    {
        return dateEnded;
    }

    /**
     *@return unique Id
     */

    public String getId() {
        return id;
    }

    public List<String> getclasses()
    {
        List<String> k = new ArrayList<>();
        for (Object object :Object.class.getClasses()) {
            if(object instanceof CrawleableUri && !object.equals(CrawleableUri.class))
            {

                k.add(object.getClass().getSimpleName());
            }

        }
        return k;
    }

    public String getHadPlan()
    {
        ArrayList<String> list = new ArrayList<>();
        for(String o: getclasses())
        {
            list.add(o.toString());

        }
        return list.toString();
    }


    public CrawleableUri getCrawleableUri()
    {
        return uri;
    }

    public String getGraphId(CrawleableUri uri)
    {
        return graphId;

    }

    public int getNumTriples() {
        return numTriples;
    }

    public Worker getWorker() {
        return worker;
    }

    public enum CrawlingURIState {SUCCESSFUL, UNKNOWN, FAILED}

    public CrawlingURIState getState() {
        return state;
    }

    public String geturl (String o)
    {
        String uri = "http://www.example.org/" + o;
        return uri;

    }

    public String getHostedOn() {
        return hostedOn;
    }
}

