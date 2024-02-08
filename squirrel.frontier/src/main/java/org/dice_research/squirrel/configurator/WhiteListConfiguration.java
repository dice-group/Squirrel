package org.dice_research.squirrel.configurator;

import org.dice_research.squirrel.configurator.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class WhiteListConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(WhiteListConfiguration.class);

    private static final String URI_WHITELIST_FILE = "URI_WHITELIST_FILE";

    private String whiteListURI = null;

    private WhiteListConfiguration(String whiteListURI) {
        this.whiteListURI = whiteListURI;
    }

    public static WhiteListConfiguration getWhiteListConfiguration() {
        String whiteListURI = getEnvWhiteListURI();
        if (whiteListURI != null) {
            LOGGER.warn("{} found, loading it...", URI_WHITELIST_FILE);
            return new WhiteListConfiguration(whiteListURI);
        } else {
            LOGGER.warn("Couldn't get {} from the environment.", URI_WHITELIST_FILE);
            return null;
        }
    }

    private static String getEnvWhiteListURI() {
        return getEnv(URI_WHITELIST_FILE, LOGGER);
    }

    public String getWhiteListURI() {
        return whiteListURI;
    }

}
