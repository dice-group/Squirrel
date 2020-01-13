package org.dice_research.squirrel.sink.sparqlbased;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactoryHttp;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetDescription;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.UpdateDeleteInsert;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.vocab.Squirrel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

/**
 * A sink which stores the data in different graphs in a sparql based db.
 */
public class SparqlBasedSink extends AbstractBufferingTripleBasedSink implements Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);

    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    protected QueryExecutionFactory queryExecFactory = null;

    protected UpdateExecutionFactory updateExecFactory = null;

    protected CrawleableUri metadataGraphUri = null;

    public SparqlBasedSink(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory) {
        this.queryExecFactory = queryExecFactory;
        this.updateExecFactory = updateExecFactory;
        setMetadataGraphUri(new CrawleableUri(Constants.DEFAULT_META_DATA_GRAPH_URI));
    }

    public static SparqlBasedSink create(String sparqlEndpointUrl) {
        return create(sparqlEndpointUrl, null, null);
    }

    public static SparqlBasedSink create(String sparqlEndpointUrl, String username, String password) {
        QueryExecutionFactory queryExecFactory = null;
        UpdateExecutionFactory updateExecFactory = null;
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
            queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl, new DatasetDescription(),
                authenticator);
            updateExecFactory = new UpdateExecutionFactoryHttp(sparqlEndpointUrl, authenticator);
        } else {
            queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl);
            updateExecFactory = new UpdateExecutionFactoryHttp(sparqlEndpointUrl);
        }
        return new SparqlBasedSink(queryExecFactory, updateExecFactory);
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        LOGGER.info("Closing Sink for URI: " + uri.getUri().toString());
        super.closeSinkForUri(uri);
        if (!uri.equals(metadataGraphUri)) {
            CrawlingActivity activity = (CrawlingActivity) uri.getData(Constants.URI_CRAWLING_ACTIVITY);
            if (activity != null) {
                activity.addOutputResource(getGraphId(uri), Squirrel.ResultGraph);
            }
        }
    }

    /**
     * Method to send all buffered triples to the database
     *
     * @param uri     the crawled {@link CrawleableUri}
     * @param triples the list of {@link Triple}s regarding that uri
     */
    @Override
    public void sendTriples(CrawleableUri uri, Collection<Triple> triples) {
        try {
            Node graph;
            if (uri.equals(metadataGraphUri)) {
                graph = NodeFactory.createURI(uri.getUri().toString());
            } else {
                graph = NodeFactory.createURI(getGraphId(uri));
            }

            UpdateDeleteInsert insert = new UpdateDeleteInsert();
            insert.setHasInsertClause(true);
            insert.setHasDeleteClause(false);
            QuadAcc quads = insert.getInsertAcc();
            for (Triple triple : triples) {
                quads.addQuad(new Quad(graph, triple));
            }
            quads.setGraph(graph);
            UpdateProcessor processor = updateExecFactory.createUpdateProcessor(
                new UpdateRequest(insert).toString()
                    .replaceAll("\\{\\}", ""
                        + "{ SELECT * {OPTIONAL {?s ?p ?o} } LIMIT 1}")
            );
            LOGGER.info("Storing " + triples.size() + " triples for URI: " + uri.getUri().toString());
            processor.execute();
        } catch (Exception e) {
            LOGGER.error("Exception while sending update query.", e);
        }
    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the id of the graph in which the given uri is stored.
     *
     * @param uri The given uri.
     * @return The id of the graph.
     */
    public static String getGraphId(CrawleableUri uri) {
        if(uri.getData(Constants.UUID_KEY) != null) {
            return Constants.DEFAULT_RESULT_GRAPH_URI_PREFIX + uri.getData(Constants.UUID_KEY).toString();
        } else {
            return StringUtils.EMPTY;
        }
    }

    public void setMetadataGraphUri(CrawleableUri metadataGraphUri) {
        // close the old graph if it is not null
        if (this.metadataGraphUri != null) {
            closeSinkForUri(this.metadataGraphUri);
        }
        this.metadataGraphUri = metadataGraphUri;
        // open new meta data sink
        openSinkForUri(metadataGraphUri);
    }

    @Override
    public void close() throws IOException {
        closeSinkForUri(metadataGraphUri);
        try {
            queryExecFactory.close();
        } catch (Exception e) {
        }
        try {
            updateExecFactory.close();
        } catch (Exception e) {
        }
    }
}
