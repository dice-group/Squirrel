package org.aksw.simba.squirrel.analyzer.htmlscraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 
 * @author gsjunior
 *
 */
public class HtmlScraper {
	
	private List<YamlFile> yamlFiles;
	
	
	public HtmlScraper() {
		try {
			yamlFiles = new YamlFilesParser().getYamlFiles();
		} catch (Exception e) {
			
		}

	}
	
	@SuppressWarnings("unchecked")
	public void scrape(String uri) throws IOException {
		
		for(YamlFile yamlFile : yamlFiles) {
			String url = yamlFile.getSearch().get("check").get("url").toString();
			List<String> terms = (ArrayList<String>) yamlFile.getSearch().get("check").get("terms");
			
			for(String term : terms) {
				Document doc = Jsoup.connect(url+term).get();
				System.out.println(doc.title() + " - " + term);

			}
		}
		
		
		
		
		
	}

}
