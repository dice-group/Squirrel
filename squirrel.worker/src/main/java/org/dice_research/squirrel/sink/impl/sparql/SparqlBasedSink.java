package org.dice_research.squirrel.sink.impl.sparql;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
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
import org.dice_research.squirrel.sink.triplebased.AdvancedTripleBasedSink;
import org.dice_research.squirrel.vocab.Squirrel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A sink which stores the data in different graphs in a sparql based db.
 */
@SuppressWarnings("deprecation")
public class SparqlBasedSink extends AbstractBufferingSink implements AdvancedTripleBasedSink, Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);

    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    protected QueryExecutionFactory queryExecFactory = null;

    protected UpdateExecutionFactory updateExecFactory = null;

    protected CrawleableUri metadataGraphUri = null;

    private int delay;

    private int attempts;
    

    protected SparqlBasedSink(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory,
            int delay, int attempts) {
        this(queryExecFactory, updateExecFactory);
        this.delay = delay;
        this.attempts = attempts;
    }

    protected SparqlBasedSink(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory) {
        this.queryExecFactory = queryExecFactory;
        this.updateExecFactory = updateExecFactory;
        setMetadataGraphUri(new CrawleableUri(Constants.DEFAULT_META_DATA_GRAPH_URI));
        this.attempts = 1;
    }

    public static SparqlBasedSink create(String sparqlEndpointUrl) {
        return create(sparqlEndpointUrl, null, null, 0, 0);
    }

    public static SparqlBasedSink create(String sparqlEndpointUrl, String username, String password, int delay,
            int attempts) {
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
        return new SparqlBasedSink(queryExecFactory, updateExecFactory, delay, attempts);
    }

    @Override
    public List<Triple> getTriplesForGraph(CrawleableUri uri) {
        Query selectQuery = null;
        // if (uri.equals(metaDataGraphUri)) {
        selectQuery = QueryGenerator.getInstance().getSelectQuery();
        // } else {
        // selectQuery = QueryGenerator.getInstance().getSelectQuery(getGraphId(uri));
        // }

        QueryExecution qe = queryExecFactory.createQueryExecution(selectQuery);
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

    public void deleteTriples() {
        QueryExecution execution = null;
        execution = queryExecFactory.createQueryExecution("DELETE { GRAPH ?g{\n" + "     ?s ?p ?o .}\n" + "}\n"
                + " WHERE { GRAPH ?g{\n" + "     ?s ?p ?o .}\n" + "}\n" + "");

        execution.execSelect();

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
     * @param uri        the crawled {@link CrawleableUri}
     * @param tripleList the list of {@link Triple}s regarding that uri
     */
    protected void sendTriples(CrawleableUri uri, Collection<Triple> triples) {
        for (int i = 1; i <= attempts; i++) {
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
                UpdateProcessor processor = updateExecFactory.createUpdateProcessor(new UpdateRequest(insert).toString()
                        .replaceAll("\\{\\}", "" + "{ SELECT * {OPTIONAL {?s ?p ?o} } LIMIT 1}"));
                processor.execute();
                break;
            } catch (Exception e) {
                if (i == attempts)
                    LOGGER.error("Exception while sending update query. URI: " + uri.getUri().toString(), e);
                else
                    LOGGER.info("An error was caught while inserting triples, trying again, Attempt " + i + " of "
                            + attempts);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e1) {
                }
            }
        }

    }

    /**
     * Method to send all buffered quads to the database
     *
     * @param uri      the crawled {@link CrawleableUri}
     * @param quadList the list of {@link quads}s regarding that uri
     */
    @Override
    protected void sendQuads(CrawleableUri uri, Collection<Quad> quadList) {
        for (int i = 1; i <= attempts; i++) {

            try {
                // Node graph;
                // if (uri.equals(metadataGraphUri)) {
                // graph = NodeFactory.createURI(uri.getUri().toString());
                // } else {
                // graph = NodeFactory.createURI(getGraphId(uri));
                // }
                UpdateDeleteInsert insert = new UpdateDeleteInsert();
                insert.setHasInsertClause(true);
                insert.setHasDeleteClause(false);
                QuadAcc quads = insert.getInsertAcc();
                for (Quad quad : quadList) {
                    quads.addQuad(quad);
                }
                // quads.setGraph(graph);
                UpdateProcessor processor = updateExecFactory.createUpdateProcessor(new UpdateRequest(insert).toString()
                        .replaceAll("\\{\\}", "" + "{ SELECT * {OPTIONAL {?s ?p ?o} } LIMIT 1}"));
                LOGGER.info("Storing " + quadList.size() + " Quads for URI: " + uri.getUri().toString());
                processor.execute();
            } catch (Exception e) {
                if (i == attempts)
                    LOGGER.error("Exception while sending update query. URI: " + uri.getUri().toString(), e);
                else
                    LOGGER.info("An error was caught while inserting quads, trying again, Attempt " + i + " of "
                            + attempts);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e1) {
                }
            }
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
        return Constants.DEFAULT_RESULT_GRAPH_URI_PREFIX + uri.getData(Constants.UUID_KEY).toString();
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

    @Override
    public void flushMetadata() {
        // TODO Auto-generated method stub
        
    }

}
