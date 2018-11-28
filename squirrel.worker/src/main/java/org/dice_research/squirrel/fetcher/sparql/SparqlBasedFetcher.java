package org.dice_research.squirrel.fetcher.sparql;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.apache.commons.io.FileUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.tika.io.IOUtils;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.dice_research.squirrel.metadata.ActivityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * A simple {@link Fetcher} for SPARQL that tries to get triples from a SPARQL
 * endpoint using the query {@value #SELECT_ALL_TRIPLES_QUERY}.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
public class SparqlBasedFetcher implements Fetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedFetcher.class);

    /**
     * The delay that the system will have between sending two queries.
     */
    private static final int DELAY = 1000;

    private static final String SELECT_ALL_TRIPLES_QUERY = "SELECT ?s ?p ?o {?s ?p ?o}";

    protected int delay = DELAY;
    protected File dataDirectory = FileUtils.getTempDirectory();

    @Override
    public File fetch(CrawleableUri uri) {
        // Check whether we can be sure that it is a SPARQL endpoint
        boolean shouldBeSparql = Constants.URI_TYPE_VALUE_SPARQL.equals(uri.getData(Constants.URI_TYPE_KEY));
        QueryExecutionFactory qef = null;
        QueryExecution execution = null;
        File dataFile = null;
        OutputStream out = null;
        try {
            // Create query execution instance
            qef = initQueryExecution(uri.getUri().toString());
            // create temporary file
            try {
                dataFile = File.createTempFile("fetched_", "", dataDirectory);
                out = new BufferedOutputStream(new FileOutputStream(dataFile));
            } catch (IOException e) {
                LOGGER.error("Couldn't create temporary file for storing fetched data. Returning null.", e);
                return null;
            }
            execution = qef.createQueryExecution(SELECT_ALL_TRIPLES_QUERY);
            ResultSet resultSet = execution.execSelect();
            RDFDataMgr.writeTriples(out, new SelectedTriplesIterator(resultSet));
        } catch (Throwable e) {
            // If this should have worked, print a message, otherwise silently return null
            if (shouldBeSparql) {
                LOGGER.error("Couldn't create QueryExecutionFactory for \"" + uri.getUri() + "\". Returning -1.");
                ActivityUtil.addStep(uri, getClass(), e.getMessage());
            }
            return null;
        } finally {
            IOUtils.closeQuietly(out);
            if (execution != null) {
                execution.close();
            }
            if (qef != null) {
                qef.close();
            }
        }
        ActivityUtil.addStep(uri, getClass());
        return dataFile;
    }

    protected QueryExecutionFactory initQueryExecution(String uri) throws ClassNotFoundException, SQLException {
        QueryExecutionFactory qef;
        qef = new QueryExecutionFactoryHttp(uri);
        qef = new QueryExecutionFactoryDelay(qef, DELAY);
        try {
        	LOGGER.info("Starting to Query uri:" + uri);
            return new QueryExecutionFactoryPaginated(qef, 1000);
        } catch (Exception e) {
            LOGGER.info("Couldn't create Factory with pagination. Returning Factory without pagination. Exception: {}",
                    e.getLocalizedMessage());
            return qef;
        }
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }

    protected static class SelectedTriplesIterator implements Iterator<Triple> {
        private ResultSet resultSet;

        public SelectedTriplesIterator(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        @Override
        public boolean hasNext() {
            return resultSet.hasNext();
        }

        @Override
        public Triple next() {
            QuerySolution solution = resultSet.next();
            return new Triple(solution.get("s").asNode(), solution.get("p").asNode(), solution.get("o").asNode());
        }

    }


}
