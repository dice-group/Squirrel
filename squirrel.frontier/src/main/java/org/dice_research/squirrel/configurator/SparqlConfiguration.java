package org.dice_research.squirrel.configurator;

import java.net.URI;

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
import org.apache.jena.sparql.core.DatasetDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class SparqlConfiguration  {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlConfiguration.class);

    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    protected static QueryExecutionFactory queryExecFactory = null;

    protected UpdateExecutionFactory updateExecFactory = null;


    protected SparqlConfiguration(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory) {
        this.queryExecFactory = queryExecFactory;
        this.updateExecFactory = updateExecFactory;
    }

    public static SparqlConfiguration create(String sparqlEndpointUrl) {
    	
        return create(sparqlEndpointUrl, null, null);
    }

    public static SparqlConfiguration create(String sparqlEndpointUrl, String username, String password) {
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
        return new SparqlConfiguration(queryExecFactory, updateExecFactory);
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

   