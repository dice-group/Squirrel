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
import org.apache.jena.sparql.core.DatasetDescription;
import org.dice_research.squirrel.sink.SparqlBasedSinkDedup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 */
public class SPARQLConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSinkDedup.class);

    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    public static QueryExecutionFactory queryExecFactory = null;

    public UpdateExecutionFactory updateExecFactory = null;

    public  SPARQLConnector(String username, String password, String sparqlEndpointUrl){
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
//                            LOGGER.error("I am a read-only credential provider but got a call to set credentials.");
                        }
                    });
                }
            };
            this.queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl, new DatasetDescription(),
                    authenticator);
            this.updateExecFactory = new UpdateExecutionFactoryHttp(sparqlEndpointUrl, authenticator);
        } else {
            this.queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl);
            this.updateExecFactory = new UpdateExecutionFactoryHttp(sparqlEndpointUrl);
        }
    }


}
