package org.dice_research.squirrel.analyzer.impl.html.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.apache.commons.io.FilenameUtils;
import org.dice_research.squirrel.analyzer.impl.html.scraper.exceptions.ElementNotFoundException;
import org.dice_research.squirrel.configurator.HtmlScraperConfiguration;
import org.dice_research.squirrel.utils.TempPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Parser for Yaml Files, for the HTML Scraper
 * 
 * @author gsjunior
 */
public class YamlFilesParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(YamlFilesParser.class);

    private Map<String, YamlFile> yfs = null;
    private final String fileExtension = "yaml";

    protected YamlFilesParser(File file)
        throws Exception {
        yfs = loadFiles(file);
    }

    protected YamlFilesParser()
        throws Exception {

        yfs = loadFiles(null);
    }

    private Map<String, YamlFile> loadFiles(File file) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File folder = null;
        Map<String, YamlFile> yamls = new HashMap<String, YamlFile>();
        if (file != null) {
            folder = file;
        } else if (HtmlScraperConfiguration.getHtmlScraperConfiguration() != null) {
            folder = new File(HtmlScraperConfiguration.getHtmlScraperConfiguration().getPath());
        }

        if (folder != null) {

            List<File> listYamlFiles = filterYamlFiles(TempPathUtils.searchPath4Files(folder));

            for (int i = 0; i < listYamlFiles.size(); i++) {
                try {

                    YamlFile yamlFile = mapper.readValue(listYamlFiles.get(i), YamlFile.class);

                    for (Entry<String, Map<String, Object>> entry : yamlFile.getFile_descriptor().entrySet()) {

                        if (entry.getKey().equals(YamlFileAtributes.SEARCH_CHECK))
                            continue;

                        if (!(entry.getValue().containsKey(YamlFileAtributes.REGEX) &&
                            entry.getValue().containsKey(YamlFileAtributes.RESOURCES))) {
                            throw new ElementNotFoundException("Regex or Resources not found. Please check the Yaml Files");
                        }
                    }

                    yamls.put(yamlFile.getFile_descriptor().get(YamlFileAtributes.SEARCH_CHECK).get(YamlFileAtributes.SEARCH_DOMAIN).toString(),
                        yamlFile);
                } catch (Exception e) {
                    LOGGER.error("An error occurred while parsing the file", e);
                    throw new Exception(e);
                }
            }

        }
        return yamls;
    }

    private List<File> filterYamlFiles(List<File> yamlList) {
        return yamlList.stream().filter(p -> FilenameUtils.getExtension(p.getAbsolutePath()).equals(fileExtension))
            .collect(Collectors.toList());

    }

    public Map<String, YamlFile> getYamlFiles() {

        return yfs;

    }


}
