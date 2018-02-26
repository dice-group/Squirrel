package org.aksw.simba.squirrel.analyzer.htmlscraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlScraper {
	
	protected HtmlScraper() {
		
	}
	
	@SuppressWarnings("unchecked")
	public void scrape(YamlFile yamlFile) throws IOException {
		
		String url = yamlFile.getSearch().get("check").get("url").toString();
		List<String> terms = (ArrayList<String>) yamlFile.getSearch().get("check").get("terms");
		
		for(String term : terms) {
			Document doc = Jsoup.connect(url+term).get();
			System.out.println(doc.title());
		}
		
		
		
	}

}
