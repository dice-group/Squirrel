package org.dice_research.squirrel.urifilter;

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
import org.dice_research.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.dice_research.squirrel.vocab.Squirrel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SparqlBasedUriFilterTest {
    private SparqlBasedSink sparqlBasedSink;

    private QueryExecutionFactory queryExecFactory;
    private UpdateExecutionFactory updateExecFactory;

    private SparqlBasedUriFilter uriFilter;

    Dataset dataset;

    @Before
    public void init() throws IOException, InterruptedException {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        updateExecFactory = new UpdateExecutionFactoryDataset(dataset);
        sparqlBasedSink = new SparqlBasedSink(queryExecFactory, updateExecFactory);
        uriFilter = new SparqlBasedUriFilter(queryExecFactory, updateExecFactory);
    }

    @Test
    public void testAddHashValuesForUris() throws URISyntaxException {
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

        sparqlBasedSink.openSinkForUri(uri1);
        sparqlBasedSink.addTriple(uri1, triple1);
        sparqlBasedSink.addTriple(uri1, triple2);
        sparqlBasedSink.closeSinkForUri(uri1);
        Assert.assertEquals(2, activity1.getNumberOfTriples());

        sparqlBasedSink.openSinkForUri(uri2);
        sparqlBasedSink.addTriple(uri2, triple1);
        sparqlBasedSink.addTriple(uri2, triple2);
        sparqlBasedSink.closeSinkForUri(uri2);
        Assert.assertEquals(2, activity2.getNumberOfTriples());

        List<CrawleableUri> uris = new ArrayList<>();
        uris.add(uri1);
        uris.add(uri2);
        uri1.addData(Constants.URI_HASH_KEY, "321");
        uri2.addData(Constants.URI_HASH_KEY, "322");
        uriFilter.addHashValuesForUris(uris);

        // Check if the Hash value is set in the activity
        Assert.assertEquals("321", activity1.getHashValue());
        Assert.assertEquals("322", activity2.getHashValue());
    }

    @Test
    public void testGetUrisWithSameHashValues() throws URISyntaxException {

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

        sparqlBasedSink.openSinkForUri(uri1);
        sparqlBasedSink.addTriple(uri1, triple1);
        sparqlBasedSink.addTriple(uri1, triple2);
        sparqlBasedSink.closeSinkForUri(uri1);
        Assert.assertEquals(2, activity1.getNumberOfTriples());

        sparqlBasedSink.openSinkForUri(uri2);
        sparqlBasedSink.addTriple(uri2, triple1);
        sparqlBasedSink.addTriple(uri2, triple2);
        sparqlBasedSink.closeSinkForUri(uri2);
        Assert.assertEquals(2, activity2.getNumberOfTriples());

        List<CrawleableUri> uris = new ArrayList<>();
        uris.add(uri1);
        uris.add(uri2);
        uri1.addData(Constants.URI_HASH_KEY, "555");
        uri2.addData(Constants.URI_HASH_KEY, "555");
        uriFilter.addHashValuesForUris(uris);

        Set<CrawleableUri> sameHashUris = uriFilter.getUrisWithSameHashValues("555");

        // Check if all the uris with same hash values are fetched.
        Assert.assertTrue(sameHashUris.containsAll(uris));
    }
}
