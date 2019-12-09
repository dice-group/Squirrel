package org.dice_research.squirrel.data.uri.filter;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
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
//        Query selectQuery = QueryGenerator.getInstance().getSelectQuery();
//        QueryExecution qe = queryExecFactory.createQueryExecution(selectQuery);
//        ResultSet rs = qe.execSelect();
//        List<Triple> triplesFound = new ArrayList<>();
//        while (rs.hasNext()) {
//            QuerySolution sol = rs.nextSolution();
//            RDFNode subject = sol.get("subject");
//            RDFNode predicate = sol.get("predicate");
//            RDFNode object = sol.get("object");
//            triplesFound.add(Triple.create(subject.asNode(), predicate.asNode(), object.asNode()));
//        }
//        qe.close();

        Set<CrawleableUri> uris = new HashSet<>();
        Query query = QueryFactory.create("SELECT distinct * WHERE{ ?uri rdfs:label ?label filter ()}");
        QueryExecution qe = queryExecFactory.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {

        }
        return uris;
    }

    @Override
    public void addHashValuesForUris(List<CrawleableUri> uris) {

    }
}
