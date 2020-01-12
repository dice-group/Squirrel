package org.dice_research.squirrel.deduplication.impl;

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
import org.dice_research.squirrel.deduplication.hashing.impl.SimpleTripleHashFunction;
import org.dice_research.squirrel.deduplication.sink.DeduplicationSink;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.urifilter.SparqlBasedUriHashCustodian;
import org.dice_research.squirrel.vocab.Squirrel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DeduplicationImplTest {

    private DeduplicationSink sink;

    private SparqlBasedUriHashCustodian uriFilter;

    private DeduplicationImpl deduplicationImpl;

    @Before
    public void init() {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        UpdateExecutionFactory updateExecFactory = new UpdateExecutionFactoryDataset(dataset);
        sink = new DeduplicationSink(queryExecFactory, updateExecFactory);
        uriFilter = new SparqlBasedUriHashCustodian(queryExecFactory, updateExecFactory);
        deduplicationImpl = new DeduplicationImpl(uriFilter, sink, new SimpleTripleHashFunction());
    }

    @Test
    public void testHandlingNewUris() throws URISyntaxException {

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
        sink.closeSinkForUri(uri2);
        Assert.assertEquals(1, activity2.getNumberOfTriples());

        List<CrawleableUri> uris = new ArrayList<>();
        uris.add(uri1);
        uris.add(uri2);
        deduplicationImpl.handleNewUris(uris);

        // checks the adding of triples in case they aren't the same i.e. the uris are not duplicates.
        Assert.assertEquals(2, sink.getTriplesForGraph(uri1).size());
        Assert.assertEquals(1, sink.getTriplesForGraph(uri2).size());
    }

    @Test
    public void testHandlingDuplicateUris() throws URISyntaxException {

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

        List<CrawleableUri> uris = new ArrayList<>();
        uris.add(uri1);
        deduplicationImpl.handleNewUris(uris);
        uris.clear();
        uris.add(uri2);
        deduplicationImpl.handleNewUris(uris);

        // check if the uri2 is found out as a duplicate uri and its triples deleted
        Assert.assertEquals(0, sink.getTriplesForGraph(uri2).size());
        // check if the graph id of uri2 is set to the graph id of uri1
        Assert.assertEquals(sink.getGraphIdFromSparql(uri1.getUri().toString()), sink.getGraphIdFromSparql(uri2.getUri().toString()));
    }
}
