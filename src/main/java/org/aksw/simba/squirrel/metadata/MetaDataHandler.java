package org.aksw.simba.squirrel.metadata;


import org.aksw.jena_sparql_api.mapper.annotation.RdfType;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.graph.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
        Model model  = ModelFactory.createDefaultModel();
        CrawleableUri uri = crawlingActivity.getCrawleableUri();

        Node nodeResultGraph = NodeFactory.createURI(String.valueOf(crawlingActivity.getUrl("Result_"+ crawlingActivity.getGraphId(uri))));
        Node nodeCrawlingActivity = NodeFactory.createURI(String.valueOf(crawlingActivity.getUrl(crawlingActivity.getId())));
        Node Association = NodeFactory.createURI(String.valueOf(crawlingActivity.getUrl("Worker-checking-Crawl-guide")));
        Node crawling = NodeFactory.createURI(String.valueOf(crawlingActivity.getUrl("Crawl-guide")));

        lstTriples.add(new Triple(nodeCrawlingActivity,RDF.type.asNode(),Prov.Activity.asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.startedAtTime.asNode(), model.createTypedLiteral(crawlingActivity.getLocalDateTime(),XSDDatatype.XSDdateTime).asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.endedAtTime.asNode(), model.createTypedLiteral(crawlingActivity.getLocalDateTime(),XSDDatatype.XSDdateTime).asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity, Sq.status.asNode(), NodeFactory.createLiteral(crawlingActivity.getState().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, Sq.numberOfTriples.asNode(), model.createTypedLiteral(crawlingActivity.getNumTriples(),XSDDatatype.XSDint).asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.wasAssociatedWith.asNode(), NodeFactory.createURI(String.valueOf(crawlingActivity.getUrl("Worker_"+ String.valueOf(crawlingActivity.getWorker().getId()))))));
        lstTriples.add(new Triple(nodeResultGraph, Prov.wasGeneratedBy.asNode(),nodeCrawlingActivity ));
        lstTriples.add(new Triple(nodeResultGraph, RDFS.comment.asNode(),NodeFactory.createLiteral("GraphID where the content is stored")));
        lstTriples.add(new Triple(nodeCrawlingActivity, Sq.hostedOn.asNode(),model.createResource(crawlingActivity.getHost()).asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity,Prov.qualifiedAssociation.asNode(),Association));
        lstTriples.add(new Triple(Association,RDF.type.asNode(),NodeFactory.createURI(Prov.Association.toString())));
        lstTriples.add(new Triple(Association,Prov.agent.asNode(),NodeFactory.createURI(String.valueOf(crawlingActivity.getUrl("Worker_"+ String.valueOf(crawlingActivity.getWorker().getId()))))));
        lstTriples.add(new Triple(Association,Prov.hadPlan.asNode(),crawling));
        lstTriples.add(new Triple(crawling,RDF.type.asNode(),Prov.Plan.asNode()));
        lstTriples.add(new Triple(crawling,RDF.type.asNode(),Prov.Entity.asNode()));
        lstTriples.add(new Triple(crawling,Sq.steps.asNode(),NodeFactory.createLiteral(crawlingActivity.getHadPlan())));

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
