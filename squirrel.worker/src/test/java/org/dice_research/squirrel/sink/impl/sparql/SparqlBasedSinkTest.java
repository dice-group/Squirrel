package org.dice_research.squirrel.sink.impl.sparql;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactoryDataset;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.vocab.Squirrel;
import org.junit.Assert;
import org.junit.Test;

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
        try (SparqlBasedSink sink = new SparqlBasedSink(queryExecFactory, updateExecFactory)) {
            sink.openSinkForUri(uri);
            sink.addTriple(uri, new Triple(Squirrel.ResultGraph.asNode(), RDF.type.asNode(), RDFS.Class.asNode()));
            sink.addTriple(uri, new Triple(Squirrel.ResultGraph.asNode(), RDF.value.asNode(),
                    ResourceFactory.createTypedLiteral("3.14", XSDDatatype.XSDdouble).asNode()));
            sink.closeSinkForUri(uri);
        }

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
}
