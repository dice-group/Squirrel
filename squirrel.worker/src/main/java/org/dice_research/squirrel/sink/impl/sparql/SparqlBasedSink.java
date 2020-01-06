package org.dice_research.squirrel.sink.impl.sparql;

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
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.DatasetDescription;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.QuadDataAcc;
import org.apache.jena.sparql.modify.request.UpdateDataInsert;
import org.apache.jena.sparql.modify.request.UpdateDeleteInsert;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.sink.tripleBased.AdvancedTripleBasedSink;
import org.dice_research.squirrel.vocab.Squirrel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A sink which stores the data in different graphs in a sparql based db.
 */

public class SparqlBasedSink extends AbstractBufferingTripleBasedSink implements AdvancedTripleBasedSink, Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);

    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    protected static QueryExecutionFactory queryExecFactory = null;

    protected static UpdateExecutionFactory updateExecFactory = null;

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
    public List<Triple> getTriplesForGraph(CrawleableUri uri) {
        Query selectQuery = null;
        if (uri.equals(metadataGraphUri)) {
            selectQuery = QueryGenerator.getInstance().getSelectQuery();
        } else {
            selectQuery = QueryGenerator.getInstance().getSelectQuery(getGraphId(uri));
        }

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

    /**
     * This method returns the graph id of the uri
     *
     * @param uri The uri for which the Graph Id has to be fetched
     * @return Graph Id of the URI
     */
    public String getGraphIdFromSparql(CrawleableUri uri) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT ?subject WHERE { GRAPH <");
        queryString.append(Constants.DEFAULT_META_DATA_GRAPH_URI);
        queryString.append("> {");
        queryString.append("?subject <" + Squirrel.containsDataOf + ">" +" <");
        queryString.append(uri.getUri().toString());
        queryString.append(">} ");
        queryString.append("}");
        Query query = QueryFactory.create(queryString.toString());
        QueryExecution qe = SparqlBasedSink.queryExecFactory.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        RDFNode graphId = null;
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            graphId = sol.get("subject");
        }
        qe.close();
        if(graphId != null) {
            return graphId.toString();
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

    @Override
    public void dropGraph(CrawleableUri uri) {
        LOGGER.info("Dropping Graph: " + getGraphId(uri));
        String querybuilder = "DROP GRAPH <" + getGraphId(uri) + "> ;";
        UpdateRequest request = UpdateFactory.create(querybuilder);
        updateExecFactory.createUpdateProcessor(request).execute();
    }

    @Override
    public void updateGraphForUri(CrawleableUri uriNew, CrawleableUri uriOld) {
        String graphId = "";
        if(StringUtils.isEmpty(getGraphId(uriOld))) {
            graphId = getGraphIdFromSparql(uriOld);
        }
        UpdateDeleteInsert update = new UpdateDeleteInsert();
        update.setHasInsertClause(true);
        update.setHasDeleteClause(true);
        StringBuilder queryString = new StringBuilder();
        queryString.append("DELETE { GRAPH <");
        queryString.append(Constants.DEFAULT_META_DATA_GRAPH_URI);
        queryString.append("> { ?subject <"+ Squirrel.containsDataOf +"> <");
        queryString.append(uriNew.getUri().toString());
        queryString.append("> }}");
        queryString.append("INSERT { GRAPH <");
        queryString.append(Constants.DEFAULT_META_DATA_GRAPH_URI);
        queryString.append("> { <");
        queryString.append(graphId);
        queryString.append("> <"+ Squirrel.containsDataOf +"> <");
        queryString.append(uriNew.getUri().toString());
        queryString.append("> } }");
        queryString.append(" WHERE { GRAPH <");
        queryString.append(Constants.DEFAULT_META_DATA_GRAPH_URI);
        queryString.append("> { ?subject <");
        queryString.append(Squirrel.containsDataOf + "> <");
        queryString.append(uriNew.getUri().toString());
        queryString.append("> } }");
        UpdateRequest request = UpdateFactory.create();
        request.add(String.valueOf(queryString));
        updateExecFactory.createUpdateProcessor(request).execute();
        uriNew.addData(Constants.UUID_KEY, getGraphId(uriOld));
    }

    @Override
    public void addGraphIdForURIs(List<CrawleableUri> uris) {
        Node graph = NodeFactory.createURI(Constants.DEFAULT_META_DATA_GRAPH_URI.toString());
        for (CrawleableUri uri : uris) {
            try {
                if (!StringUtils.isEmpty(getGraphId(uri))) {
                    Node sub = NodeFactory.createURI(getGraphId(uri));
                    Node obj = NodeFactory.createURI(uri.getUri().toString());
                    Triple triple = new Triple(sub, Squirrel.containsDataOf.asNode(), obj);
                    QuadDataAcc quads = new QuadDataAcc();
                    quads.addQuad(new Quad(graph, triple));
                    quads.setGraph(graph);
                    UpdateDataInsert insert = new UpdateDataInsert(quads);
                    UpdateProcessor processor = this.updateExecFactory.createUpdateProcessor(new UpdateRequest(insert));
                    processor.execute();
                }
            }catch (Exception ex) {
                LOGGER.error("Exception during updating", ex);
            }
        }
    }
}
