package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeedConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeedConfiguration.class);

    private static final String SEED_FILE_KEY = "SEED_FILE";
    private String seedFile = null;

    private SeedConfiguration(String seedFile) {
        this.seedFile = seedFile;
    }

    public static SeedConfiguration getSeedConfiguration() {
        String seedFile = getEnvSeedFile();
        if (seedFile != null) {
            LOGGER.info("{} found, will use seed file to initialize frontier...", SEED_FILE_KEY);
            return new SeedConfiguration(seedFile);
        } else {
            LOGGER.info("Couldn't get {} from the environment. " +
                "Proceeding without seed file initialization.", SEED_FILE_KEY);
            return null;
        }
    }

    private static String getEnvSeedFile() {
        return getEnv(SEED_FILE_KEY, LOGGER);
    }

    public String getSeedFile() {
        return seedFile;
    }
}
