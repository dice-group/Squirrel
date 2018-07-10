package org.aksw.simba.squirrel.sink.impl.sparql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * This class is used to get some data regarding the {@link SparqlBasedSink}.
 */
public final class SparqlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlUtils.class);

    private static final String UPDATE_SUFFIX = "update";
    private static final String QUERY_SUFFIX = "query";
    private static final String SPARQL_HOST_PORTS_KEY = "SPARQL_HOST_PORT";
    private static final String SPARQL_HOST_CONTAINER_NAME_KEY = "SPARQL_HOST_NAME";

    private static String sparqlDatasetPrefix;

    static {
        Map<String, String> env = System.getenv();
        if (env.containsKey(SPARQL_HOST_CONTAINER_NAME_KEY) && env.containsKey(SPARQL_HOST_PORTS_KEY)) {
            sparqlDatasetPrefix = "http://" + env.get(SPARQL_HOST_CONTAINER_NAME_KEY) + ":" + env.get(SPARQL_HOST_PORTS_KEY) + "/contentset/";
        } else {
            LOGGER.error("Couldn't get " + SPARQL_HOST_CONTAINER_NAME_KEY + " or " + SPARQL_HOST_PORTS_KEY + " from the environment.");
        }
    }

    /**
     * Get the uri for the dataset for updates.
     *
     * @return the uri for the dataset for updates.
     */
    public static String getDatasetUriForUpdate() {
        return sparqlDatasetPrefix + UPDATE_SUFFIX;
    }

    /**
     * Get the uri for the dataset for queries.
     *
     * @return the uri for the dataset for queries.
     */
    public static String getDatasetUriForQuery() {
        return sparqlDatasetPrefix + QUERY_SUFFIX;
    }
}
