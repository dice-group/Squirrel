package org.dice_research.squirrel.sink;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.DatasetDescription;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.tripleBased.AdvancedTripleBasedSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class SparqlBasedSinkDedup implements AdvancedTripleBasedSink, Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSinkDedup.class);

    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    public QueryExecutionFactory queryExecFactory = null;

    public UpdateExecutionFactory updateExecFactory = null;


    protected SparqlBasedSinkDedup(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory) {
        this.queryExecFactory = queryExecFactory;
        this.updateExecFactory = updateExecFactory;
    }

    public static SparqlBasedSinkDedup create(String sparqlEndpointUrl, String username, String password) {
        QueryExecutionFactory queryExecFactory = null;
        UpdateExecutionFactory updateExecFactory = null;
        if (username != null && password != null) {
            // Create the factory with the credentials
            final Credentials credentials = new UsernamePasswordCredentials(username, password);
            HttpAuthenticator authenticator = new HttpAuthenticator() {
                @Override
                public void invalidate() {
                }

                @Override
                public void apply(AbstractHttpClient client, HttpContext httpContext, URI target) {
                    client.setCredentialsProvider(new CredentialsProvider() {
                        @Override
                        public void clear() {
                        }

                        @Override
                        public Credentials getCredentials(AuthScope scope) {
                            return credentials;
                        }

                        @Override
                        public void setCredentials(AuthScope arg0, Credentials arg1) {
                            LOGGER.error("I am a read-only credential provider but got a call to set credentials.");
                        }
                    });
                }
            };
            queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl, new DatasetDescription(),
                authenticator);
            updateExecFactory = new UpdateExecutionFactoryHttp(sparqlEndpointUrl, authenticator);
        } else {
            queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl);
            updateExecFactory = new UpdateExecutionFactoryHttp(sparqlEndpointUrl);
        }
        return new SparqlBasedSinkDedup(queryExecFactory, updateExecFactory);
    }

    public void queryExecute(String query){
    }

    public List<CrawleableUri> getGeneratedUrisFromMetadata(CrawleableUri uri){
        return null;
    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {

    }

    @Override
    public List<Triple> getTriplesForGraph(CrawleableUri uri) {
        //query
        String queryString = "SELECT ?subject ?predicate ?object\n" +
                "WHERE {\n" +
                "GRAPH ?g {"+uri.toString()+ "?predicate ?object}\n" +
                "}\n" +
                "LIMIT 100";
        LOGGER.info("Query looks like: ", queryString);

        QueryExecution qe = this.queryExecFactory.createQueryExecution(queryString);
        LOGGER.warn("Query execution: ", qe);

        ResultSet rs = qe.execSelect();
        List<Triple> triplesFound = new ArrayList<>();
        System.out.println("-------------------------------------------------------------------------------------------------------");
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode subject = sol.get("subject");
            RDFNode predicate = sol.get("predicate");
            RDFNode object = sol.get("object");
            triplesFound.add(Triple.create(subject.asNode(), predicate.asNode(), object.asNode()));
        }
        qe.close();
        return triplesFound;
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {

    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {

    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {

    }
//public static void main(String args[]) {
//	String sparqlEndpointUrl = "http://localhost:8890/sparql/";
//	SparqlConfiguration.create(sparqlEndpointUrl);
//
//	String queryString = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";
//
//	QueryExecution qe = queryExecFactory.createQueryExecution(queryString);
//	  System.out.println(qe);
//    ResultSet rs = qe.execSelect();
//	  System.out.println("rs"+rs);
//
//    while (rs.hasNext()) {
//     QuerySolution sol = rs.nextSolution();
//      RDFNode subject = sol.get("Concept");
//      System.out.println(subject);
//  }
//
//}
}


