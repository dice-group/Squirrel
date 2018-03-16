package org.aksw.simba.squirrel.analyzer.htmlscraper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aksw.simba.squirrel.analyzer.htmlscraper.exceptions.ElementNotFoundException;
import org.aksw.simba.squirrel.configurator.HtmlScraperConfiguration;
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
	
	private Map<String, YamlFile> files = new HashMap<String, YamlFile>();
	
	
	
	public HtmlScraper(File file) {
		try {
			yamlFiles = new YamlFilesParser(file).getYamlFiles();
		} catch (Exception e) {
			
		}
	}
	
	public HtmlScraper() {
		try {
			yamlFiles = new YamlFilesParser().getYamlFiles();
		} catch (Exception e) {
			
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<Triple> scrape(String uri, File filetToScrape) throws Exception {
		
		List<Triple> listTriples = new ArrayList<Triple>();
				
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
					listTriples.addAll(scrapeDownloadLink(yamlFile, href));
				}

			}
		}
		return listTriples;
	}
	
	private Set<Triple> scrapeDownloadLink(YamlFile yamlFile, String href) throws Exception {
		Document doc = Jsoup.connect(href).get();
		String pageUrl = href.substring(0, href.indexOf("?"));
		
		Node s = NodeFactory.createBlankNode(pageUrl);
		
		Set<Triple> listTriples = new LinkedHashSet<Triple>();
		
		Map<String,Object> download_resources = new
				HashMap<String,Object>(yamlFile.getDownload_page().get(YamlFileAtributes.DOWNLOAD_PAGE_RESOURCES));

		List<String> resourcesList = new ArrayList<String>();
		Node objectNode;
		for(Entry<String, Object> entry: 
			download_resources.entrySet()) {
			resourcesList.clear();
			
			Node p = NodeFactory.createBlankNode(entry.getKey());
			
			if(entry.getValue() instanceof List<?> && ((ArrayList<String>) entry.getValue()).size() > 1) {
				resourcesList = (ArrayList<String>) entry.getValue();
			} else {
				resourcesList.add(entry.getValue().toString());
			}
			
			for(String resource: resourcesList) {
				Elements elements = null;
				
				try {
					elements = doc.select(resource);
					if(elements.isEmpty()) {
						throw new ElementNotFoundException("Element (" + entry.getKey() + " -> "+ resource + ")"
								+ " not found. Check selector syntax");
						
					}
				}catch(Exception e) {
					throw new Exception(e);
				}
				
				
				
				for(int i=0; i<elements.size();i++) {
					if(elements.get(i).hasAttr("href")) {
						objectNode = NodeFactory.createURI(elements.get(i).attr("abs:href"));
					}else {
						objectNode = NodeFactory.createLiteral(elements.get(i).text());
					}
					
					Triple triple = new Triple(s, p, objectNode);
					listTriples.add(triple);
				}
	
			}
		}
		

		return listTriples;
	}


}
