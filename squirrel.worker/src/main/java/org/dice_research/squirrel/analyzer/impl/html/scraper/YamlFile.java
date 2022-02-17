package org.dice_research.squirrel.analyzer.impl.html.scraper;

import java.util.Map;

/**
 * @author gsjunior
 */
public class YamlFile implements Cloneable {


    private Map<String, Map<String, Object>> file_descriptor;


    public Map<String, Map<String, Object>> getFile_descriptor() {
        return file_descriptor;
    }

    public void setSearch(Map<String, Map<String, Object>> file_descriptor) {
        this.file_descriptor = file_descriptor;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
    	// TODO Auto-generated method stub
    	return super.clone();
    }


}
