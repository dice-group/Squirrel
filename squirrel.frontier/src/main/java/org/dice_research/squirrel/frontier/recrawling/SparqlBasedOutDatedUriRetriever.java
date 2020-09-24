package org.dice_research.squirrel.frontier.recrawling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
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

@SuppressWarnings("deprecation")
public class SparqlBasedOutDatedUriRetriever implements OutDatedUriRetriever{

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedOutDatedUriRetriever.class);

    /**
     * SparqlBasedOutDatedUriRetriever creates a connection to the SPARQL endpoint and Query factory used to generate a query.
     */
    private QueryExecutionFactory queryExecFactory;
    private List<CrawleableUri> urisToRecrawl = new ArrayList<>();

    public SparqlBasedOutDatedUriRetriever(QueryExecutionFactory queryExecFactory) {
        this.queryExecFactory = queryExecFactory;
        LOGGER.info("Connected");
    }

    public SparqlBasedOutDatedUriRetriever create(String sparqlEndpointUrl) {
        return create(sparqlEndpointUrl, null, null);
    }

    public static SparqlBasedOutDatedUriRetriever create(String sparqlEndpointUrl, String username, String password) {
        QueryExecutionFactory queryExecFactory;
        if (username != null && password != null) {
            // Create the factory with the credentials
            final Credentials credentials = new UsernamePasswordCredentials(username, password);
            HttpAuthenticator authenticator = new HttpAuthenticator() {
                @Override
                public void invalidate() {
                	//TODO dummy method
                }

                @Override
                public void apply(AbstractHttpClient client, HttpContext httpContext, URI target) {
                    client.setCredentialsProvider(new CredentialsProvider() {
                        @Override
                        public void clear() {
                        	//TODO dummy method

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
        } else {
            queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl);
        }
        return new SparqlBasedOutDatedUriRetriever(queryExecFactory);
    }


    /**
     * @return list of outdated URIs
     */
    @Override
    public List<CrawleableUri> getUriToRecrawl() {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_YEAR, 7);
        Query getOutdatedUrisQuery = FrontierQueryGenerator.getOutdatedUrisQuery(date);
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

}

