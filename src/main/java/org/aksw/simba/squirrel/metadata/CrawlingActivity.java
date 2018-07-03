package org.aksw.simba.squirrel.metadata;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Node;
import org.aksw.simba.squirrel.components.WorkerComponent;
import org.aksw.simba.squirrel.configurator.WorkerConfiguration;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.TripleBasedSink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
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
    private String graphId;

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
    private TripleBasedSink sink;
    /**
     * date format for start_date and end_date
     */


    /**
     * Constructor
     *
     * @param uri
     * @param worker
     * @param sink
     */
    public CrawlingActivity(CrawleableUri uri, Worker worker, TripleBasedSink sink) {
        this.worker = worker;
        this.dateStarted = new Date();
        this.uri = uri;
        this.state = CrawlingURIState.UNKNOWN;
        if (sink instanceof SparqlBasedSink) {
            graphId = ((SparqlBasedSink) sink).getGraphId(uri);
        }
        id = "crawlingActivity:" + graphId;
        this.sink = sink;
    }

    public void setState(CrawlingURIState state) {
        this.state = state;
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

    public String getId() {
        return id;
    }

    public String getHost() {
        try {
            WorkerConfiguration workerConfiguration = WorkerConfiguration.getWorkerConfiguration();
            String httpPrefix = "http://" + workerConfiguration.getSparqlHost() + ":" + workerConfiguration.getSqarqlPort() + "/";
            return httpPrefix;
        } catch (Exception e) {
            return null;
        }
    }



    /*public void addStep (Object k){

        //uri.addData(k.getClass().getSimpleName().toString(),k);

    }

    public String getHadPlan()
    {
        for(Object object : uri.getData().keySet())
        {
            int count = 1;
            String l = "step" + count++ ;
            uri.addData(l,object.getClass().getSimpleName());
        }
        ArrayList<String> list = new ArrayList<>();
        for(Object o: uri.getData().values())
        {
            list.add(o.toString());

        }
        return list.toString();
    }*/

    public Date getDateStarted() {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss");

        String dstarted = ft.format(dateStarted);
        try
        {
            Date date = ft.parse(dstarted);
            return date;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public Date getDateEnded() {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss");
        String dended = ft.format(dateEnded);
        try {
            Date date = ft.parse(dended);
            return date;
        }
        catch (Exception e)
        {
            return null;
        }

    }
    public CrawleableUri getCrawleableUri()
    {
        return uri;
    }

    public int getNumTriples() {
        return numTriples;
    }

    public Worker getWorker() {
        return worker;
    }

    public enum CrawlingURIState {SUCCESSFUL, UNKNOWN, FAILED};

    public CrawlingURIState getState() {
        return state;
    }
    public URL geturl(String o) {
        try {
            URL Domain = new URL("http://www.example.org/dataset1/file.ttl/");
             URL url = new URL(Domain + o );
             return url;

        } catch (MalformedURLException e) {
            return null;
        }
    }

   public URI getUri()
    {
        return URI.create(uri.getUri().toString());

    }

}
