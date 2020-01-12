package org.dice_research.squirrel.deduplication.sink;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.modify.request.QuadDataAcc;
import org.apache.jena.sparql.modify.request.UpdateDataInsert;
import org.apache.jena.sparql.modify.request.UpdateDeleteInsert;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.sparqlBased.SparqlBasedSink;
import org.dice_research.squirrel.vocab.Squirrel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class is responsible for actions on graphs associated with {@link CrawleableUri}
 *
 */
public class DeduplicationSink extends SparqlBasedSink {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeduplicationSink.class);

    public DeduplicationSink(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory) {
        super(queryExecFactory, updateExecFactory);
    }

    /**
     * This method drops the graph for the uri
     *
     * @param uri The uri for which the Graph has to be dropped
     */
    public void dropGraph(CrawleableUri uri) {
        LOGGER.info("Dropping Graph: " + getGraphId(uri));
        String queryBuilder = "DROP GRAPH <" + getGraphId(uri) + "> ;";
        UpdateRequest request = UpdateFactory.create(queryBuilder);
        updateExecFactory.createUpdateProcessor(request).execute();
    }

    /**
     * This method updates the graph id for the uri in metadata graph
     *
     * @param uriNew The uri for which the Graph Id has to be updated
     * @param graphId The graph id to be updated to the uri
     */
    public void updateGraphForUri(CrawleableUri uriNew, String graphId) {
        UpdateDeleteInsert update = new UpdateDeleteInsert();
        update.setHasInsertClause(true);
        update.setHasDeleteClause(true);
        String queryString = getUpdateHashQuery(uriNew.getUri().toString(), graphId);
        UpdateRequest request = UpdateFactory.create();
        request.add(String.valueOf(queryString));
        updateExecFactory.createUpdateProcessor(request).execute();
    }

    /**
     * This method adds the graph ids for uris in metadata graph
     *
     * @param uris list of uris for which the graph id has to be added
     */
    public void addGraphIdForURIs(List<CrawleableUri> uris) {
        Node graph = NodeFactory.createURI(Constants.DEFAULT_META_DATA_GRAPH_URI.toString());
        QuadDataAcc quads = new QuadDataAcc();
        for (CrawleableUri uri : uris) {
            if (!StringUtils.isEmpty(getGraphId(uri))) {
                Node sub = NodeFactory.createURI(getGraphId(uri));
                Node obj = NodeFactory.createURI(uri.getUri().toString());
                Triple triple = new Triple(sub, Squirrel.containsDataOf.asNode(), obj);
                quads.addQuad(new Quad(graph, triple));
            }
        }
        quads.setGraph(graph);
        UpdateDataInsert insert = new UpdateDataInsert(quads);
        UpdateProcessor processor = this.updateExecFactory.createUpdateProcessor(new UpdateRequest(insert));
        processor.execute();
    }

    /**
     * This method returns the graph id of the uri
     *
     * @param uri The uri for which the Graph Id has to be fetched
     * @return Graph Id of the URI
     */
    public String getGraphIdFromSparql(String uri) {
        Query query = getGraphIdQuery(uri);
        QueryExecution qe = queryExecFactory.createQueryExecution(query);
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

    /**
     * This methods builds a query to update the graph id of a URI
     *
     * @param uri the uri for which the graph id has to be updated
     * @param graphId the graph id to be updated for the uri
     * @return query string
     */
    public String getUpdateHashQuery(String uri, String graphId) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("DELETE { GRAPH <");
        queryString.append(Constants.DEFAULT_META_DATA_GRAPH_URI);
        queryString.append("> { ?subject <");
        queryString.append(Squirrel.containsDataOf);
        queryString.append("> <");
        queryString.append(uri);
        queryString.append("> }} INSERT { GRAPH <");
        queryString.append(Constants.DEFAULT_META_DATA_GRAPH_URI);
        queryString.append("> { <");
        queryString.append(graphId);
        queryString.append("> <");
        queryString.append(Squirrel.containsDataOf);
        queryString.append("> <");
        queryString.append(uri);
        queryString.append("> } } WHERE { GRAPH <");
        queryString.append(Constants.DEFAULT_META_DATA_GRAPH_URI);
        queryString.append("> { ?subject <");
        queryString.append(Squirrel.containsDataOf);
        queryString.append("> <");
        queryString.append(uri);
        queryString.append("> } }");
        return queryString.toString();
    }

    /**
     * This method returns a query to fetch the graph id of the uri.
     *
     * @param uri the uri whose graph id is being queried
     * @return query
     */
    public Query getGraphIdQuery(String uri) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT ?subject WHERE { GRAPH <");
        queryString.append(Constants.DEFAULT_META_DATA_GRAPH_URI);
        queryString.append("> { ?subject <");
        queryString.append(Squirrel.containsDataOf);
        queryString.append("> <");
        queryString.append(uri);
        queryString.append(">} }");
        return QueryFactory.create(queryString.toString());
    }
}
