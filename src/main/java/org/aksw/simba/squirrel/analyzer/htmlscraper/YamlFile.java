package org.aksw.simba.squirrel.analyzer.htmlscraper;

import java.util.Map;

/**
 * 
 * @author gsjunior
 *
 */
public class YamlFile {
	
	protected YamlFile() {
		
	}
	
	private Map<String,Map<String,Object>> search;
	

	public Map<String, Map<String, Object>> getSearch() {
		return search;
	}

	public void setSearch(Map<String, Map<String, Object>> search) {
		this.search = search;
	}
	
	
	

	

}
