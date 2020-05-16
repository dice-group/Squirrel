package org.dice_research.squirrel.queue;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIGraphSizeBasedQueue extends AbstractURIScoreBasedQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(URIGraphSizeBasedQueue.class);

    protected QueryExecutionFactory queryExecFactory = null;

    public URIGraphSizeBasedQueue() {
    }

    public URIGraphSizeBasedQueue(QueryExecutionFactory qe) {
        this.queryExecFactory = qe;
    }

    protected float getURIScore(CrawleableUri uri) {
        int uriScore = getGraphSize(uri.getUri().toString());
        if(uriScore == 0) {
            return 1;
        }
        return 1 / (float)uriScore;
    }

    private int getGraphSize(String uri) {
        String query = "SELECT (COUNT(*) AS ?C) WHERE { GRAPH <" + Constants.DEFAULT_META_DATA_GRAPH_URI +  "> { <" + uri + "> ?p ?o } }";
        try (QueryExecution execution = queryExecFactory.createQueryExecution(query)) {
            ResultSet resultSet = execution.execSelect();
            if(resultSet.hasNext()){
                QuerySolution solution = resultSet.next();
                Literal countNode = solution.getLiteral("C");
                ResultSetFormatter.out(resultSet);
                return countNode.getInt();
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while querying Sparql for duplicity of URL", e);
        }
        return 0;
    }

}
