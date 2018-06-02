package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerConfiguration.class);

    private static final String OUTPUT_FOLDER_KEY = "OUTPUT_FOLDER";
    private String outputFolder = null;

    private WorkerConfiguration(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public static WorkerConfiguration getWorkerConfiguration() throws Exception {
        String outputFolder = getEnvOutputFolder();
        if(outputFolder != null) {
            LOGGER.info("The worker will use " + OUTPUT_FOLDER_KEY + " as an output folder.");
            return new WorkerConfiguration(outputFolder);
        } else {
            String msg = "Couldn't get " + OUTPUT_FOLDER_KEY + " from the environment. " +
                "The worker can not be initialized.";
            throw new Exception(msg);
        }
    }

    private static String getEnvOutputFolder() {
        return getEnv(OUTPUT_FOLDER_KEY, LOGGER);
    }

    public String getOutputFolder() {
        return outputFolder;
    }
}
