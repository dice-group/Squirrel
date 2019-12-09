package org.dice_research.squirrel.data.uri.filter;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.dice_research.squirrel.vocab.Squirrel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Query query = QueryFactory.create("SELECT DISTINCT * WHERE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?term. FILTER (str(?term) = \"" + hashValue + "\") }");
        QueryExecution qe = queryExecFactory.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            //TODO: convert to CrawleableUri and add to Set.
        }
        return duplicateUris;
    }

    @Override
    public void addHashValuesForUris(List<CrawleableUri> uris) {
        SparqlBasedSink sink = new SparqlBasedSink(queryExecFactory, updateExecFactory);
        for(CrawleableUri uri:uris) {
            sink.openSinkForUri(uri);
            sink.addTriple(uri, new Triple(Squirrel.ResultGraph.asNode(), RDF.value.asNode(),
                ResourceFactory.createStringLiteral(String.valueOf(uri.getData(Constants.URI_HASH_KEY))).asNode()));
            sink.closeSinkForUri(uri);
        }
    }
}
