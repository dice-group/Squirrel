package org.dice_research.squirrel.analyzer.impl.html.scraper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.apache.commons.lang.StringUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.tdb.store.Hash;
import org.dice_research.squirrel.Constants;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ResourceFactory;
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
 * 
 * HTMLScraper to extract triples from HTML Data based in
 * pre configured yaml files.
 * 
 * @author gsjunior
 */
public class HtmlScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlScraper.class);

    private Map<String, YamlFile> yamlFiles = new HashMap<String, YamlFile>();
    
    private LinkedHashSet<String> listIterableObjects;
    
    private String uri;
    private String label;
    private Document doc;
    private String newUri;
    private Map<String,List<Triple>> staticMap = new HashMap<String,List<Triple>>();
    private Map<String,List<Triple>> selectedMap = new HashMap<String,List<Triple>>();


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

  
  
  @SuppressWarnings("unchecked")
    public List<Triple> scrape(CrawleableUri curi, File filetToScrape) throws Exception {
        List<Triple> listTriples = new ArrayList<Triple>();
        listIterableObjects = new LinkedHashSet<String>();
        uri= curi.getUri().toString();
        YamlFile yamlFile = (YamlFile) yamlFiles.get(UriUtils.getDomainName(uri)).clone();
        
        
        this.uri = uri;
	     if(uri.contains("?")) {
	      this.label = uri.substring(uri.lastIndexOf("/")+1, uri.lastIndexOf("?"));
	     }else {
	      this.label = uri.substring(uri.lastIndexOf("/")+1, uri.length());
	     }
        if((boolean) yamlFile.getFile_descriptor().get(YamlFileAtributes.SEARCH_CHECK).get("ignore-request") && uri.contains("?")) {
        	label = uri.substring(uri.lastIndexOf("/")+1, uri.lastIndexOf("?"));
        	this.uri = uri.substring(0, uri.indexOf("?"));
        }
        
        if (yamlFile != null) {
//            yamlFile.getFile_descriptor().remove(YamlFileAtributes.SEARCH_CHECK);
        	
        	if(yamlFile.getFile_descriptor().get(YamlFileAtributes.SEARCH_CHECK).get("static-resources")!= null) {
        		
        		for(Entry<String,Object> entry: ((HashMap<String, Object>)  yamlFile.getFile_descriptor().get(YamlFileAtributes.SEARCH_CHECK)
        		.get("static-resources")).entrySet()) {
        			for(Entry<String,Object> typesEntry: ((HashMap<String, Object>) entry.getValue()).entrySet() ) {
        				Node s = NodeFactory.createURI(typesEntry.getKey());
        				List<Triple> listTriple = new ArrayList<Triple>();
        				for(Entry<String,Object> valuesEntry: ((HashMap<String, Object>) typesEntry.getValue()).entrySet()) {
        					Node o;
        					Node p = NodeFactory.createURI(valuesEntry.getKey());
        					
        					try {
        	                     new URL(valuesEntry.getValue().toString());
        	                     o = NodeFactory.createURI(valuesEntry.getValue().toString());
        	                 } catch (MalformedURLException e) {
        	                	 o = NodeFactory.createLiteral(valuesEntry.getValue().toString());
        	                 }
        	              
        					Triple t = new Triple(s, p, o);
        					listTriple.add(t);
        				}
        				staticMap.put(entry.getKey().toLowerCase(), listTriple);
        			}
        		}
        		
//        		for(Object entry : yamlFile.getFile_descriptor().get(YamlFileAtributes.SEARCH_CHECK).get("static-resources")) {
//      			
//        		}
        	}
        	 

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
    		
    		
    		
    		System.out.println();
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
       

    private Set<Triple> scrapeDownloadLink(Map<String, Object> resources, File htmlFile, CrawleableUri curi) throws Exception {
        this.doc = Jsoup.parse(htmlFile, "UTF-8");
        Map<String, Object> jsResources = null;
        for(Entry<String, Object> resEntry: resources.entrySet()){
            if ((resEntry.getKey().equals(YamlFileAtributes.JAVASCRIPT))||(resEntry.getKey().equals(YamlFileAtributes.PAGINATION))){
                 jsResources = (Map<String, Object>) resEntry.getValue();
                resources.remove(resEntry.getKey());
                break;
            }
        }
        if (jsResources != null)
            HtmlUnitImp(jsResources, htmlFile, curi);

        uri= curi.getUri().toString();
        Set<Triple> triples = new LinkedHashSet<Triple>();


        List<String> resourcesList = new ArrayList<String>();
        

//        if(uri.contains("?")) {
//         this.label = uri.substring(uri.lastIndexOf("/")+1, uri.lastIndexOf("?"));
//        }else {
//         this.label = uri.substring(uri.lastIndexOf("/")+1, uri.length());
//        }

        for (Entry<String, Object> entry :
            resources.entrySet()) {
            resourcesList.clear();
            
            
            if(entry.getValue() instanceof Map<?,?>) {
            	Stack<Node> stackNode = new Stack<Node>();
            	stackNode.push(NodeFactory.createURI(replaceCommands(entry.getKey())));
            	
            	scrapeTree((Map<String,Object> )entry.getValue(),triples,stackNode);
            }

        }
        
        if(!selectedMap.isEmpty()) {
        	for(Entry<String,List<Triple>> entry : selectedMap.entrySet()) {
        		triples.addAll(entry.getValue());
        	}
        }

        return triples;
    }

    private void HtmlUnitImp(Map<String, Object> resources, File htmlFile, CrawleableUri curi){
        String id = null, action = null, disable_id = null;
        long timeout=0, defaultMinWaitingTime=2000;
        Boolean htmltypejs=false;
        for(Entry<String, Object> jsEntry: resources.entrySet()){
            if (jsEntry.getKey().equals(YamlFileAtributes.BUTTON)){
                Map<String, Object> btnEntry = (Map<String, Object>) jsEntry.getValue();
                for (Entry<String, Object> btnEntries: btnEntry.entrySet()){
                    if (btnEntries.getKey().equals(YamlFileAtributes.ACTION))
                        action = btnEntries.getValue().toString();
                    if (btnEntries.getKey().equals(YamlFileAtributes.ID))
                        id = btnEntries.getValue().toString();
                    if (btnEntries.getKey().equals(YamlFileAtributes.DISABLE_ID))
                        disable_id = btnEntries.getValue().toString();
                }
            }
        }

        if (action.equals("click"))
            htmltypejs=true;

        if (id != null && action != null && disable_id != null) {
            WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
            webClient.getOptions().setJavaScriptEnabled(htmltypejs);
            webClient.getOptions().setThrowExceptionOnScriptError(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());


            try{
               if (curi.getData("time-out").equals(null)) {
                  timeout= defaultMinWaitingTime;
                }else{
                    timeout= (long) curi.getData("time-out");
                }
            } catch (Exception e) {
                LOGGER.error("An error occurred when retrieving the Time out value, ", e);
            }

            uri= curi.getUri().toString();
            try{
                HtmlPage htmlPage = webClient.getPage(uri);
                if (action.equals("click")){
                    try{
                    HtmlButton btn = (HtmlButton) htmlPage.getElementById(id);
                    DomElement disabledElement = htmlPage.getElementById(disable_id);
                    do {
                        htmlPage = btn.click();
                        //Thread.sleep(timeout);
                    } while (!disabledElement.getStyleMap().containsKey("display") ||
                        (disabledElement.getStyleMap().containsKey("display") &&
                            !disabledElement.getStyleMap().get("display").getValue().equals("none")));
                    this.doc = Jsoup.parse(htmlPage.getWebResponse().getContentAsString(), "UTF-8");
                     } catch (Exception e) {
                    LOGGER.error("An error occurred when trying to scrape Java script, ", e);
                }

                }else if (action.equals("link")) {
                    //TODO Test in case of being in last page
                    try {
                        HtmlAnchor htmlAnchor = htmlPage.getAnchorByText(id);
                        HtmlPage htmlpage2 = htmlAnchor.click();
                        this.newUri=htmlpage2.getUrl().toString(); //Return the Uri of the next page
                        LOGGER.info("woslet"+ newUri);
                        this.doc = Jsoup.parse(htmlpage2.getWebResponse().getContentAsString(), "UTF-8");
                    } catch (Exception e) {
                        LOGGER.error("An error occurred when trying to scrape html Anchor, ", e);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("An error occurred when trying to scrape the Uri , ", e);
            }
        }
    }

    public String getNewUri(){
        if (newUri!=null){
            return newUri;
        }
        return null;
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
     * 
     * 
     * 
     * @param mapEntry
     * @param triples
     * @param stackNode
     * @return
     * @throws MalformedURLException
     */
    private Set<Triple> scrapeTree(Map<String, Object > mapEntry,Set<Triple> triples, Stack<Node> stackNode) throws MalformedURLException{
    	for(Entry<String,Object> entry: mapEntry.entrySet()) {
    		if(entry.getValue() instanceof Map<?,?>) {
    				Node node = NodeFactory.createURI(replaceCommands(entry.getKey()));
        			stackNode.push(node);
                   triples.addAll(scrapeTree((Map<String,Object> )entry.getValue(),triples,stackNode));
            }else if(entry.getValue() instanceof String) {
            	
            	Node p = ResourceFactory.createResource(entry.getKey()).asNode();
    			List<Node> o = jsoupQuery((String) entry.getValue());
    			if (o.isEmpty()) {
    				LOGGER.warn("Element "+ entry.getKey() + ": " + entry.getValue() + " not found or does not exist");
    				continue;
    			}
    			for(Node n : o) {
    				Triple t = new Triple(stackNode.peek(),p,n);
        			triples.add(t);
    			}
    			
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
    	
    	@SuppressWarnings("unused")
		boolean useResource = false;
    	
    	 try {
          	
          	if(cssQuery.startsWith("l(")) {
          		
          		String val = cssQuery.substring(cssQuery.indexOf("(")+1,cssQuery.lastIndexOf(")"));
//          		String label = uri.substring(uri.lastIndexOf("/")+1, uri.lastIndexOf("?"));
          		
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
            	  
            	  if(cssQuery.startsWith("res(")) {
            		  useResource = true;
            		  cssQuery = cssQuery.substring(cssQuery.indexOf("(")+1, cssQuery.lastIndexOf(")") );
            	  }
            	  elements = doc.select(cssQuery);

	                    if (elements.isEmpty()) {
	                        throw new ElementNotFoundException("Element (" + cssQuery + ")"
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
                     listNodes.add(NodeFactory.createURI(base));
                 }else {
                	 listNodes.add(NodeFactory.createURI(elements.get(i).attr("abs:href")));
                 }
             }else if(useResource) {
            	 listNodes.add(staticMap.get(elements.get(i).text().toLowerCase()).get(0).getSubject());
            	 selectedMap.put(elements.get(i).text().toLowerCase(), staticMap.get(elements.get(i).text().toLowerCase()));
             } else {
                 boolean uriFlag = true;
                 
                 String qText = elements.get(i).text();
                 
                 if(elements.get(i).text().endsWith("*")) {
                	 qText = qText.substring(0,qText.length()-1);
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
    	 
    	 return listNodes;
    	
    }


}
