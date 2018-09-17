package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerConfiguration.class);

    private static final String OUTPUT_FOLDER_KEY = "OUTPUT_FOLDER";
    public static final String SPARQL_HOST_PORTS_KEY = "SPARQL_HOST_PORT";
    public static final String SPARQL_HOST_CONTAINER_NAME_KEY = "SPARQL_HOST_NAME";

    private String outputFolder;
    private String sparqlHost;
    private String sqarqlPort;

    private WorkerConfiguration(String outputFolder, String sparqlHost, String sqarqlPort) {
        this.outputFolder = outputFolder;
        this.sqarqlPort = sqarqlPort;
        this.sparqlHost = sparqlHost;
    }

    public static WorkerConfiguration getWorkerConfiguration() throws Exception {
        String outputFolder = getEnv(OUTPUT_FOLDER_KEY, LOGGER);
        if (outputFolder != null) {
            LOGGER.info("The worker will use " + OUTPUT_FOLDER_KEY + " as an output folder.");
            String sparqlHost = getEnv(SPARQL_HOST_CONTAINER_NAME_KEY, LOGGER);
            String sqarqlPort = getEnv(SPARQL_HOST_PORTS_KEY, LOGGER);
            return new WorkerConfiguration(outputFolder, sparqlHost, sqarqlPort);
        } else {
            String msg = "Couldn't get " + OUTPUT_FOLDER_KEY + " from the environment. " +
                "The worker can not be initialized.";
            throw new Exception(msg);
        }
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public String getSparqlHost() {
        return sparqlHost;
    }

    public String getSqarqlPort() {
        return sqarqlPort;
    }
}