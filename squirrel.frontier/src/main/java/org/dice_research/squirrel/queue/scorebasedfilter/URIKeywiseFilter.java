package org.dice_research.squirrel.queue.scorebasedfilter;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.core.DatasetDescription;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;

/**
 * This class filters the {@link CrawleableUri}s to be added to the queue based on the score.
 * The duplicity score is calculated for a {@link CrawleableUri} as 1 / (number of times the Uri occurs as a subject in graphs
 */
public class URIKeywiseFilter implements IURIKeywiseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(URIKeywiseFilter.class);

    protected QueryExecutionFactory queryExecFactory = null;

    public URIKeywiseFilter(QueryExecutionFactory qe) {
        this.queryExecFactory = qe;
    }

    public URIKeywiseFilter(String sparqlEndpointUrl, String username, String password) {
        if (username != null && password != null) {
            // Create the factory with the credentials
            final Credentials credentials = new UsernamePasswordCredentials(username, password);
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
                            LOGGER.error("I am a read-only credential provider but got a call to set credentials.");
                        }
                    });
                }
            };
            this.queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl, new DatasetDescription(), authenticator);
        } else {
            this.queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl);
        }

    }

    /**
     * This method calculates the duplicity score for a {@link CrawleableUri} which is 1 / (number of times the Uri
     * occurs as a subject in graphs).
     *
     * @param uri the {@link CrawleableUri} for which the duplicity score has to be calculated
     * @return duplicity score
     */
    public float getURIScore(CrawleableUri uri) {
        int uriScore = getSubjectTripleCount(uri.getUri().toString());
        if (uriScore == 0) {
            return 1;
        }
        return 1 / (float) uriScore;
    }

    @Override
    public Map<CrawleableUri, Float> filterUrisKeywise(Map keyWiseUris, int minNumberOfUrisToCheck, float criticalScore) {
        Collection<List<CrawleableUri>> uriLists = keyWiseUris.values();
        Map<CrawleableUri, Float> filteredUriMap = new HashMap<>();
        for (List<CrawleableUri> uriList : uriLists) {
            boolean scoresBelowCritical = true;
            for (int i = 0; i < (minNumberOfUrisToCheck < uriList.size() ? minNumberOfUrisToCheck : uriList.size()); i++) {
                float score = getURIScore(uriList.get(i));
                if (score > criticalScore) {
                    scoresBelowCritical = false;
                }
            }
            if (!scoresBelowCritical) {
                for (CrawleableUri uri : uriList) {
                    filteredUriMap.put(uri, getURIScore(uri));
                }
            }
        }
        return filteredUriMap;
    }

    /**
     * This method return the number of times the Uri occurs as a subject in the graphs.
     *
     * @param uri the Uri for which the score has to calculated
     * @return number of times the Uri occurs as a subject in the graphs
     */
    private int getSubjectTripleCount(String uri) {
        String query = "SELECT (COUNT(*) AS ?C) WHERE { GRAPH ?g { <" + uri + "> ?p ?o } }";
        try (QueryExecution execution = queryExecFactory.createQueryExecution(query)) {
            ResultSet resultSet = execution.execSelect();
            if (resultSet.hasNext()) {
                QuerySolution solution = resultSet.next();
                int count = solution.getLiteral("C").getInt();
                LOGGER.info("getGraphSize(<{}>) = {}", uri, count);
                return count;
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while querying Sparql for duplicity of URL", e);
        }
        return 0;
    }
}
