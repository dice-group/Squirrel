package org.dice_research.squirrel.analyzer.impl.html.scraper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice_research.squirrel.analyzer.impl.html.scraper.exceptions.ElementNotFoundException;
import org.dice_research.squirrel.data.uri.UriUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author gsjunior
 */
public class HtmlScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlScraper.class);

    private Map<String, YamlFile> yamlFiles = new HashMap<String, YamlFile>();


    public HtmlScraper(File file) {
        try {
            yamlFiles = new YamlFilesParser(file).getYamlFiles();
        } catch (Exception e) {
            LOGGER.error("An error occurred when trying to scrape HTML files", e);
        }
    }

    public HtmlScraper() {
        try {
            yamlFiles = new YamlFilesParser().getYamlFiles();
        } catch (Exception e) {
            LOGGER.error("An error occurred when trying to scrape HTML files", e);
        }

    }

    public List<Triple> scrape(String uri, File filetToScrape) throws Exception {

        List<Triple> listTriples = new ArrayList<Triple>();

        YamlFile yamlFile = (YamlFile) yamlFiles.get(UriUtils.getDomainName(uri)).clone();
        
        
        
        if((boolean) yamlFile.getFile_descriptor().get(YamlFileAtributes.SEARCH_CHECK).get("ignore-request") && uri.contains("?")) {

        	uri = uri.substring(0, uri.indexOf("?"));

        }
        
        if (yamlFile != null) {
//            yamlFile.getFile_descriptor().remove(YamlFileAtributes.SEARCH_CHECK);

            for (Entry<String, Map<String, Object>> entry : yamlFile.getFile_descriptor().entrySet()) {
                for (Entry<String, Object> cfg : entry.getValue().entrySet()) {

                    List<String> regexList = new ArrayList<String>();

                    if (cfg.getValue() instanceof List<?> && ((ArrayList<String>) cfg.getValue()).size() > 1) {
                        regexList = (ArrayList<String>) cfg.getValue();
                    } else {
                        regexList.add(cfg.getValue().toString().toLowerCase());
                    }

                    for (String regex : regexList) {
                        if (cfg.getKey().equals(YamlFileAtributes.REGEX) && uri.toLowerCase().contains(regex.toLowerCase())) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> resources = (Map<String, Object>) entry.getValue().get(YamlFileAtributes.RESOURCES);
                            listTriples.addAll(scrapeDownloadLink(resources, filetToScrape, uri));
                            break;
                        }
                    }

                }
            }

        }

        return listTriples;
    }
    
    
    /**
     * 
     * Creates S,P,O using
     * s = The user defined S in the yaml file
     * p = the key defined in the yaml file
     * o = the value defined in the yaml file
     * 
     * @author gsjunior
     * 
     * @throws MalformedURLException 
     */
    
    private Set<Triple> createResources(String resource, Object obj, Document doc,String uri) throws MalformedURLException{
    	 Set<Triple> listTriples = new LinkedHashSet<Triple>();
    	
    	 
    	 String res = resource.substring(resource.indexOf("(")+1,resource.lastIndexOf(")"));
    	 String label = uri.substring(uri.lastIndexOf("/")+1, uri.length());
    	 
    	 if (res.contains("$uri")) {
    		 res = res.replaceAll("\\$uri", uri);
    	 }
    	 
    	 if (res.contains("$label")) {
    		 res = res.replaceAll("\\$label", label);
  		}
    	 
    	 LinkedHashMap<String,Object>resourcesList = new LinkedHashMap<String,Object> ();
    	 
    	 if(obj instanceof LinkedHashMap) {
    		 resourcesList = (LinkedHashMap<String, Object>) obj;
    	 }
         
         for(Entry<String,Object> entry: resourcesList.entrySet()) {
        	 listTriples.addAll(createPredicateValues(entry.getKey(),entry.getValue(),doc, res));
         }
    	 
    	
    	 return listTriples;
    }
    
    /**
     * 
     * Creates S,P,O using
     * s = The url of the page crawled
     * p = the key defined in the yaml file
     * o = the value defined in the yaml file
     * 
     * @author gsjunior
     * 

     * @throws MalformedURLException 
     */
    private Set<Triple> createPredicateValues(String pr, Object list,Document doc, String uri) throws MalformedURLException{
    	
    	Model model = ModelFactory.createDefaultModel();
    	
    	Set<Triple> listTriples = new LinkedHashSet<Triple>();
    	
    	 Node s = NodeFactory.createURI(uri);
    	 Node p = NodeFactory.createURI(pr);
    	 

    	 List<String> resourcesList = new ArrayList<String>();
    	 
         if (list instanceof List<?> && ((ArrayList<String>) list).size() > 1) {
             resourcesList = (ArrayList<String>) list;
         } else {
             resourcesList.add(list.toString());
         }

         for (String resource : resourcesList) {
        	 Node objectNode = null;
         	
             Elements elements = null;

             try {
             	
             	if(resource.startsWith("o")) {
             		
             		String val = resource.substring(resource.indexOf("(")+1,resource.lastIndexOf(")"));
             		String label = uri.substring(uri.lastIndexOf("/")+1, uri.length());
             		
             		if (val.contains("$uri")) {
             			val = val.replaceAll("\\$uri", uri);
             		}

             		
             		if (val.contains("$label")) {
             			val = val.replaceAll("\\$label", label);
             		}
             		
                 	Element el = new Element(Tag.valueOf(val),"");
                 	el.text(val);
                 	Element[] arrayElements = new Element[1];
                 	arrayElements[0] = el;
                 	elements = new Elements(arrayElements); 
                 }else {
	                    elements = doc.select(resource);
	                    
	                    
	                    
	                    if (elements.isEmpty()) {
	                        throw new ElementNotFoundException("Element (" + pr + " -> " + resource + ")"
	                            + " not found. Check selector syntax");
	                    }
                 }
                
                 
             } catch (Exception e) {
                 LOGGER.warn(e.getMessage() + " :: Uri: " + uri);
             }

             for (int i = 0; i < elements.size(); i++) {
                 if (elements.get(i).hasAttr("href")) {
                     if (!elements.get(i).attr("href").startsWith("http") && !elements.get(i).attr("href").startsWith("https")) {
                         URL url = new URL(uri);
                         String path = elements.get(i).attr("href");
                         String base = url.getProtocol() + "://" + url.getHost() + path;
                         objectNode = NodeFactory.createURI(base);
                     } else {
                         objectNode = NodeFactory.createURI(elements.get(i).attr("abs:href"));
                     }
                 } else {
                     boolean uriFlag = true;
                     try {
                         URL url = new URL(elements.get(i).text());
                     } catch (MalformedURLException e) {
                         uriFlag = false;
                         objectNode = NodeFactory.createLiteral(elements.get(i).text());
                     }
                     if (uriFlag) {
                         objectNode = NodeFactory.createURI(elements.get(i).text());
                     }
                 }

                 Triple triple = new Triple(s, p, objectNode);
                 listTriples.add(triple);
             }

         }
    	
    	return listTriples;
    }

    private Set<Triple> scrapeDownloadLink(Map<String, Object> resources, File htmlFile, String uri) throws Exception {
        Document doc = Jsoup.parse(htmlFile, "UTF-8");

        Node s = NodeFactory.createURI(uri);

        Set<Triple> listTriples = new LinkedHashSet<Triple>();


        List<String> resourcesList = new ArrayList<String>();
        Node objectNode = null;
        for (Entry<String, Object> entry :
            resources.entrySet()) {
            resourcesList.clear();
            
            if(entry.getKey().startsWith("s")) {
            	listTriples.addAll(createResources(entry.getKey(), entry.getValue() ,doc,uri));
            }else {
            	listTriples.addAll( createPredicateValues(entry.getKey(),entry.getValue(),doc,uri) );
            }

        }

        return listTriples;
    }


}
