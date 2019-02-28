package org.dice_research.squirrel.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.vocab.PROV_O;
import org.dice_research.squirrel.vocab.Squirrel;
import org.dice_research.squirrel.worker.Worker;

/**
 * Representation of Crawling activity. A crawling activity is started by a
 * single worker, and sends this data to the sink. So, it contains a bunch of
 * Uris and some meta data, like timestamps for the start and end of the
 * crawling activity.
 */
public class CrawlingActivity implements Serializable {
	
	private static final long serialVersionUID = 1L;

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
     * The URIs of the resources generated by this activity as well as their type as
     * RDF Resource.
     */
    private Map<String, Resource> outputResource = new HashMap<String, Resource>();

    /**
     * The crawling state of the uri.
     */
    private CrawlingURIState state;

    /**
     * URI of the worker assigned carrying out this activity.
     */
    private String workerUri;

    private long numberOfTriples = 0;

    private List<String> steps = new ArrayList<String>();

    /**
     * Constructor.
     *
     * @param uri
     *            the URI, which was crawled
     * @param workerUri
     *            URI of the {@link Worker} that crawled the URI
     */
    public CrawlingActivity(CrawleableUri uri, String workerUri) {
        this.workerUri = workerUri;
        this.dateStarted = Calendar.getInstance();
        this.uri = uri;
        this.state = CrawlingURIState.UNKNOWN;
        activityUri = Constants.DEFAULT_ACTIVITY_URI_PREFIX + uri.getData(Constants.UUID_KEY).toString();
        uri.addData(Constants.URI_CRAWLING_ACTIVITY_URI, activityUri);
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
        if (getCrawleableUri().getIpAddress() != null) {
            Resource ip = model.createResource("ip:" + getCrawleableUri().getIpAddress().getHostAddress());
            model.add(activity, Squirrel.uriHostedOn, ip);
        }
       
        model.add(activity, Squirrel.status, model.createTypedLiteral(getState().toString()));
        model.add(activity, PROV_O.startedAtTime, model.createTypedLiteral(dateStarted));
        model.add(activity, PROV_O.endedAtTime, model.createTypedLiteral(dateEnded));
        model.add(activity, Squirrel.approxNumberOfTriples, model.createTypedLiteral(numberOfTriples));

        Resource association = model.createResource(activityUri + "_workerAssoc");
        model.add(association, RDF.type, PROV_O.Association);
        model.add(activity, PROV_O.qualifiedAssociation, association);
        Resource plan = model.createResource(activityUri + "_plan");
        model.add(association, RDF.type, PROV_O.Plan);
        model.add(activity, PROV_O.hadPlan, plan);
        model.add(plan, RDFS.comment, model.createTypedLiteral(getStepsAsString()));

        if (workerUri != null) {
            model.add(activity, PROV_O.wasAssociatedWith, model.createResource(workerUri));
        }
        if (outputResource != null) {
            // write all the output resources to the model
            for (String resultUri : outputResource.keySet()) {
                Resource result = model.createResource(resultUri);
                model.add(result, RDF.type, outputResource.get(resultUri));
                model.add(result, PROV_O.wasGeneratedBy, activity);
                model.add(result, Squirrel.containsDataOf, crawledUri);
            }
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

    public enum CrawlingURIState {
        SUCCESSFUL, UNKNOWN, FAILED;

        public final String uri;

        private CrawlingURIState() {
            this.uri = Constants.DEFAULT_STATUS_URI_PREFIX + StringUtils.capitalize(this.name().toLowerCase());
        }
    }

    public CrawlingURIState getState() {
        return state;
    }

    public CrawleableUri getCrawleableUri() {
        return uri;
    }

    public CrawleableUri getUri() {
        return uri;
    }

    public void addStep(Class<?> clazz, String... actions) {
        StringBuilder builder = new StringBuilder();
        builder.append(clazz.getName());
        if ((actions != null) && (actions.length > 0)) {
            builder.append(" [");
            for (int i = 0; i < actions.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(actions[i]);
            }
            builder.append(']');
        }
        steps.add(builder.toString());
    }

    public void setNumberOfTriples(long numberOfTriples) {
        this.numberOfTriples = numberOfTriples;
    }
    
    public long getNumberOfTriples() {
        return numberOfTriples;
    }

    public void addOutputResource(String outputResource, Resource resourceType) {
        this.outputResource.put(outputResource, resourceType);
    }
}
