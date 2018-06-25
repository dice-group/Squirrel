package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.apache.jena.graph.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class MetaDataHandler {

    private static final String GRAPH_NAME_FOR_METADATA = "MetaData";
    private CrawleableUri dummyUri;
    private Sink sink;

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);


    public MetaDataHandler(String updateDatasetURI, String queryDatasetURI) {
        sink = new SparqlBasedSink(updateDatasetURI, queryDatasetURI);
        try {
            dummyUri = new CrawleableUri(new URI("MetaData:DummyUri"));
            dummyUri.addData(CrawleableUri.UUID_KEY, GRAPH_NAME_FOR_METADATA);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        List<Triple> lstTriples = new LinkedList<>();
        Node nodeCrawlingActivity = NodeFactory.createLiteral("crawlingActivity" + crawlingActivity.getId());
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.startedAtTime.asNode(), NodeFactory.createLiteral(crawlingActivity.getDateStarted())));
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.endedAtTime.asNode(), NodeFactory.createLiteral(crawlingActivity.getDateEnded())));
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.status.asNode(), NodeFactory.createLiteral(crawlingActivity.getState().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.wasAssociatedWith.asNode(), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getWorker().getId()))));
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.numberOfTriples.asNode(), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getNumTriples()))));
        Node nodeResultGraph = NodeFactory.createLiteral(crawlingActivity.getGraphId());
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.wasGeneratedBy.asNode(), nodeResultGraph));
        lstTriples.add(new Triple(nodeResultGraph, MetaDataVocabulary.uriName.asNode(), NodeFactory.createURI(crawlingActivity.getCrawleableUri().getUri().toString())));
        //TODO lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("sq:hostedOn"), NodeFactory.createLiteral(datasetPrefix)));
        //TODO for Meher: Merge new change from other branch manually for hadPlan
        //lstTriples.add(new Triple(nodeCrawlingActivity,NodeFactory.createURI("prov:hadPlan"),NodeFactory.createLiteral(crawlingActivity.getHadPlan())));

        //TODO null is not supproted
        sink.openSinkForUri(dummyUri);
        for (Triple triple : lstTriples) {
            sink.addTriple(dummyUri, triple);
        }
        sink.closeSinkForUri(dummyUri);
        LOGGER.info("MetaData successfully added for crawling activity: " + crawlingActivity.getId());
    }

    /*
    public int getNumberOfTriplesForGraph(CrawleableUri dummyUri) {
        QueryExecution q = QueryExecutionFactory.sparqlService(strContentDatasetUriQuery,
            QueryGenerator.getInstance().getSelectAllQuery(dummyUri));
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
