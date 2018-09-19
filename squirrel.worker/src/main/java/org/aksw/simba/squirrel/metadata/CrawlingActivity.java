package org.aksw.simba.squirrel.metadata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.vocab.PROV_O;
import org.aksw.simba.squirrel.vocab.Squirrel;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * Representation of Crawling activity. A crawling activity is started by a
 * single worker, and sends this data to the sink. So, it contains a bunch of
 * Uris and some meta data, like timestamps for the start and end of the
 * crawling activity.
 */
public class CrawlingActivity {

    // /**
    // * The logger.
    // */
    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(CrawlingActivity.class);
    /**
     * A unique id.
     */
    private String activityUri;
    /**
     * When the activity has started.
     */
    private Calendar dateStarted;
    /**
     * When the activity has ended.
     */
    private Calendar dateEnded;

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

    private List<String> steps = new ArrayList<String>();

    /**
     * Constructor
     *
     * @param uri
     *            the URI, which was crawled
     * @param worker
     *            the {@link Worker}, that crawled the URI
     * @param sink
     *            the {@link Sink}, that was used to store the downloaded content
     *            from the URI
     */
    public CrawlingActivity(CrawleableUri uri, Worker worker, Sink sink) {
        this.worker = worker;
        this.dateStarted = Calendar.getInstance();
        this.uri = uri;
        this.state = CrawlingURIState.UNKNOWN;
        activityUri = Constants.DEFAULT_ACTIVITY_URI_PREFIX + uri.getData(Constants.UUID_KEY).toString();
        this.sink = sink;
    }

    public void setState(CrawlingURIState state) {
        this.state = state;
    }

    /**
     * Finish the crawling activity and send data to sink
     */
    public void finishActivity(Sink sink) {
        dateEnded = Calendar.getInstance();
        sink.addMetaData(prepareMetadataModel());
    }

    /**
     * Prepare the metadata model and returns it.
     * 
     * @return the RDF model representing this metadata
     */
    public Model prepareMetadataModel() {
        Model model = ModelFactory.createDefaultModel();
        Resource activity = model.createResource(activityUri);
        model.add(activity, RDF.type, PROV_O.Activity);
        Resource crawledUri = model.createResource(getCrawleableUri().getUri().toString());
        model.add(activity, Squirrel.crawled, crawledUri);

        model.add(activity, Squirrel.status, model.createTypedLiteral(getState().toString()));
        model.add(activity, PROV_O.startedAtTime, model.createTypedLiteral(dateStarted));
        model.add(activity, PROV_O.endedAtTime, model.createTypedLiteral(dateEnded));

        Resource association = model.createResource(activityUri + "_workerAssoc");
        model.add(association, RDF.type, PROV_O.Association);
        model.add(activity, PROV_O.qualifiedAssociation, association);
        Resource plan = model.createResource(activityUri + "_plan");
        model.add(association, RDF.type, PROV_O.Plan);
        model.add(activity, PROV_O.hadPlan, plan);
        model.add(plan, RDFS.comment, model.createTypedLiteral(getStepsAsString()));

        if (getWorker() != null) {
            model.add(activity, PROV_O.wasAssociatedWith, model.createResource(getWorker().getUri()));
        }
        if (getHostedOn() != null) {
            model.add(activity, Squirrel.hostedOn, model.createTypedLiteral(getHostedOn()));
        }
        if (getGraphId() != null) {
            Resource resultGraph = model.createResource(getGraphId());
            model.add(resultGraph, PROV_O.wasGeneratedBy, activity);
            model.add(resultGraph, Squirrel.resultGraphOf, crawledUri);
        }
        return model;
    }

    protected String getStepsAsString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Steps of crawling:");
        for (String step : steps) {
            builder.append("\n");
            builder.append(step);
        }
        return builder.toString();
    }

    public Worker getWorker() {
        return worker;
    }

    public enum CrawlingURIState {
        SUCCESSFUL, UNKNOWN, FAILED;
    }

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
