package org.dice_research.squirrel.deduplication.impl;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
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
import org.dice_research.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.dice_research.squirrel.deduplication.hashing.impl.SimpleTripleComparator;
import org.dice_research.squirrel.deduplication.hashing.impl.SimpleTripleHashFunction;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.model.RDBConnector;
import org.dice_research.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.dice_research.squirrel.vocab.Squirrel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DeduplicationImplTest {

    private RDBKnownUriFilter filter;
    private RDBConnector rdbConnector = null;

    private static Connection connection;
    private static RethinkDB r = RethinkDB.r;

    private static final String DB_HOST_NAME = "localhost";
    private static final int DB_PORT = 58015;
    private SparqlBasedSink sparqlBasedSink;

    @Before
    public void init() throws IOException, InterruptedException {
        String rdbStopAll = "docker stop $(docker ps -a -q)";
        String rdbRemoveAll = "docker rm $(docker ps -a -q)";
        Process x = Runtime.getRuntime().exec(rdbStopAll);
        x.waitFor();
        x = Runtime.getRuntime().exec(rdbRemoveAll);
        x.waitFor();
        String rethinkDockerExecCmd = "docker run --name squirrel-test-rethinkdb "
            + "-p " + DB_PORT + ":28015 -p 58884:8080 -d rethinkdb:2.3.5";
        Process p = Runtime.getRuntime().exec(rethinkDockerExecCmd);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // read any errors from the attempted command
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        UpdateExecutionFactory updateExecFactory = new UpdateExecutionFactoryDataset(dataset);
        sparqlBasedSink = new SparqlBasedSink(queryExecFactory, updateExecFactory);
    }

    @After
    public void teardown() throws IOException, InterruptedException {
        String rethinkDockerStopCommand = "docker stop squirrel-test-rethinkdb";
        Process p = Runtime.getRuntime().exec(rethinkDockerStopCommand);
        p.waitFor();
        String rethinkDockerRmCommand = "docker rm -f squirrel-test-rethinkdb";
        p = Runtime.getRuntime().exec(rethinkDockerRmCommand);
        p.waitFor();
    }

    @Test
    public void testHandlingNewUris() throws URISyntaxException {

        DeduplicationImpl deduplicationImpl = new DeduplicationImpl(sparqlBasedSink, new SimpleTripleComparator(), new SimpleTripleHashFunction());

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
        sparqlBasedSink.closeSinkForUri(uri2);
        Assert.assertEquals(1, activity2.getNumberOfTriples());

        List<CrawleableUri> uris = new ArrayList<>();
        uris.add(uri1);
        uris.add(uri2);
        deduplicationImpl.handleNewUris(uris);
        Assert.assertEquals(2, activity1.getNumberOfTriples());
        deduplicationImpl.handleNewUris(uris);
        Assert.assertEquals(1, activity2.getNumberOfTriples());
    }

    @Test
    public void testHandlingDuplicateUris() throws URISyntaxException {
        DeduplicationImpl deduplicationImpl = new DeduplicationImpl(sparqlBasedSink, new SimpleTripleComparator(), new SimpleTripleHashFunction());

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
        deduplicationImpl.handleNewUris(uris);
        Assert.assertEquals(0, activity1.getNumberOfTriples());
    }
}
