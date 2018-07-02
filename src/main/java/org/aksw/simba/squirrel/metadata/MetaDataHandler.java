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
        String nodeCrawlingActivity = crawlingActivity.getId().toString();
        Resource CrawlingActivity = model.createResource(nodeCrawlingActivity);
        //Node nodeCrawlingActivity = NodeFactory.createURI("crawlingActivity" + crawlingActivity.getUri());
        lstTriples.add(new Triple(CrawlingActivity.asNode(), Prov.startedAtTime.asNode(), NodeFactory.createLiteral(crawlingActivity.getDateStarted(), XSDDatatype.XSDdateTime)));
        lstTriples.add(new Triple(CrawlingActivity.asNode(), Prov.endedAtTime.asNode(), NodeFactory.createLiteral(crawlingActivity.getDateEnded(),XSDDatatype.XSDdateTime)));
        lstTriples.add(new Triple(CrawlingActivity.asNode(), Sq.status.asNode(), NodeFactory.createLiteral(crawlingActivity.getState().toString())));
        lstTriples.add(new Triple(CrawlingActivity.asNode(), Prov.wasAssociatedWith.asNode(), NodeFactory.createLiteral(String.valueOf(( crawlingActivity.getWorker().getId())))));
        lstTriples.add(new Triple(CrawlingActivity.asNode(), Sq.numberOfTriples.asNode(), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getNumTriples()))));
        Node nodeResultGraph = NodeFactory.createURI(crawlingActivity.getUri().toString());
        lstTriples.add(new Triple(CrawlingActivity.asNode(), Prov.wasGeneratedBy.asNode(), nodeResultGraph));
        lstTriples.add(new Triple(nodeResultGraph, Sq.uriName.asNode(), NodeFactory.createLiteral(crawlingActivity.getCrawleableUri().getUri().toString())));
        lstTriples.add(new Triple(CrawlingActivity.asNode(), Sq.hostedOn.asNode(), NodeFactory.createLiteral(crawlingActivity.getHost())));
        lstTriples.add(new Triple(CrawlingActivity.asNode(),Prov.qualifiedAssociation.asNode(),Prov.Association.asNode()));
        lstTriples.add(new Triple(Prov.Association.asNode(),Prov.agent.asNode(),NodeFactory.createLiteral(String.valueOf(crawlingActivity.getWorker().getId()))));
        //lstTriples.add(new Triple(nodeCrawlingActivity,NodeFactory.createURI("prov:hadPlan"),NodeFactory.createLiteral(crawlingActivity.getHadPlan())));

        sink.openSinkForUri(dummyUri);
        for (Triple triple : lstTriples) {
            sink.addTriple(dummyUri, triple);
        }
        sink.closeSinkForUri(dummyUri);
        LOGGER.info("MetaData successfully added for crawling activity: " + crawlingActivity.getId());
    }


}