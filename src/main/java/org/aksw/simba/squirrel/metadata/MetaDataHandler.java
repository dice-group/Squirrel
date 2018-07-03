package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.graph.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
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
        Model model  = ModelFactory.createDefaultModel();
        Node nodeCrawlingActivity = NodeFactory.createURI(crawlingActivity.geturl("crawlingActivity").toString());
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.startedAtTime.asNode(), model.createTypedLiteral(crawlingActivity.getDateStarted(),XSDDatatype.XSDdateTime).asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.endedAtTime.asNode(), model.createTypedLiteral(crawlingActivity.getDateEnded(),XSDDatatype.XSDdateTime).asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity, Sq.status.asNode(), NodeFactory.createLiteral(crawlingActivity.getState().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.wasAssociatedWith.asNode(), NodeFactory.createURI(String.valueOf(crawlingActivity.geturl("Worker_"+ String.valueOf(crawlingActivity.getWorker().getId()))))));
        lstTriples.add(new Triple(nodeCrawlingActivity, Sq.numberOfTriples.asNode(), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getNumTriples()))));
        Node nodeResultGraph = NodeFactory.createLiteral(crawlingActivity.geturl("Resultgraph").toString());
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.wasGeneratedBy.asNode(), nodeResultGraph));
        lstTriples.add(new Triple(nodeResultGraph, Sq.uriName.asNode(), NodeFactory.createLiteral(crawlingActivity.getCrawleableUri().getUri().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, Sq.hostedOn.asNode(), NodeFactory.createLiteral(crawlingActivity.getHost())));
        lstTriples.add(new Triple(nodeCrawlingActivity,Prov.qualifiedAssociation.asNode(),Prov.Association.asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity,Prov.agent.asNode(),NodeFactory.createURI(String.valueOf(crawlingActivity.geturl("Worker_"+ String.valueOf(crawlingActivity.getWorker().getId()))))));
        //lstTriples.add(new Triple(nodeCrawlingActivity,NodeFactory.createURI("prov:hadPlan"),NodeFactory.createLiteral(crawlingActivity.getHadPlan())));

        sink.openSinkForUri(dummyUri);
        for (Triple triple : lstTriples) {
            sink.addTriple(dummyUri, triple);
        }
        sink.closeSinkForUri(dummyUri);
        LOGGER.info("MetaData successfully added for crawling activity: " + crawlingActivity.getId());
    }


}
