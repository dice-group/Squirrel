package org.dice_research.squirrel.data.uri.filter;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SPARQLKnownUriFilter implements KnownUriFilter, Closeable, UriHashCustodian {

    private static final Logger LOGGER = LoggerFactory.getLogger(SPARQLKnownUriFilter.class);

    private SPARQLConnector connector = null;

    public SPARQLKnownUriFilter() {
//        this.connector = new SPARQLConnector(username, password, endpointUrl);
    }

    public List<String> getGeneratedUris(QueryExecutionFactory queryExecFactory){
        String queryString = "SELECT ?s ?p ?o\n" +
                "WHERE {\n" +
                " GRAPH <http://w3id.org/squirrel/metadata> {?s prov:value ?o}\n" +
                "}";
        LOGGER.info("Query looks like: ", queryString);

        QueryExecution qe = queryExecFactory.createQueryExecution(queryString);
        LOGGER.warn("Query execution: ", qe);

        ResultSet rs = qe.execSelect();
        List<String> urisFound = new ArrayList<>();
        System.out.println("-------------------------------------------------------------------------------------------------------");
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode object = sol.get("object");
            urisFound.add(object.asLiteral().toString());
        }
        qe.close();
        return urisFound;
    }
    @Override
    public void close() throws IOException {

    }

    @Override
    public void add(CrawleableUri uri, long nextCrawlTimestamp) {

    }

    @Override
    public void add(CrawleableUri uri, long lastCrawlTimestamp, long nextCrawlTimestamp) {

    }

    @Override
    public List<CrawleableUri> getOutdatedUris() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        return false;
    }

    @Override
    public Set<CrawleableUri> getUrisWithSameHashValues(Set<HashValue> hashValuesForComparison) {
        return null;
    }

    @Override
    public void addHashValuesForUris(List<CrawleableUri> uris) {

    }

}
