package org.dice_research.squirrel;

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
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.DatasetDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class QueryExecFactoryConnection {
   private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecFactoryConnection.class);
    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    protected static QueryExecutionFactory queryExecFactory = null;

    public QueryExecFactoryConnection(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory) {
        this.queryExecFactory = queryExecFactory;
        LOGGER.info("Connected");
    }

    public QueryExecFactoryConnection(QueryExecutionFactory queryExecFactory) {
        this.queryExecFactory = queryExecFactory;
        LOGGER.info("crawled data Connected");
    }


    public static QueryExecFactoryConnection create(String sparqlEndpointUrl) {
        return create(sparqlEndpointUrl, null, null);
    }

    public static QueryExecFactoryConnection create(String sparqlEndpointUrl, String username, String password) {
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
        return new QueryExecFactoryConnection(queryExecFactory, updateExecFactory);
    }



    public static void main(String args[]){
        QueryExecFactoryConnection.create("http://localhost:8890/sparql-auth/","dba","pw123");
        Query domainQuery = SparqlQueryGenerator.getDomain();
        Query query = QueryFactory.create(domainQuery);
        QueryExecution qe = queryExecFactory.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode prop = sol.get("prop");
            RDFNode domain = sol.get("domain");
            LOGGER.info("property: "+prop+" "+"domain: "+domain);
        }
        qe.close();

        }

}
