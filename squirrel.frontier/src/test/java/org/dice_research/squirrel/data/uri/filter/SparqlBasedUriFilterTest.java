package org.dice_research.squirrel.data.uri.filter;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactoryHttp;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.DatasetDescription;
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

public class SparqlBasedUriFilterTest {
    private SparqlBasedSink sparqlBasedSink;

    private QueryExecutionFactory queryExecFactory;
    private UpdateExecutionFactory updateExecFactory;

    private SparqlBasedUriFilter uriFilter;

    Dataset dataset;

    @Before
    public void init() throws IOException, InterruptedException {

        String user = "dba";
        String password = "dba";
        String sparqlEndpointUrl1 = "http://localhost:8890/sparql";
        String sparqlEndpointUrl2 = "http://localhost:8890/sparql";
        if (user != null && password != null) {
            // Create the factory with the credentials
            final Credentials credentials = new UsernamePasswordCredentials(user, password);
            HttpAuthenticator authenticator = new HttpAuthenticator() {
                @Override
                public void invalidate() {
                    // unused method in this implementation
                }

                @Override
                public void apply(AbstractHttpClient client, HttpContext httpContext, URI target) {
                    client.setCredentialsProvider(new CredentialsProvider() {
                        @Override
                        public void clear() {
                            // unused method in this implementation

                        }

                        @Override
                        public Credentials getCredentials(AuthScope scope) {
                            return credentials;
                        }

                        @Override
                        public void setCredentials(AuthScope arg0, Credentials arg1) {
                        }
                    });
                }
            };
            queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl2, new DatasetDescription(),
                authenticator);
            updateExecFactory = new UpdateExecutionFactoryHttp(sparqlEndpointUrl1, authenticator);
        } else {
            queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl2);
            updateExecFactory = new UpdateExecutionFactoryHttp(sparqlEndpointUrl1);
        }
        uriFilter = new SparqlBasedUriFilter(queryExecFactory, updateExecFactory);
        sparqlBasedSink = new SparqlBasedSink(queryExecFactory, updateExecFactory);
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
        uri1.addData(Constants.URI_HASH_KEY, "123");
        uri2.addData(Constants.URI_HASH_KEY, "124");
        //uriFilter.addHashValuesForUris(uris);

        StringBuilder stringBuilder = new StringBuilder();

        // Most likely correct query
/*        stringBuilder.append("SELECT * WHERE {\n" +
            "  GRAPH <http://w3id.org/squirrel/metadata> {\n" +
            "    {\n" +
            "      SELECT ?x WHERE {\n" +
            "        ?x ?p \"123\" .\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}");*/

        stringBuilder.append("SELECT ?s ?p ?o WHERE { ?s ?p ?o }");

        Query query = QueryFactory.create(stringBuilder.toString());

        QueryExecution qe = queryExecFactory.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        List<Triple> triplesFound = new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode subject = sol.get("s");
            RDFNode predicate = sol.get("p");
            RDFNode object = sol.get("o");
            System.out.println(subject.toString()+"-------"+predicate.toString()+"-------"+object.toString());
        }
        qe.close();
    }
}
