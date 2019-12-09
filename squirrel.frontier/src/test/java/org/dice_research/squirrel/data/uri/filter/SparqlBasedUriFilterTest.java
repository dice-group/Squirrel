package org.dice_research.squirrel.data.uri.filter;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactoryDataset;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
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

public class SparqlBasedUriFilterTest {
    private RDBKnownUriFilter filter;
    private RDBConnector rdbConnector = null;

    private static Connection connection;
    private static RethinkDB r = RethinkDB.r;

    private static final String DB_HOST_NAME = "localhost";
    private static final int DB_PORT = 58015;
    private SparqlBasedSink sparqlBasedSink;

    QueryExecutionFactory queryExecFactory;

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
        queryExecFactory = new QueryExecutionFactoryDataset(dataset);
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
    public void test() throws IOException, InterruptedException, URISyntaxException {
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
//            Assert.assertEquals(new Double(150964577E16), s.getObject().asLiteral().getValue());
        }

        // Check the content of the activity
        Assert.assertEquals(2, activity.getNumberOfTriples());

        //Query query = QueryFactory.create("SELECT ?uri WHERE { ?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?term. FILTER (str(?term) = \"150964577\") }");

        Query query = QueryFactory.create("SELECT ?subject ?predicate ?object WHERE { }");
        QueryExecution qe = queryExecFactory.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            //Assert.assertEquals(Squirrel.ResultGraph, qs.get);
            //Assert.assertEquals(RDFS.Class, s.getObject());
        }
    }
}
