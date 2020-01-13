package org.dice_research.squirrel.deduplication.graphhandler;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactoryDataset;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.vocab.Squirrel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SparqlBasedGraphHandlerTest {

    private SparqlBasedGraphHandler graphHandler;

    @Before
    public void init() {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        UpdateExecutionFactory updateExecFactory = new UpdateExecutionFactoryDataset(dataset);
        graphHandler = new SparqlBasedGraphHandler(queryExecFactory, updateExecFactory);
    }

    @Test
    public void testDropGraph() throws URISyntaxException {
        CrawleableUri uri1 = new CrawleableUri(new URI("http://example.org/dataset1"));
        uri1.addData(Constants.UUID_KEY, "123");

        CrawlingActivity activity1 = new CrawlingActivity(uri1, "http://example.org/testWorker1");
        uri1.addData(Constants.URI_CRAWLING_ACTIVITY, activity1);

        Triple triple1 = new Triple(Squirrel.ResultGraph.asNode(), RDF.type.asNode(), RDFS.Class.asNode());
        Triple triple2 = new Triple(Squirrel.ResultGraph.asNode(), RDF.value.asNode(),
            ResourceFactory.createTypedLiteral("3.14", XSDDatatype.XSDdouble).asNode());

        graphHandler.openSinkForUri(uri1);
        graphHandler.addTriple(uri1, triple1);
        graphHandler.addTriple(uri1, triple2);
        graphHandler.closeSinkForUri(uri1);
        Assert.assertEquals(2, graphHandler.getTriplesForGraph(uri1).size());

        graphHandler.dropGraph(uri1);
        // check if the triples associated with the graph have been deleted.
        Assert.assertEquals(0, graphHandler.getTriplesForGraph(uri1).size());
    }

    @Test
    public void testUpdateGraphForUri() throws URISyntaxException {
        CrawleableUri uri1 = new CrawleableUri(new URI("http://example.org/dataset1"));
        uri1.addData(Constants.UUID_KEY, "123");

        CrawlingActivity activity1 = new CrawlingActivity(uri1, "http://example.org/testWorker1");
        uri1.addData(Constants.URI_CRAWLING_ACTIVITY, activity1);

        Triple triple1 = new Triple(Squirrel.ResultGraph.asNode(), RDF.type.asNode(), RDFS.Class.asNode());
        Triple triple2 = new Triple(Squirrel.ResultGraph.asNode(), RDF.value.asNode(),
            ResourceFactory.createTypedLiteral("3.14", XSDDatatype.XSDdouble).asNode());

        graphHandler.openSinkForUri(uri1);
        graphHandler.addTriple(uri1, triple1);
        graphHandler.addTriple(uri1, triple2);
        graphHandler.closeSinkForUri(uri1);
        Assert.assertEquals(2, graphHandler.getTriplesForGraph(uri1).size());

        List<CrawleableUri> uris = new ArrayList<>();
        uris.add(uri1);
        graphHandler.addGraphIdForURIs(uris);

        graphHandler.updateGraphForUri(uri1, Constants.DEFAULT_RESULT_GRAPH_URI_PREFIX + "124");
        // check if the graph id of the uri has been updated in the metadata graph
        Assert.assertEquals(Constants.DEFAULT_RESULT_GRAPH_URI_PREFIX + "124", graphHandler.getGraphIdFromSparql(uri1.getUri().toString()));
    }
}
