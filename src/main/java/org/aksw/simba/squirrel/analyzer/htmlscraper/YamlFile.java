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
	
	private Map<String,Map<String,Object>> dataset_page;

	private Map<String,Map<String,Object>> download_page;
	
	public Map<String, Map<String, Object>> getDownload_page() {
		return download_page;
	}

	public void setDownload_page(Map<String, Map<String, Object>> download_page) {
		this.download_page = download_page;
	}

	public Map<String, Map<String, Object>> getDataset_page() {
		return dataset_page;
	}

	public void setDataset_page(Map<String, Map<String, Object>> dataset_page) {
		this.dataset_page = dataset_page;
	}

	public Map<String, Map<String, Object>> getSearch() {
		return search;
	}

	public void setSearch(Map<String, Map<String, Object>> search) {
		this.search = search;
	}
	
	
	

	

}
