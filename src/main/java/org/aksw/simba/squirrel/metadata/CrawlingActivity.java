package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.sink.tripleBased.TripleBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Representation of Crawling activity. A crawling activity is started by a single worker, and sends this data to the sink. So, it contains a bunch of Uris
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
        this.dateStarted = new Date();
        this.uri = uri;
        this.state = CrawlingURIState.UNKNOWN;
        if (sink instanceof SparqlBasedSink) {
            graphId = ((SparqlBasedSink) sink).getGraphId(uri);
            hostedOn = ((SparqlBasedSink) sink).getUpdateDatasetURI();
        }
        id = "activity:" + uri.getData(CrawleableUri.UUID_KEY);
        this.sink = sink;
    }

    public void setState(CrawlingURIState state) {
        this.state = state;
    }

    /**
     * Finish the crawling activity and send data to sink
     */
    public void finishActivity() {
        dateEnded = new Date();
        prepareDataAndSendToSink();
    }

    /**
     * Prepare the metadata and send it to the sink.
     */
    public void prepareDataAndSendToSink() {
        Model model = ModelFactory.createDefaultModel();
        Resource activity = ResourceFactory.createResource(getId());
        Resource resultGraph = ResourceFactory.createResource(getGraphId());
        Resource crawledUri = ResourceFactory.createResource(getCrawleableUri().getUri().toString());

        model.add(activity, MetaDataVocabulary.status, model.createTypedLiteral(getState().toString()));
        model.add(activity, MetaDataVocabulary.startedAtTime, model.createTypedLiteral(getDateStarted()));
        model.add(activity, MetaDataVocabulary.endedAtTime, model.createTypedLiteral(getDateEnded()));
        model.add(activity, MetaDataVocabulary.wasAssociatedWith, model.createTypedLiteral(getWorker().getId()));
        if (getSink() instanceof SparqlBasedSink) {
            model.add(activity, MetaDataVocabulary.hostedOn, model.createTypedLiteral(getHostedOn()));
        }
        model.add(resultGraph, MetaDataVocabulary.wasGeneratedBy, activity);
        model.add(resultGraph, MetaDataVocabulary.uriName, crawledUri);

        getSink().addMetaData(model);

    }


    public String getId() {
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


    public Worker getWorker() {
        return worker;
    }

    public enum CrawlingURIState {SUCCESSFUL, UNKNOWN, FAILED;}

    public CrawlingURIState getState() {
        return state;
    }

    public CrawleableUri getCrawleableUri() {
        return uri;
    }

    public String getGraphId() {
        return graphId;
    }

    public String getHostedOn() {
        return hostedOn;
    }

    public Sink getSink() {
        return sink;
    }
}
