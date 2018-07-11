package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class MetaDataHandler {

    private Sink sink;
    private CrawleableUri defaultGraphUri;

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataHandler.class);


    public MetaDataHandler(String updateDatasetURI, String queryDatasetURI) {
        sink = new SparqlBasedSink(updateDatasetURI, queryDatasetURI);
        try {
            defaultGraphUri = new CrawleableUri(new URI(SparqlBasedSink.DEFAULT_GRAPH_STRING));
            defaultGraphUri.addData(CrawleableUri.UUID_KEY, SparqlBasedSink.DEFAULT_GRAPH_STRING);
        } catch (URISyntaxException e) {
            LOGGER.error("Error while constructing uri " + SparqlBasedSink.DEFAULT_GRAPH_STRING);
        }
    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        List<Triple> lstTriples = new LinkedList<>();
        Node nodeCrawlingActivity = NodeFactory.createURI("crawlingActivity" + crawlingActivity.getId());
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.startedAtTime.asNode(), NodeFactory.createLiteral(crawlingActivity.getDateStarted())));
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.endedAtTime.asNode(), NodeFactory.createLiteral(crawlingActivity.getDateEnded())));
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.status.asNode(), NodeFactory.createLiteral(crawlingActivity.getState().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.wasAssociatedWith.asNode(), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getWorker().getId()))));
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.numberOfTriples.asNode(), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getNumTriples()))));
        Node nodeResultGraph = NodeFactory.createURI(crawlingActivity.getGraphId());
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.wasGeneratedBy.asNode(), nodeResultGraph));
        lstTriples.add(new Triple(nodeResultGraph, MetaDataVocabulary.uriName.asNode(), NodeFactory.createURI(crawlingActivity.getCrawleableUri().getUri().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, MetaDataVocabulary.hostedOn.asNode(), NodeFactory.createLiteral(crawlingActivity.getHostedOn())));
        //TODO construct triples for hadPlan

        sink.openSinkForUri(defaultGraphUri);
        for (Triple triple : lstTriples) {
            sink.addTriple(defaultGraphUri, triple);
        }
        sink.closeSinkForUri(defaultGraphUri);
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
