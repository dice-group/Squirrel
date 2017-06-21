package org.aksw.simba.squirrel.fetcher.sparql;

import java.sql.SQLException;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.fetcher.Fetcher;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlBasedFetcher implements Fetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedFetcher.class);

    /**
     * The delay that the system will have between sending two queries.
     */
    private static final int DELAY = 5000;

    private static final String SELECT_ALL_TRIPLES_QUERY = "SELECT ?s ?p ?o {?s ?p ?o}";

    @Override
    public int fetch(CrawleableUri uri, Sink sink) {
        QueryExecutionFactory qef = null;
        try {
            qef = initQueryExecution(uri.getUri().toString());
        } catch (Exception e) {
            LOGGER.error("Couldn't create QueryExecutionFactory for \"" + uri.getUri() + "\". Returning -1.");
            return -1;
        }
        QueryExecution execution = qef.createQueryExecution(SELECT_ALL_TRIPLES_QUERY);
        ResultSet resultSet = execution.execSelect();
        QuerySolution solution;
        int tripleCount = 0;
        sink.openSinkForUri(uri);
        while (resultSet.hasNext()) {
            solution = resultSet.next();
            sink.addTriple(uri,
                    new Triple(solution.get("s").asNode(), solution.get("p").asNode(), solution.get("o").asNode()));
            ++tripleCount;
        }
        execution.close();
        sink.closeSinkForUri(uri);
        return tripleCount;
    }

    protected QueryExecutionFactory initQueryExecution(String uri) throws ClassNotFoundException, SQLException {
        QueryExecutionFactory qef;
        qef = new QueryExecutionFactoryHttp(uri);
        qef = new QueryExecutionFactoryDelay(qef, DELAY);
        try {
            return new QueryExecutionFactoryPaginated(qef, 100);
        } catch (Exception e) {
            LOGGER.warn("Couldn't create Factory with pagination. Returning Factory without pagination. Exception: {}",
                    e.getLocalizedMessage());
            return qef;
        }
    }
}
