package org.dice_research.squirrel.sink;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.dice_research.squirrel.sink.impl.sparql.QueryGenerator;
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

    public RDFNode getActivityUri(String uriCrawled){
        Query activityIdQuery = QueryGenerator.getInstance().getActivityUriQuery(uriCrawled);
        QueryExecution qe = this.queryExecFactory.createQueryExecution(activityIdQuery);
        ResultSet rs = qe.execSelect();
        RDFNode activityId = null;
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            activityId = sol.get("subject");
        }
        qe.close();
        return activityId;
    }

    public List<CrawleableUri> getGeneratedUrisFromMetadata(CrawleableUri uri){
        List<CrawleableUri> generatedUris = new ArrayList<>();
        RDFNode activityUri = getActivityUri(uri.getUri().toString());
        Query generatedUrisQuery = QueryGenerator.getInstance().getGeneratedUrisQuery(
            QueryGenerator.formatNodeToString(activityUri.asNode())); //TODO check weather the node with the prefix is returned
        QueryExecution qe = this.queryExecFactory.createQueryExecution(generatedUrisQuery);
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            String genUri = sol.get("object").toString();
            try {
                generatedUris.add(new CrawleableUri(new URI(genUri)));
            } catch (URISyntaxException e){
                LOGGER.warn("Exception thrown when parsing generated uri - " + genUri, e);
            }
        }
        qe.close();
        return generatedUris;
    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {

    }

    public RDFNode getGraphId(String uriCrawled){
        Query graphIdQuery = QueryGenerator.getInstance().getGraphIdQuery(uriCrawled);
        QueryExecution qe = this.queryExecFactory.createQueryExecution(graphIdQuery);
        ResultSet rs = qe.execSelect();
        RDFNode graphId = null;
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            graphId = sol.get("subject");
        }
        qe.close();
        return graphId;
    }

    @Override
    public List<Triple> getTriplesForGraph(CrawleableUri uri) {
        RDFNode graphId = getGraphId(uri.getUri().toString());
        Query triplesQuery = QueryGenerator.getInstance().getSelectQuery(
            QueryGenerator.formatNodeToString(graphId.asNode()),//TODO check weather the node with the prefix is returned(ex: http://w3id.org/squirrel/graph#e5b059d0-61d0-4830-8625-baea3b9a2bbc).
            false);
        QueryExecution qe = this.queryExecFactory.createQueryExecution(triplesQuery);
        ResultSet rs = qe.execSelect();
        List<Triple> triplesFound = new ArrayList<>();
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


