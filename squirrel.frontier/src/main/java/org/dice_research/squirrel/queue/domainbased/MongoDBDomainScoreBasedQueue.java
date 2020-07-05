package org.dice_research.squirrel.queue.domainbased;

import com.mongodb.client.model.Sorts;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.sparql.core.DatasetDescription;
import org.bson.Document;
import org.bson.types.Binary;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.SnappyJavaUriSerializer;
import org.dice_research.squirrel.queue.scorebasedfilter.IUriKeywiseFilter;
import org.dice_research.squirrel.queue.scorebasedfilter.UriKeywiseFilter;
import org.dice_research.squirrel.queue.scorecalculator.IUriScoreCalculator;
import org.dice_research.squirrel.queue.scorecalculator.UriScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * DomainBasedQueue with MongoDB, and score based sorting of URI Queue
 *
 */
public class MongoDBDomainScoreBasedQueue extends MongoDBDomainBasedQueue {

    @Deprecated
    private final String DEFAULT_TYPE = "default";
    private static final boolean PERSIST = System.getenv("QUEUE_FILTER_PERSIST") == null ? false
        : Boolean.parseBoolean(System.getenv("QUEUE_FILTER_PERSIST"));
    public static final String URI_DOMAIN = "domain";
    private IUriKeywiseFilter uriKeywiseFilter;
    private IUriScoreCalculator uriScoreCalculator;

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBDomainScoreBasedQueue.class);

    /**
     * This constructor is for the junit test case execution only
     *
     */
    public MongoDBDomainScoreBasedQueue(String hostName, Integer port, boolean includeDepth, QueryExecutionFactory queryExecFactory) {
        super(hostName, port, new SnappyJavaUriSerializer(), includeDepth);
        this.uriScoreCalculator = new UriScoreCalculator(queryExecFactory);
        this.uriKeywiseFilter = new UriKeywiseFilter(uriScoreCalculator);
    }

    public MongoDBDomainScoreBasedQueue(String hostName, Integer port, Serializer serializer, boolean includeDepth, String sparqlEndpointUrl, String username, String password) {
        super(hostName, port, serializer, includeDepth);
        QueryExecutionFactory qef = getQueryExecutionFactory(sparqlEndpointUrl, username, password);
        this.uriScoreCalculator = new UriScoreCalculator(qef);
        this.uriKeywiseFilter = new UriKeywiseFilter(uriScoreCalculator);
    }

    public MongoDBDomainScoreBasedQueue(String hostName, Integer port, Serializer serializer, boolean includeDepth, String sparqlEndpointUrl, String username, String password, UriScoreCalculator uriScoreCalculator, UriKeywiseFilter uriKeywiseFilter) {
        super(hostName, port, serializer, includeDepth);
        QueryExecutionFactory qef = getQueryExecutionFactory(sparqlEndpointUrl, username, password);
        this.uriScoreCalculator = uriScoreCalculator;
        this.uriKeywiseFilter = uriKeywiseFilter;
    }

    private QueryExecutionFactory getQueryExecutionFactory(String sparqlEndpointUrl, String username, String password) {
        QueryExecutionFactory qef;
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
            qef = new QueryExecutionFactoryHttp(sparqlEndpointUrl, new DatasetDescription(), authenticator);
        } else {
            qef = new QueryExecutionFactoryHttp(sparqlEndpointUrl);
        }
        return qef;
    }

    protected void addCrawleableUri(CrawleableUri uri, String domain, float score) {
        try {
            Document uriDoc = getUriDocument(uri, domain);
            uriDoc.put(Constants.URI_SCORE, score);
            // If the document does not already exist, add it
            if (mongoDB.getCollection(COLLECTION_URIS).find(uriDoc).first() == null) {
                mongoDB.getCollection(COLLECTION_URIS).insertOne(uriDoc);
            }
        } catch (Exception e) {
            LOGGER.error("Error while adding uri to MongoDBQueue", e);
        }
    }

    @Override
    public List<CrawleableUri> getUris(String domain) {

        Iterator<Document> uriDocs = mongoDB.getCollection(COLLECTION_URIS)
            .find(new Document(URI_DOMAIN, domain).append("type", DEFAULT_TYPE))
            .sort(Sorts.descending(Constants.URI_SCORE)).iterator();

        List<CrawleableUri> listUris = new ArrayList<CrawleableUri>();

        try {
            while (uriDocs.hasNext()) {

                Document doc = uriDocs.next();

                listUris.add(serializer.deserialize(((Binary) doc.get("uri")).getData()));

            }

        } catch (Exception e) {
            LOGGER.error("Error while retrieving uri from MongoDBQueue", e);
        }

        return listUris;
    }

    @Override
    protected void addUris(Map<String, List<CrawleableUri>> domainwiswUris) {
        Map<String, List<CrawleableUri>> uriMap = uriKeywiseFilter.filterUrisKeywise(domainwiswUris);
        for(Map.Entry<String, List<CrawleableUri>> entry : uriMap.entrySet()) {
            addDomain(entry.getKey());
            for(CrawleableUri uri:entry.getValue()) {
                addCrawleableUri(uri, entry.getKey(), (float)uri.getData(Constants.URI_SCORE));
            }
        }
    }
}
