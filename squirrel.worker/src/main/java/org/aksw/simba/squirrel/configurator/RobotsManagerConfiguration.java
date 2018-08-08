package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotsManagerConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotsManagerConfiguration.class);

    private static final String MIN_DELAY_KEY = "MIN_DELAY";
    private Long minDelay = null;

    private RobotsManagerConfiguration(Long minDelay) {
        this.minDelay = minDelay;
    }

    public static RobotsManagerConfiguration getRobotsManagerConfiguration() throws Exception {
        Long minDelay = getEnvMinDelay();
        if (minDelay != null) {
            LOGGER.info("RobotsManager will use " + MIN_DELAY_KEY + " as minimum delay parameter.");
            return new RobotsManagerConfiguration(minDelay);
        } else {
            String msg = "Couldn't get " + MIN_DELAY_KEY + " from the environment. " +
                "The RobotsManager will use default minimum delay parameter.";
            return null;
        }
    }

    private static Long getEnvMinDelay() {
        return getEnvLong(MIN_DELAY_KEY, LOGGER);
    }

    public Long getMinDelay() {
        return minDelay;
    }
}
