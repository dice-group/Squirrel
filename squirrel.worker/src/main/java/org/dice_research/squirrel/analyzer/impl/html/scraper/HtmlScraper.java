package org.dice_research.squirrel.analyzer.impl.html.scraper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.impl.html.scraper.exceptions.ElementNotFoundException;
import org.dice_research.squirrel.data.uri.CrawleableUri;
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
    private LinkedHashSet<String> listIterableObjects;
    private String uri;
    private String label;
    private Document doc;

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

    public List<Triple> scrape(CrawleableUri curi, File filetToScrape) throws Exception {

        List<Triple> listTriples = new ArrayList<Triple>();
        listIterableObjects = new LinkedHashSet<String>();
        uri= curi.getUri().toString();
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
                        if (cfg.getKey().equals(YamlFileAtributes.REGEX) && uri.toLowerCase().contains(regex.toLowerCase()) ) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> resources = (Map<String, Object>) entry.getValue().get(YamlFileAtributes.RESOURCES);
                            listTriples.addAll(scrapeDownloadLink(resources, filetToScrape, curi));
                            break;
                        }
                    }

                }
            }

        }
        
        if(!listTriples.isEmpty() && !listIterableObjects.isEmpty()) {
        	listTriples = updateRelationship(listTriples);
        }

        return listTriples;
    }
    
    /**
     * Update the triples with nested objects
     * @param listTriples
     * @return
     */
    private List<Triple> updateRelationship(List<Triple> listTriples) {
    	Map<String,Set<Triple>> updatedTriples = new HashMap<String, Set<Triple>>();
    	
    	for(String o: listIterableObjects) {
    		Set<Triple> staticNodes = new LinkedHashSet<>();
    		Set<Triple> iterableNodes = new LinkedHashSet<>();
    		for(Triple t : listTriples) {
    			if(t.getSubject().toString().equals(o))  {
    				
    				if(t.getPredicate().toString().endsWith("*")) {
    					iterableNodes.add(t);
    				}else {
    					staticNodes.add(t);
    				}
    			}
    		}
    		
    		int cont = 0;
    		for(Triple tt: iterableNodes) {
    			
    			Set<Triple> newTriples = new LinkedHashSet<Triple>();
    			if(updatedTriples.containsKey(o)) {
    				newTriples = updatedTriples.get(o);
    			}
    			
    			Node s = new NodeFactory().createURI(tt.getSubject().toString()+"_" + cont);
    			Node p = new NodeFactory().createURI(tt.getPredicate().toString().substring(0, tt.getPredicate().toString().length()-1));
    		
    			newTriples.add(new Triple(s,p,tt.getObject()));
    			
    			for(Triple t : staticNodes) {
    				newTriples.add(new Triple(s,t.getPredicate(),t.getObject()));
    			}
    			updatedTriples.put(o, newTriples);
    			cont++;
    		}
    	}
    	
    	
//    	for(Entry<String,Set<Triple>> entry: updatedTriples.entrySet()) {
//    		System.out.println(entry.getKey());
//    		for(Triple t: entry.getValue()) {
//    			System.out.println(" -- " + t);
//    		}
//    	}
    	
    	List<Triple> updatedList = new ArrayList<>();
		
		for(Triple t : listTriples) {
			if(updatedTriples.containsKey(t.getObject().toString())) {
				
				for(Triple ut: updatedTriples.get(t.getObject().toString())) {
					Triple newT = new Triple(t.getSubject(),t.getPredicate(),ut.getSubject());
					if(!updatedList.contains(newT))
						updatedList.add(newT);
						updatedList.add(ut);
				}
				
			}else if(!updatedTriples.containsKey(t.getSubject().toString())){
				updatedList.add(t);
			}
		}

    	return updatedList;
    }

    /**
     * Method to execute java script commands in a html page.
     * @param uri
     * @throws IOException
     */
    private void executeJavaScript(CrawleableUri uri){
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
        webClient.setRefreshHandler(new ThreadedRefreshHandler());
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(true);
        long timeout;
        try {
            if (uri.getData("time-out").equals(null)) {
                    timeout = Constants.JAVASCRIPT_WAIT_TIME;
            }else{
                timeout = (long) uri.getData("time-out");
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred when retrieving the Time out value, ", e);
            timeout = Constants.JAVASCRIPT_WAIT_TIME;
        }
        
        try {
            HtmlPage htmlPage = webClient.getPage(uri.getUri().toString());
            webClient.waitForBackgroundJavaScript(timeout);
            this.doc = Jsoup.parse(htmlPage.getWebResponse().getContentAsString(), "UTF-8");
        } catch (IOException e){
            LOGGER.warn("Error in handling java script by htmlunit: " + e.getMessage());
        }
    }

    private Set<Triple> scrapeDownloadLink(Map<String, Object> resources, File htmlFile, CrawleableUri curi) throws Exception {
        this.doc = Jsoup.parse(htmlFile, "UTF-8");
        this.uri = curi.getUri().toString();
        Set<Triple> triples = new LinkedHashSet<Triple>();
        this.label = uri.substring(uri.lastIndexOf("/")+1, uri.length());

        if (!htmlFile.toString().contains("test")) //To prevent downloading a page when running unit test cases
            executeJavaScript(curi);

        for (Entry<String, Object> entry :
            resources.entrySet()) {
            if(entry != null) {
            	Stack<Node> stackNode = new Stack<Node>();
            	stackNode.push(NodeFactory.createURI(replaceCommands(entry.getKey())));
            	scrapeTree(entry,triples,stackNode);
            }

        }

        return triples;
    }
    
    private String replaceCommands(String s) {
    	
    	if (s.contains("$uri")) {
 			s = s.replaceAll("\\$uri", uri);
 		}
 		
 		if (s.contains("$label")) {
 			s = s.replaceAll("\\$label", label);
 		}
    	
    	return s;
    }
    
    /**
     * @param entry
     * @param triples
     * @param stackNode
     * @return
     * @throws MalformedURLException
     */
    private Set<Triple> scrapeTree(Entry<String, Object> entry, Set<Triple> triples, Stack<Node> stackNode) throws MalformedURLException{
        if(entry.getValue() instanceof Map<?,?>) {
            for (Entry<String, Object> nestedEntry: ((Map<String, Object>) entry.getValue()).entrySet()) {
                Node node = NodeFactory.createURI(replaceCommands(nestedEntry.getKey()));
                stackNode.push(node);
                triples.addAll(scrapeTree(nestedEntry, triples, stackNode));
            }
        } else if (entry.getValue() instanceof String) {
            Node p = NodeFactory.createURI(entry.getKey());
            List<Node> o = jsoupQuery((String) entry.getValue());
            if (o.isEmpty()) {
                LOGGER.warn("Element "+ entry.getKey() + ": " + entry.getValue() + " not found or does not exist");
            }

            for(Node n : o) {
                Triple t = new Triple(stackNode.peek(),p,n);
                triples.add(t);
            }

        }
    	stackNode.pop();
    	return triples;
    }

    /**
     * 
     * @param cssQuery
     * @return
     * @throws MalformedURLException
     */
    private List<Node> jsoupQuery(String cssQuery) throws MalformedURLException {
    	Elements elements = null;
    	
    	List<Node> listNodes = new ArrayList<Node>();
    	
    	 try {
          	
          	if(cssQuery.startsWith("l")) {
          		
          		String val = cssQuery.substring(cssQuery.indexOf("(")+1,cssQuery.lastIndexOf(")"));
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
	                    elements = doc.select(cssQuery);

	                    if (elements.isEmpty()) {
	                        throw new ElementNotFoundException("Element (" + cssQuery + ")"
	                            + " not found. Check selector syntax");
	                    }
              }
             
              
          } catch (Exception e) {
              LOGGER.warn(e.getMessage() + " :: Uri: " + uri);
          }

    	 if (elements != null) {
             for (int i = 0; i < elements.size(); i++) {
                 if (elements.get(i).hasAttr("href")) {
                     if (!elements.get(i).attr("href").startsWith("http") && !elements.get(i).attr("href").startsWith("https")) {
                         URL url = new URL(uri);
                         String path = elements.get(i).attr("href");
                         String base = url.getProtocol() + "://" + url.getHost() + path;
                         listNodes.add(NodeFactory.createURI(base));
                     } else {
                         listNodes.add(NodeFactory.createURI(elements.get(i).attr("abs:href")));
                     }
                 } else {
                     boolean uriFlag = true;

                     String qText = elements.get(i).text();

                     if (elements.get(i).text().endsWith("*")) {
                         qText = qText.substring(0, qText.length() - 1);
                         listIterableObjects.add(qText);
                     }

                     try {
                         new URL(qText);
                     } catch (MalformedURLException e) {
                         uriFlag = false;
                         listNodes.add(NodeFactory.createLiteral(qText));
                     }
                     if (uriFlag) {
                         listNodes.add(NodeFactory.createURI(qText));
                     }

                 }


             }
         }
    	 
    	 return listNodes;
    	
    }


}
