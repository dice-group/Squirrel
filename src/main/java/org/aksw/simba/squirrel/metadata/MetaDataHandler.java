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
        List<Triple> models = new LinkedList<>();
        Model model  = ModelFactory.createDefaultModel();
        CrawleableUri uri = crawlingActivity.getCrawleableUri();

        Resource nodeResultGraph = ResourceFactory.createResource(String.valueOf(crawlingActivity.geturl("Result_"+ crawlingActivity.getGraphId(uri))));
        Resource nodeCrawlingActivity = ResourceFactory.createResource(String.valueOf(crawlingActivity.geturl(crawlingActivity.getId())));
        Resource Association = ResourceFactory.createResource(String.valueOf(crawlingActivity.geturl("Worker-checking-Crawl-guide")));
        Resource crawling = ResourceFactory.createResource(String.valueOf(crawlingActivity.geturl("Crawl-guide")));

        model.add(nodeCrawlingActivity,RDF.type,Prov.Activity);
        model.add(nodeCrawlingActivity, Prov.startedAtTime, model.createTypedLiteral(crawlingActivity.getdateStarted(),XSDDatatype.XSDdateTime));
        model.add(nodeCrawlingActivity, Prov.endedAtTime, model.createTypedLiteral(crawlingActivity.getDateEnded(),XSDDatatype.XSDdateTime));
        model.add(nodeCrawlingActivity, Sq.status, model.createLiteral(crawlingActivity.getState().toString()));
        model.add(nodeCrawlingActivity, Sq.numberOfTriples, model.createTypedLiteral(crawlingActivity.getNumTriples(),XSDDatatype.XSDint));
        model.add(nodeCrawlingActivity, Prov.wasAssociatedWith, model.createLiteral(String.valueOf(crawlingActivity.geturl("Worker_"+ String.valueOf(crawlingActivity.getWorker().getId())))));
        model.add(nodeResultGraph, Prov.wasGeneratedBy,nodeCrawlingActivity.asLiteral());
        model.add(nodeResultGraph, RDFS.comment,model.createLiteral("GraphID where the content is stored"));
        model.add(nodeCrawlingActivity, Sq.hostedOn,model.createResource(crawlingActivity.getHostedOn()));
        model.add(nodeCrawlingActivity,Prov.qualifiedAssociation,Association);
        model.add(Association,RDF.type,model.createResource(Prov.Association.toString()));
        model.add(Association,Prov.agent,model.createLiteral(String.valueOf(crawlingActivity.geturl("Worker_"+ String.valueOf(crawlingActivity.getWorker().getId())))));
        model.add(Association,Prov.hadPlan,crawling);
        model.add(crawling,RDF.type,Prov.Plan);
        model.add(crawling,RDF.type,Prov.Entity);
        model.add(crawling,Sq.steps,model.createLiteral(crawlingActivity.getHadPlan()));

        sink.openSinkForUri(defaultGraphUri);
        for (Triple triple : models) {
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
