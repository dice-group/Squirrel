package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import static org.apache.jena.vocabulary.DCTerms.provenance;

import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.QueryGenerator;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.apache.jena.graph.*;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MetaDataHandler {

    private Sink sink;

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);


    public MetaDataHandler(String updateDatasetURI, String queryDatasetURI) {
        sink = new SparqlBasedSink(updateDatasetURI, queryDatasetURI);
    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        List<Triple> lstTriples = new LinkedList<>();
        Node nodeCrawlingActivity = NodeFactory.createLiteral("crawlingActivity" + crawlingActivity.getId());
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("prov:startedAtTime"), NodeFactory.createLiteral(crawlingActivity.getDateStarted())));
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("prov:endedAtTime"), NodeFactory.createLiteral(crawlingActivity.getDateEnded())));
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("sq:Status"), NodeFactory.createLiteral(crawlingActivity.getState().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("prov:wasAssociatedWith"), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getWorker().getId()))));
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("sq:numberOfTriples"), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getNumTriples()))));
        //TODO lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("sq:hostedOn"), NodeFactory.createLiteral(datasetPrefix)));
        //TODO for Meher: Merge new change from other branch manually for hadPlan
        //lstTriples.add(new Triple(nodeCrawlingActivity,NodeFactory.createURI("prov:hadPlan"),NodeFactory.createLiteral(crawlingActivity.getHadPlan())));
        Node nodeResultGraph = NodeFactory.createLiteral(crawlingActivity.getGraphId());
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("prov:wasGeneratedBy"), nodeResultGraph));
        lstTriples.add(new Triple(nodeResultGraph, NodeFactory.createURI("sq:uriName"), NodeFactory.createURI(crawlingActivity.getCrawleableUri().getUri().toString())));

        //TODO null is not supproted
        sink.openSinkForUri(null);
        for (Triple triple : lstTriples) {
            sink.addTriple(null, triple);
        }
        sink.closeSinkForUri(null);
        LOGGER.info("MetaData successfully added for crawling activity: " + crawlingActivity.getId());
    }

    /*
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
    */


}
