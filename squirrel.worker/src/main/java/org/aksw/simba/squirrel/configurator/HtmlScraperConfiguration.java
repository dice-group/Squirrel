package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gsjunior
 */
public class HtmlScraperConfiguration extends Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlScraperConfiguration.class);

    private String path;

    private static final String HTML_SCRAPER_YAML_PATH = "HTML_SCRAPER_YAML_PATH";

    public static HtmlScraperConfiguration getHtmlScraperConfiguration() {
        String path = getEnv(HTML_SCRAPER_YAML_PATH, LOGGER);
        if (path != null) {
            return new HtmlScraperConfiguration(path);
        } else {
            return null;
        }
    }

    private HtmlScraperConfiguration(String path) {
        this.path = path;
    }

    public static String getYamlFilesPath() {
        return HTML_SCRAPER_YAML_PATH;
    }

    public String getPath() {
        return path;
    }


}
