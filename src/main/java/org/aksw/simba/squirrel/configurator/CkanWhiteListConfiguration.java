package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The CKAN URL list for CKAN Crawler
 *
 * @author Varun Maitreya Eranki
 */

public class CkanWhiteListConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CkanWhiteListConfiguration.class);

    private static final String CKAN_WHITELIST_FILE = "CKAN_WHITELIST_FILE";

    private String ckanwhiteListURI = null;

    private CkanWhiteListConfiguration(String ckanwhiteListURI) {
        this.ckanwhiteListURI = ckanwhiteListURI;
    }

    public static CkanWhiteListConfiguration getCkanWhiteListConfiguration() {
        String ckanwhiteListURI = getEnvCkanWhiteListURI();
        if (ckanwhiteListURI != null) {
            LOGGER.warn("{} found, loading it...", CKAN_WHITELIST_FILE);
            return new CkanWhiteListConfiguration(ckanwhiteListURI);
        } else {
            LOGGER.warn("Couldn't get {} from the environment.", CKAN_WHITELIST_FILE);
            return null;
        }
    }

    private static String getEnvCkanWhiteListURI() {
        return getEnv(CKAN_WHITELIST_FILE, LOGGER);
    }

    public String getCkanWhiteListURI() {
        return ckanwhiteListURI;
    }


}
