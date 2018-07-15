package org.aksw.simba.squirrel.metadata;


import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.fetcher.http.HTTPFetcher;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.sink.tripleBased.TripleBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.time.LocalDateTime;
import java.util.*;


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
     * number of triples
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
        }
        id = "CrawlingActivity" + uri.getData(CrawleableUri.UUID_KEY);
        this.sink = sink;
    }

    public void setState(CrawlingURIState state) {
        this.state = state;
    }

    /**
     * Finish the crawling activity and send data to sink
     */
    public void finishActivity() {
        countTriples();
        dateEnded = getLocalDateTime();
        prepareDataAndSendToSink();

    }

    /**
     * Prepare the metadata and send it to the sink.
     */
    public void prepareDataAndSendToSink() {
        Model model = ModelFactory.createDefaultModel();
        CrawleableUri uri = getCrawleableUri();

        Resource nodeResultGraph = ResourceFactory.createResource(String.valueOf(geturl("Result_" + getGraphId(uri))));
        Resource nodeCrawlingActivity = ResourceFactory.createResource(String.valueOf(geturl(getId())));
        Resource Association = ResourceFactory.createResource(String.valueOf(geturl("Worker-checking-Crawl-guide")));
        Resource crawling = ResourceFactory.createResource(String.valueOf(geturl("Crawl-guide")));


        model.add(nodeCrawlingActivity, RDF.type, Prov.Activity);
        model.add(nodeCrawlingActivity, RDFS.comment, model.createLiteral("Activity of Collecting the metadata of the Crawled content"));
        model.add(nodeCrawlingActivity, Prov.startedAtTime, model.createTypedLiteral(getdateStarted(), XSDDatatype.XSDdateTime));
        model.add(nodeCrawlingActivity, Prov.endedAtTime, model.createTypedLiteral(getDateEnded(), XSDDatatype.XSDdateTime));
        model.add(nodeCrawlingActivity, Sq.status, getState().toString());
        model.add(nodeCrawlingActivity, Sq.numberOfTriples, model.createTypedLiteral(getNumTriples(), XSDDatatype.XSDint));
        model.add(nodeCrawlingActivity, Prov.wasAssociatedWith, model.createLiteral(geturl("Worker_" + String.valueOf(getWorker().getId()))));
        model.add(nodeResultGraph, Prov.wasGeneratedBy, nodeCrawlingActivity);
        model.add(nodeResultGraph, RDFS.comment, model.createLiteral("GraphID where the content is stored"));
        model.add(nodeResultGraph, Sq.OfUri, model.createLiteral(getCrawleableUri().toString()));
        model.add(nodeCrawlingActivity, Sq.hostedOn, model.createResource(getHostedOn()));
        model.add(nodeCrawlingActivity, Prov.qualifiedAssociation, Association);
        model.add(Association, RDF.type, model.createResource(Prov.Association.toString()));
        model.add(Association, Prov.agent, model.createLiteral(geturl("Worker_" + String.valueOf(getWorker().getId()))));
        model.add(Association, Prov.hadPlan, crawling);
        model.add(crawling, RDF.type, Prov.Plan);
        model.add(crawling, RDF.type, Prov.Entity);
        model.add(crawling, Sq.steps, getHadPlan());

        getSink().addMetaData(model);

    }


    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }

    public LocalDateTime getdateStarted() {
        return dateStarted;
    }

    public LocalDateTime getDateEnded() {
        return dateEnded;
    }

    /**
     * @return unique Id
     */

    public String getId() {
        return id;
    }

    public void setNumTriples(int numTriples) {
        this.numTriples = numTriples;
    }

    private void countTriples() {
        int sum = 7;
        if (sink instanceof SparqlBasedSink) {

            //sum += ((SparqlBasedSink) sink).getNumberOfTriplesForGraph(uri);

            setNumTriples(sum);
        } else {
            setNumTriples(-1);
        }
    }


    public int getNumTriples() {
        return numTriples;
    }

    public String getclasses() {
        List<String> k = new ArrayList<>();

        for (Object object : Object.class.getClasses())
        {
            if (object instanceof CrawleableUri && !object.equals(CrawleableUri.class)) {

                k.add(object.getClass().getSimpleName());
            }

        }
        return k.toString();

    }


    public String getHadPlan(){
        return getclasses();

    }

    public CrawleableUri getCrawleableUri()
    {
        return uri;
    }

    public String getGraphId(CrawleableUri uri)
    {
        return graphId;

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
        return hostedOn.replace("update"," ");
    }

    public Sink getSink() {
        return sink;
    }
}

