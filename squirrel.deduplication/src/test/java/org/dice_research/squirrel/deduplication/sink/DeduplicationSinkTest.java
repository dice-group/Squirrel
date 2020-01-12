package org.dice_research.squirrel.deduplication.sink;

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

public class DeduplicationSinkTest {

    private DeduplicationSink sink;
    private QueryExecutionFactory queryExecFactory;
    private UpdateExecutionFactory updateExecFactory;
    private Dataset dataset;

    @Before
    public void init() {
        dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        updateExecFactory = new UpdateExecutionFactoryDataset(dataset);
        sink = new DeduplicationSink(queryExecFactory, updateExecFactory);
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

        sink.openSinkForUri(uri1);
        sink.addTriple(uri1, triple1);
        sink.addTriple(uri1, triple2);
        sink.closeSinkForUri(uri1);
        Assert.assertEquals(2, sink.getTriplesForGraph(uri1).size());

        sink.dropGraph(uri1);
        // check if the triples associated with the graph have been deleted.
        Assert.assertEquals(0, sink.getTriplesForGraph(uri1).size());
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

        sink.openSinkForUri(uri1);
        sink.addTriple(uri1, triple1);
        sink.addTriple(uri1, triple2);
        sink.closeSinkForUri(uri1);
        Assert.assertEquals(2, sink.getTriplesForGraph(uri1).size());

        List<CrawleableUri> uris = new ArrayList<>();
        uris.add(uri1);
        sink.addGraphIdForURIs(uris);

        sink.updateGraphForUri(uri1, Constants.DEFAULT_RESULT_GRAPH_URI_PREFIX + "124");
        // check if the graph id of the uri has been updated in the metadata graph
        Assert.assertEquals(Constants.DEFAULT_RESULT_GRAPH_URI_PREFIX + "124", sink.getGraphIdFromSparql(uri1.getUri().toString()));
    }
}
