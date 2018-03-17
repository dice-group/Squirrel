package org.aksw.simba.squirrel.sink.impl.rdfSink;

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

public class RDFSink implements Sink {

    private static String strContentDatasetUriUpdate;
    private static String strContentDatasetUriQuery;
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(RDFSink.class);
    @SuppressWarnings("unused")
    private static String strMetaDatasetUriQuery;
    @SuppressWarnings("unused")
    private static String strMetaDatasetUriUpdate;


    public RDFSink() {
        String strIP = "jena";
        strContentDatasetUriUpdate = "http://" + strIP + ":3030/ContentSet/update";
        strMetaDatasetUriUpdate = "http://" + strIP + ":3030/MetaData/update";
        strContentDatasetUriQuery = "http://" + strIP + ":3030/ContentSet/query";
        strMetaDatasetUriQuery = "http://" + strIP + ":3030/MetaData/query";

    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        List<Triple> lstTriples = new ArrayList<>();
        Node nodeSubject = new Node_Variable("crawlingActivity" + crawlingActivity.getId());
        lstTriples.add(new Triple(nodeSubject, new Node_Variable("date_started"), new Node_Variable(crawlingActivity.getDateStarted())));
        lstTriples.add(new Triple(nodeSubject, new Node_Variable("date_ended"), new Node_Variable(crawlingActivity.getDateEnded())));
        lstTriples.add(new Triple(nodeSubject, new Node_Variable("state"), new Node_Variable(crawlingActivity.getStatus().toString())));
        lstTriples.add(new Triple(nodeSubject, new Node_Variable("id_of_worker"), new Node_Variable(String.valueOf(crawlingActivity.getWorker().getId()))));
        lstTriples.add(new Triple(nodeSubject, new Node_Variable("num_of_all_triples"), new Node_Variable(String.valueOf(crawlingActivity.getNumTriples()))));

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
