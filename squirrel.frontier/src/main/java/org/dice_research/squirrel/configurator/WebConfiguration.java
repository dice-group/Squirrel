package org.dice_research.squirrel.configurator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfiguration.class);

    private boolean communicationWithWebserviceEnabled;
    private boolean visualizationOfCrawledGraphEnabaled;

    private static final String COMMUNICATION_WITH_WEBSERVICE = "COMMUNICATION_WITH_WEBSERVICE";
    private static final String VISUALIZATION_OF_CRAWLED_GRAPH = "VISUALIZATION_OF_CRAWLED_GRAPH";

    private WebConfiguration(boolean communicationWithWebserviceEnabled, boolean visualizationOfCrawledGraphEnabaled) {
        this.communicationWithWebserviceEnabled = communicationWithWebserviceEnabled;
        this.visualizationOfCrawledGraphEnabaled = visualizationOfCrawledGraphEnabaled;
    }

    public static WebConfiguration getWebConfiguration() {
        return new WebConfiguration(Configuration.getEnvBoolean(COMMUNICATION_WITH_WEBSERVICE, LOGGER), Configuration.getEnvBoolean(VISUALIZATION_OF_CRAWLED_GRAPH, LOGGER));
    }

    public boolean isCommunicationWithWebserviceEnabled() {
        return communicationWithWebserviceEnabled;
    }

    public boolean isVisualizationOfCrawledGraphEnabled() {
        return visualizationOfCrawledGraphEnabaled;
    }
}
