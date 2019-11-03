package org.dice_research.squirrel.frontier.recrawling;

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
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.DatasetDescription;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class SparqlhostConnector implements OutDatedUriRetreiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlhostConnector.class);

    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    protected static QueryExecutionFactory queryExecFactory = null;
    protected UpdateExecutionFactory updateExecFactory = null;
    List<CrawleableUri> urisToRecrawl = new ArrayList<>();

    public SparqlhostConnector(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory) {
        this.queryExecFactory = queryExecFactory;
        this.updateExecFactory = updateExecFactory;
        LOGGER.info("Connected");
    }

    public static SparqlhostConnector create(String sparqlEndpointUrl) {
        return create(sparqlEndpointUrl, null, null);
    }

    public static SparqlhostConnector create(String sparqlEndpointUrl, String username, String password) {
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
        return new SparqlhostConnector(queryExecFactory, updateExecFactory);
    }


    @Override
    public List<CrawleableUri> getUriToRecrawl() {
        //SparqlhostConnector.create("http://localhost:8890/sparql-auth", "dba", "pw123");
        Query getOutdatedUrisQuery = FrontierQueryGenerator.getOutdatedUrisQuery();
        System.out.println(getOutdatedUrisQuery);
        QueryExecution qe = queryExecFactory.createQueryExecution(getOutdatedUrisQuery);
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode outdatedUri = sol.get("uri");
            try {
                urisToRecrawl.add(new CrawleableUri(new URI((outdatedUri.toString()))));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        qe.close();
        return urisToRecrawl;
    }


    @Override
    public void close() throws IOException {
        getUriToRecrawl();
    }
}

