package org.aksw.simba.squirrel.analyzer.htmlscraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.aksw.simba.squirrel.analyzer.htmlscraper.exceptions.ElementNotFoundException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



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
//			model = ModelFactory.createDefaultModel();
		} catch (Exception e) {
			
		}

	}
	
	@SuppressWarnings("unchecked")
	public void scrape(String uri) throws IOException, ElementNotFoundException {
		
		for(YamlFile yamlFile : yamlFiles) {
			
			String url = yamlFile.getSearch().get(YamlFileAtributes.SEARCH_CHECK)
					.get(YamlFileAtributes.SEARCH_CHECK_URL).toString();
			
			List<String> terms = (ArrayList<String>) yamlFile.getSearch().get(YamlFileAtributes.SEARCH_CHECK)
					.get(YamlFileAtributes.SEARCH_CHECK_TERMS);
			
			for(String term : terms) {
				Document doc = Jsoup.connect(url+term).get();
				
				
				Elements elements = doc.select(yamlFile.getSearch().get(
						YamlFileAtributes.SEARCH_RESULT).get(YamlFileAtributes.SEARCH_RESULT_SELECTOR).toString());
				
				for (int i = 0; i < elements.size(); i++) {
					Element element = elements.get(i);
					String downloadLink = yamlFile.getDataset_page()
							.get(YamlFileAtributes.DATASET_RULES).get(YamlFileAtributes.DATASET_RULES_DOWNLOADLINK).toString();
					String href = element.select(downloadLink).attr("abs:href");
					scrapeDownloadLink(yamlFile, href);
				}

			}
		}
		
	}
	
	private List<Triple> scrapeDownloadLink(YamlFile yamlFile, String href) throws IOException, ElementNotFoundException {
		Document doc = Jsoup.connect(href).get();
		String pageUrl = href.substring(0, href.indexOf("?"));
		
		Node s = NodeFactory.createBlankNode(pageUrl);
		
		List<Triple> listTriples = new ArrayList<Triple>();
		
		for(Entry<String, Object> entry: 
			yamlFile.getDownload_page().get(YamlFileAtributes.DOWNLOAD_PAGE_RESOURCES).entrySet()) {
			
			Node p = NodeFactory.createBlankNode(entry.getKey());
			
			Elements elements = doc.select(entry.getValue().toString());
			
			String object = "";
			
			System.out.println(entry.getKey() + " - " + entry.getValue());
			
			if (elements.size() > 1) {
				throw new ElementNotFoundException("Element (" + entry.getKey() + " -> "+ entry.getValue() + ")"
						+ " not found. Check selector syntax");
			}else {
				if(elements.get(0).hasAttr("href")) {
					object = doc.select(entry.getValue().toString()).attr("abs:href");
				}else {
					object = doc.select(entry.getValue().toString()).text();
				}
			}

			Node o = NodeFactory.createBlankNode(object);
			
			Triple triple = new Triple(s, p, o);
			listTriples.add(triple);
		}
		

		return listTriples;
	}


}
