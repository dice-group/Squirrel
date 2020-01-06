package org.dice_research.squirrel.sink.impl.sparql;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactoryDataset;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.vocab.Squirrel;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SparqlBasedSinkTest {

    @Test
    public void test() throws URISyntaxException, IOException {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        UpdateExecutionFactory updateExecFactory = new UpdateExecutionFactoryDataset(dataset);

        CrawleableUri uri = new CrawleableUri(new URI("http://example.org/dataset"));
        uri.addData(Constants.UUID_KEY, "123");

        CrawlingActivity activity = new CrawlingActivity(uri, "http://example.org/testWorker");
        uri.addData(Constants.URI_CRAWLING_ACTIVITY, activity);
        SparqlBasedSink sink = new SparqlBasedSink(queryExecFactory, updateExecFactory);
        sink.openSinkForUri(uri);
        sink.addTriple(uri, new Triple(Squirrel.ResultGraph.asNode(), RDF.type.asNode(), RDFS.Class.asNode()));
        sink.addTriple(uri, new Triple(Squirrel.ResultGraph.asNode(), RDF.value.asNode(),
            ResourceFactory.createTypedLiteral("3.14", XSDDatatype.XSDdouble).asNode()));
        sink.closeSinkForUri(uri);

        Model model = dataset.getNamedModel(SparqlBasedSink.getGraphId(uri));
        Assert.assertEquals(2, model.size());
        StmtIterator iterator = model.listStatements(null, RDF.type, (RDFNode) null);
        Statement s;
        while (iterator.hasNext()) {
            s = iterator.next();
            Assert.assertEquals(Squirrel.ResultGraph, s.getSubject());
            Assert.assertEquals(RDFS.Class, s.getObject());
        }
        iterator = model.listStatements(null, RDF.value, (RDFNode) null);
        while (iterator.hasNext()) {
            s = iterator.next();
            Assert.assertEquals(Squirrel.ResultGraph, s.getSubject());
            Assert.assertTrue(s.getObject().isLiteral());
            Assert.assertEquals(new Double(3.14), s.getObject().asLiteral().getValue());
        }

        // Check the content of the activity
        Assert.assertEquals(2, activity.getNumberOfTriples());
    }

    @Test
    public void testDropGraph() throws URISyntaxException, IOException {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        UpdateExecutionFactory updateExecFactory = new UpdateExecutionFactoryDataset(dataset);

        SparqlBasedSink sink = new SparqlBasedSink(queryExecFactory, updateExecFactory);

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
        Assert.assertEquals(2, activity1.getNumberOfTriples());

        sink.dropGraph(uri1);
        Assert.assertEquals(0, sink.getTriplesForGraph(uri1).size());
    }

    @Test
    public void testUpdateGraphForUri() throws URISyntaxException, IOException {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        UpdateExecutionFactory updateExecFactory = new UpdateExecutionFactoryDataset(dataset);

        SparqlBasedSink sink = new SparqlBasedSink(queryExecFactory, updateExecFactory);

        CrawleableUri uri1 = new CrawleableUri(new URI("http://example.org/dataset1"));
        uri1.addData(Constants.UUID_KEY, "123");

        CrawleableUri uri2 = new CrawleableUri(new URI("http://example.org/dataset2"));
        uri2.addData(Constants.UUID_KEY, "124");

        CrawlingActivity activity1 = new CrawlingActivity(uri1, "http://example.org/testWorker1");
        uri1.addData(Constants.URI_CRAWLING_ACTIVITY, activity1);

        CrawlingActivity activity2 = new CrawlingActivity(uri2, "http://example.org/testWorker2");
        uri2.addData(Constants.URI_CRAWLING_ACTIVITY, activity2);

        Triple triple1 = new Triple(Squirrel.ResultGraph.asNode(), RDF.type.asNode(), RDFS.Class.asNode());
        Triple triple2 = new Triple(Squirrel.ResultGraph.asNode(), RDF.value.asNode(),
            ResourceFactory.createTypedLiteral("3.14", XSDDatatype.XSDdouble).asNode());

        sink.openSinkForUri(uri1);
        sink.addTriple(uri1, triple1);
        sink.addTriple(uri1, triple2);
        sink.closeSinkForUri(uri1);
        Assert.assertEquals(2, activity1.getNumberOfTriples());

        sink.openSinkForUri(uri2);
        sink.addTriple(uri2, triple1);
        sink.addTriple(uri2, triple2);
        sink.closeSinkForUri(uri2);
        Assert.assertEquals(2, activity2.getNumberOfTriples());

        sink.updateGraphForUri(uri1, uri2);
        Assert.assertEquals(Constants.DEFAULT_RESULT_GRAPH_URI_PREFIX + "124", uri1.getData(Constants.UUID_KEY));
    }
}
