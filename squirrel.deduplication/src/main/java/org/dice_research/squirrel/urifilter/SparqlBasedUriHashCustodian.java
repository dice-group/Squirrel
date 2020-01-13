package org.dice_research.squirrel.urifilter;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.modify.request.QuadDataAcc;
import org.apache.jena.sparql.modify.request.UpdateDataInsert;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.vocab.Squirrel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is a sparql based implementation of {@link UriHashCustodian}
 */
public class SparqlBasedUriHashCustodian implements UriHashCustodian {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedUriHashCustodian.class);

    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    private QueryExecutionFactory queryExecFactory;

    private UpdateExecutionFactory updateExecFactory;

    public SparqlBasedUriHashCustodian(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory) {
        this.queryExecFactory = queryExecFactory;
        this.updateExecFactory = updateExecFactory;
    }

    @Override
    public Set<CrawleableUri> getUrisWithSameHashValues(Set<HashValue> hashValuesForComparison) {
        return null;
    }

    @Override
    public Set<String> getUrisWithSameHashValues(String hashValue) {
        Set<String> duplicateUris = new HashSet<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ?subject WHERE { GRAPH <");
        stringBuilder.append(Constants.DEFAULT_META_DATA_GRAPH_URI);
        stringBuilder.append("> { ?subject <");
        stringBuilder.append(Squirrel.hash);
        stringBuilder.append(">\"");
        stringBuilder.append(hashValue);
        stringBuilder.append("\" }}");
        Query query = QueryFactory.create(stringBuilder.toString());
        QueryExecution qe = queryExecFactory.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            duplicateUris.add(sol.get("subject").toString());
        }
        qe.close();
        return duplicateUris;
    }

    @Override
    public void addHashValuesForUris(List<CrawleableUri> uris) {
        Node graph = NodeFactory.createURI(Constants.DEFAULT_META_DATA_GRAPH_URI.toString());
        QuadDataAcc quads = new QuadDataAcc();
        for (CrawleableUri uri : uris) {
            if (uri.getData(Constants.URI_HASH_KEY) != null) {
                Node subject = NodeFactory.createURI(uri.getUri().toString());
                Node object = NodeFactory.createLiteral(uri.getData(Constants.URI_HASH_KEY).toString());
                Triple triple = new Triple(subject, Squirrel.hash.asNode(), object);
                quads.addQuad(new Quad(graph, triple));
            }
        }
        quads.setGraph(graph);
        UpdateDataInsert insert = new UpdateDataInsert(quads);
        UpdateProcessor processor = this.updateExecFactory.createUpdateProcessor(new UpdateRequest(insert));
        processor.execute();
    }
}
