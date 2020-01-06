package org.dice_research.squirrel.urifilter;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.modify.request.QuadDataAcc;
import org.apache.jena.sparql.modify.request.UpdateDataInsert;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.vocab.Squirrel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SparqlBasedUriFilter implements UriHashCustodian {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedUriFilter.class);

    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    private QueryExecutionFactory queryExecFactory;

    private UpdateExecutionFactory updateExecFactory;

    public SparqlBasedUriFilter(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory) {
        this.queryExecFactory = queryExecFactory;
        this.updateExecFactory = updateExecFactory;
    }

    @Override
    public Set<CrawleableUri> getUrisWithSameHashValues(Set<HashValue> hashValuesForComparison) {
        return null;
    }

    @Override
    public Set<CrawleableUri> getUrisWithSameHashValues(String hashValue) {
        Set<CrawleableUri> duplicateUris = new HashSet<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ?subject WHERE { GRAPH <");
        stringBuilder.append(Constants.DEFAULT_META_DATA_GRAPH_URI);
        stringBuilder.append("> {");
        stringBuilder.append("?subject ");
        stringBuilder.append("<http://w3id.org/squirrel/vocab#hash>");
        stringBuilder.append(" ");
        stringBuilder.append("\"" + hashValue + "\"");
        stringBuilder.append(" ");
        stringBuilder.append("}}");
        Query query = QueryFactory.create(stringBuilder.toString());
        QueryExecution qe = queryExecFactory.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode subject = sol.get("subject");
            String uri = subject.toString();
            CrawleableUri newUri = null;
            try{
                newUri = new CrawleableUri(new URI(uri));
                newUri.addData(Constants.URI_HASH_KEY, hashValue);
            }catch (URISyntaxException e) {
                LOGGER.error("Error during URI construction " + uri);
            }
            duplicateUris.add(newUri);
        }
        qe.close();
        return duplicateUris;
    }

    @Override
    public void addHashValuesForUris(List<CrawleableUri> uris) {
        Node graph = NodeFactory.createURI(Constants.DEFAULT_META_DATA_GRAPH_URI.toString());
        for (CrawleableUri uri : uris) {
            try {
                if (uri.getData(Constants.URI_HASH_KEY) != null) {
                    Node subject = NodeFactory.createURI(uri.getUri().toString());
                    Node object = NodeFactory.createLiteral(uri.getData(Constants.URI_HASH_KEY).toString());
                    Triple triple = new Triple(subject, Squirrel.hash.asNode(), object);
                    QuadDataAcc quads = new QuadDataAcc();
                    quads.addQuad(new Quad(graph, triple));
                    quads.setGraph(graph);
                    UpdateDataInsert insert = new UpdateDataInsert(quads);
                    UpdateProcessor processor = this.updateExecFactory.createUpdateProcessor(new UpdateRequest(insert));
                    processor.execute();
                    CrawlingActivity activity = (CrawlingActivity) uri.getData(Constants.URI_CRAWLING_ACTIVITY);
                    activity.setHashValue(uri.getData(Constants.URI_HASH_KEY).toString());
                }
            }catch (Exception ex) {
                LOGGER.error("Exception occurred while executing update query", ex);
            }
        }
    }
}
