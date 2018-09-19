package org.aksw.simba.squirrel.analyzer.impl.html.scraper;

import java.util.Map;

/**
 * @author gsjunior
 */
public class YamlFile {

    protected YamlFile() {

    }

    private Map<String, Map<String, Object>> file_descriptor;


    public Map<String, Map<String, Object>> getFile_descriptor() {
        return file_descriptor;
    }

    public void setSearch(Map<String, Map<String, Object>> file_descriptor) {
        this.file_descriptor = file_descriptor;
    }


}
