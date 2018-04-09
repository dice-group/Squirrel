package org.aksw.simba.squirrel.sink.impl.sparql;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.metadata.CrawlingActivity;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SparqlBasedSink implements Sink {

    private static String strContentDatasetUriUpdate;
    private static String strContentDatasetUriQuery;
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);
    @SuppressWarnings("unused")
    private static String strMetaDatasetUriQuery;
    @SuppressWarnings("unused")
    private static String strMetaDatasetUriUpdate;
    private static String datasetPrefix;


    public SparqlBasedSink() {
        String strIP = "jena";
        String portVar = "PORT";
        Map<String, String> env = System.getenv();
        String port = "3030";
        datasetPrefix = "http://" + strIP + ":" + port + "/";
        strContentDatasetUriUpdate = datasetPrefix + "ContentSet/update";
        strMetaDatasetUriUpdate = datasetPrefix + "MetaData/update";
        strContentDatasetUriQuery = datasetPrefix + "ContentSet/query";
        strMetaDatasetUriQuery = datasetPrefix + "MetaData/query";

    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        List<Triple> lstTriples = new ArrayList<>();
        Node nodeCrawlingActivity = new Node_Variable("crawlingActivity" + crawlingActivity.getId());
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("prov:startedAtTime"), new Node_Variable(crawlingActivity.getDateStarted())));
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("prov:endedAtTime"), new Node_Variable(crawlingActivity.getDateEnded())));
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("sq:Status"), new Node_Variable(crawlingActivity.getStatus().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("prov:wasAssociatedWith"), new Node_Variable(String.valueOf(crawlingActivity.getWorker().getId()))));
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("sq:numberOfTriples"), new Node_Variable(String.valueOf(crawlingActivity.getNumTriples()))));
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("sq:hostedOn"), new Node_Variable(datasetPrefix)));
//        for (CrawleableUri uri : crawlingActivity.getMapUri().keySet()) {
//            lstTriples.add(new Triple(new Node_Variable(uri.toString()), new Node_Variable("prov:wasGeneratedBy"), nodeCrawlingActivity));
//
//        }

        lstTriples.forEach(triple -> {

            UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(String.valueOf(crawlingActivity.getId()), triple, true));
            UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, strMetaDatasetUriUpdate);
            proc.execute();
        });

    }

    public int getNumberOfTriplesForGraph(CrawleableUri uri) {
        QueryExecution q = QueryExecutionFactory.sparqlService(strContentDatasetUriQuery,
            QueryGenerator.getInstance().getSelectAllQuery(uri));
        ResultSet results = q.execSelect();
        int sum = 0;
        while (results.hasNext()) {
            results.next();
            sum++;
        }
        return sum;
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(uri, triple, false));
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, strContentDatasetUriUpdate);
        proc.execute();
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {
    }
}
