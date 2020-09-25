package org.dice_research.squirrel.analyzer.impl.html.scraper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.squirrel.analyzer.impl.html.scraper.exceptions.SyntaxParserException;
import org.dice_research.squirrel.data.uri.UriUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * HTMLScraper to extract triples from HTML Data based in pre configured yaml
 * files.
 * 
 * @author gsjunior
 */
public class HtmlScraper {

	private static final Logger LOGGER = LoggerFactory.getLogger(HtmlScraper.class);

	private Map<String, YamlFile> yamlFiles = new HashMap<String, YamlFile>();
	
	private LinkedHashSet<Node> updatedObjects = new LinkedHashSet<Node>();
	private LinkedHashSet<String> listIterableObjects;

	private String uri;
	private String label;
	private Document doc;
	private Map<String, List<Triple>> staticMap = new HashMap<String, List<Triple>>();
	private Map<String, List<Triple>> selectedMap = new HashMap<String, List<Triple>>();

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
	public List<Triple> scrape(String uri, File filetToScrape) throws Exception {

		List<Triple> listTriples = new ArrayList<Triple>();
		listIterableObjects = new LinkedHashSet<String>();

		YamlFile yamlFile = (YamlFile) yamlFiles.get(UriUtils.getDomainName(uri)).clone();

		this.uri = uri;
		if (uri.contains("?")) {
			String temp = uri.substring(0 , uri.indexOf("?")+1);
			this.label = temp.substring(temp.lastIndexOf("/") + 1, temp.lastIndexOf("?"));
		} else {
			this.label = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
		}
		if ((boolean) yamlFile.getFile_descriptor().get(YamlFileAtributes.SEARCH_CHECK).get("ignore-request")
				&& uri.contains("?")) {
			String temp = uri.substring(0 , uri.indexOf("?")+1);
			this.label = temp.substring(temp.lastIndexOf("/") + 1, temp.lastIndexOf("?"));
			this.uri = uri.substring(0, uri.indexOf("?"));
		}

		if (yamlFile != null) {
//            yamlFile.getFile_descriptor().remove(YamlFileAtributes.SEARCH_CHECK);

			if (yamlFile.getFile_descriptor().get(YamlFileAtributes.SEARCH_CHECK).get("static-resources") != null) {

				for (Entry<String, Object> entry : ((HashMap<String, Object>) yamlFile.getFile_descriptor()
						.get(YamlFileAtributes.SEARCH_CHECK).get("static-resources")).entrySet()) {
					for (Entry<String, Object> typesEntry : ((HashMap<String, Object>) entry.getValue()).entrySet()) {
						Node s = NodeFactory.createURI(typesEntry.getKey());
						List<Triple> listTriple = new ArrayList<Triple>();
						for (Entry<String, Object> valuesEntry : ((HashMap<String, Object>) typesEntry.getValue())
								.entrySet()) {
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
//      			and
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
						if (cfg.getKey().equals(YamlFileAtributes.REGEX)) {
							try {
								Pattern p = Pattern.compile(regex);
								Matcher m = p.matcher(uri.toLowerCase());

								if (m.find()) {
									Map<String, Object> resources = (Map<String, Object>) entry.getValue()
											.get(YamlFileAtributes.RESOURCES);
									listTriples.addAll(scrapeDownloadLink(resources, filetToScrape));
									break;
								}
							} catch (Exception e) {
								LOGGER.info("Got an incorrect regex pattern. It will be ignored.", e);
							}
						}
					}

				}
			}

		}

		if (!listTriples.isEmpty() && !listIterableObjects.isEmpty()) {
			listTriples = updateRelationship(listTriples);
		}
		listTriples.sort(tripleComparator);
		return listTriples;
	}

	public static Comparator<Triple> tripleComparator = new Comparator<Triple>() {

		public int compare(Triple t1, Triple t2) {
			String s1 = t1.getSubject().toString();
			String s2 = t2.getSubject().toString();

			// ascending order
			return s2.compareTo(s1);

		}
	};

    /**
     * Update the triples with nested objects
     * 
     * @param listTriples
     * @return
     */
    private List<Triple> updateRelationship(List<Triple> listTriples) {
       List<Triple> updatedTriples = new ArrayList<Triple>();
       
       for(Triple t : listTriples) {
    	   boolean found = false;
    	   for(String o: listIterableObjects) {
    		   if(t.getObject().toString().equals(o)) {
    			   found = true;
    			   for(Node no: updatedObjects) {
    				   updatedTriples.add(new Triple(t.getSubject(),t.getPredicate(),no));
    			   }
    		   }
    	   }
    	   if(!found)
    		   updatedTriples.add(t);
       }
       
       return updatedTriples;
    }

	@SuppressWarnings("unchecked")
	private Set<Triple> scrapeDownloadLink(Map<String, Object> resources, File htmlFile) throws Exception {
		this.doc = Jsoup.parse(htmlFile, "UTF-8");

		Set<Triple> triples = new LinkedHashSet<Triple>();

		List<String> resourcesList = new ArrayList<String>();

//        if(uri.contains("?")) {
//         this.label = uri.substring(uri.lastIndexOf("/")+1, uri.lastIndexOf("?"));
//        }else {
//         this.label = uri.substring(uri.lastIndexOf("/")+1, uri.length());
//        }

		for (Entry<String, Object> entry : resources.entrySet()) {
			resourcesList.clear();

			if (entry.getValue() instanceof Map<?, ?>) {
				Stack<Node> stackNode = new Stack<Node>();
				stackNode.push(NodeFactory.createURI(replaceCommands(entry.getKey())));

//                jsoupQuery(entry.getKey()).forEach(n -> stackNode.push(n));

//              stackNode.push(NodeFactory.createURI(replaceCommands(entry.getKey())));

				scrapeTree((Map<String, Object>) entry.getValue(), triples, stackNode);
			}

		}

		if (!selectedMap.isEmpty()) {
			for (Entry<String, List<Triple>> entry : selectedMap.entrySet()) {
				triples.addAll(entry.getValue());
			}
		}

		return triples;
	}

	private String replaceCommands(String s) {

		if (s.contains("$uri")) {
			return uri;
		}

		if (s.contains("$label")) {
			return label;
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
	@SuppressWarnings("unchecked")
	private Set<Triple> scrapeTree(Map<String, Object> mapEntry, Set<Triple> triples, Stack<Node> stackNode)
			throws Exception {
		
		for (Entry<String, Object> entry : mapEntry.entrySet()) {
						
			if (entry.getValue() instanceof Map<?, ?>) {
				Node node = NodeFactory.createURI(replaceCommands(entry.getKey()));
				
					
				stackNode.push(node);
				triples.addAll(scrapeTree((Map<String, Object>) entry.getValue(), triples, stackNode));
			}else if(entry.getValue() instanceof ArrayList<?>) {
				
				List<String> listValues = (ArrayList<String>)entry.getValue();
				
				Node p = ResourceFactory.createResource(entry.getKey()).asNode();
				
				for(String v : listValues) {
					
					
					List<Node> o = jsoupQuery(v);
					if (o.isEmpty()) {
						LOGGER.warn("Element " + entry.getKey() + ": " + v + " not found or does not exist");
						continue;
					}
					int i=0;
					for (Node n : o) {
						if(listIterableObjects.contains(stackNode.peek().toString())) {
							Triple t = new Triple(NodeFactory.createURI(stackNode.peek().toString() + "_" + i), p, n);
							updatedObjects.add(NodeFactory.createURI(stackNode.peek().toString() + "_" + i));
							triples.add(t);
							i = i + 1;
							
						}else {
						Triple t = new Triple(NodeFactory.createURI(stackNode.peek().toString()), p, n);
						triples.add(t);
						}
					}

					
				}
			
			
			}else if (entry.getValue() instanceof String) {


				Node p = ResourceFactory.createResource(entry.getKey()).asNode();

				List<Node> o = jsoupQuery((String) entry.getValue());
				if (o.isEmpty()) {
					LOGGER.warn("Element " + entry.getKey() + ": " + entry.getValue() + " not found or does not exist");
					continue;
				}
				int i=0;
				for (Node n : o) {
					if(listIterableObjects.contains(stackNode.peek().toString())) {
						Triple t = new Triple(NodeFactory.createURI(stackNode.peek().toString() + "_" + i), p, n);
						updatedObjects.add(NodeFactory.createURI(stackNode.peek().toString() + "_" + i));
						triples.add(t);
						i = i + 1;
						
					}else {
					Triple t = new Triple(NodeFactory.createURI(stackNode.peek().toString()), p, n);
					triples.add(t);
					}
				}

			}
		}
		stackNode.pop();
		return triples;
	}

	private List<Node> jsoupQuery(String cssQ) throws Exception {
		String cssQuery = cssQ;
		List<Node> listNodes = new ArrayList<Node>();
		String prefix = "";
		String attribute = "";

		if (cssQuery.startsWith("l(")) {
			prefix = cssQuery.substring(cssQuery.indexOf("(") + 1, cssQuery.lastIndexOf(")"));

		}

		String[] sentence = cssQuery.split("\\+");

		if (sentence.length > 1) {
			prefix = sentence[0].substring(sentence[0].indexOf("(") + 1, sentence[0].lastIndexOf(")"));
			cssQuery = sentence[1];
		} else if (sentence.length == 1 && !prefix.isEmpty()) {
			try {
				new URL(prefix);

				if (prefix.endsWith("*")) {
					prefix = prefix.substring(0, prefix.length() - 1);
					listIterableObjects.add(prefix);
				}
				listNodes.add(NodeFactory.createURI(prefix));
			} catch (MalformedURLException e) {
				listNodes.add(NodeFactory.createLiteral(prefix));
			}
			return listNodes;
		}

		if (cssQuery.startsWith("attr(")) {
			String[] val = cssQuery.substring(cssQuery.indexOf("(") + 1, cssQuery.lastIndexOf(")")).split("\\:");
			if (val.length != 2)
				throw new SyntaxParserException("attr command is invalid. Check the syntax");

			attribute = val[0];
			cssQuery = val[1];

		}

		Elements elements = doc.select(cssQuery);

		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).hasAttr("href")) {
				if (!elements.get(i).attr("href").startsWith("http")
						&& !elements.get(i).attr("href").startsWith("https")) {
					URL url = new URL(uri);
					String path = elements.get(i).attr("href");
					String base = url.getProtocol() + "://" + url.getHost() + path;
					listNodes.add(NodeFactory.createURI(base));
				} else {
					listNodes.add(NodeFactory.createURI(elements.get(i).attr("abs:href")));
				}
			} else {

				String qText = elements.get(i).text();

				if (elements.get(i).text().endsWith("*")) {
					qText = qText.substring(0, qText.length() - 1);
					listIterableObjects.add(qText);
				}

				if (!attribute.isEmpty())
					qText = elements.get(i).attr(attribute);

				if (elements.get(i).text().endsWith("*")) {
					qText = qText.substring(0, qText.length() - 1);
					listIterableObjects.add(qText);
				}

				try {
					new URL(prefix + qText);
					listNodes.add(NodeFactory.createURI(prefix + qText));
				} catch (MalformedURLException e) {
					listNodes.add(NodeFactory.createLiteral(prefix + qText));
				}
			}

		}

		return listNodes;
	}

//    /**
//     * 
//     * @param cssQuery
//     * @return
//     * @throws MalformedURLException
//     */
//    private List<Node> jsoupQuery(String cssQuery) throws MalformedURLException {
//    	
//    	String[] sentence = cssQuery.split("\\+");
//    	    	
//        List<Node> listNodes = new ArrayList<Node>();
//        
//        StringBuilder wholeSentence = new StringBuilder();
//        
//        String attribute = "";
//
//    	
////    	for(String part: sentence) {
////    		cssQuery = part.trim();
//    		
//    		Elements elements = null;
//
//            boolean useResource = false;
//
//            try {
//
//                if (cssQuery.startsWith("l(")) {
//
//                	String val = cssQuery.substring(cssQuery.indexOf("(") + 1, cssQuery.lastIndexOf(")"));
////              		String label = uri.substring(uri.lastIndexOf("/")+1, uri.lastIndexOf("?"));
//
//                    val = replaceCommands(val);
//                    
//                    Element el = new Element(Tag.valueOf(val), "");
//                    el.text(val);
//                    Element[] arrayElements = new Element[1];
//                    arrayElements[0] = el;
//                    elements = new Elements(arrayElements);
//                }else if( cssQuery.startsWith("attr(") ) {
//                	String[] val = cssQuery.substring(cssQuery.indexOf("(") + 1, cssQuery.lastIndexOf(")")).split("\\:");
//                	attribute = val[1];
//                	elements = doc.select(val[0]);
//                	
//                }
//                else {
//
//                    if (cssQuery.startsWith("res(")) {
//                        useResource = true;
//                        cssQuery = cssQuery.substring(cssQuery.indexOf("(") + 1, cssQuery.lastIndexOf(")"));
//                    }
//                    elements = doc.select(cssQuery);
//
//                    if (elements.isEmpty()) {
//                        throw new ElementNotFoundException(
//                                "Element (" + cssQuery + ")" + " not found. Check selector syntax");
//                    }
//                }
//
//            } catch (Exception e) {
//                LOGGER.warn(e.getMessage() + " :: Uri: " + uri);
//            }
//
//            for (int i = 0; i < elements.size(); i++) {
//                if (elements.get(i).hasAttr("href")) {
//                    if (!elements.get(i).attr("href").startsWith("http")
//                            && !elements.get(i).attr("href").startsWith("https")) {
//                        URL url = new URL(uri);
//                        String path = elements.get(i).attr("href");
//                        String base = url.getProtocol() + "://" + url.getHost() + path;
//                        listNodes.add(NodeFactory.createURI(base));
//                    } else {
//                        listNodes.add(NodeFactory.createURI(elements.get(i).attr("abs:href")));
//                    }
//                } else if (useResource) {
//                    listNodes.add(staticMap.get(elements.get(i).text().toLowerCase()).get(0).getSubject());
//                    selectedMap.put(elements.get(i).text().toLowerCase(),
//                            staticMap.get(elements.get(i).text().toLowerCase()));
//                } else {
//                    boolean uriFlag = true;
//
//                    String qText = elements.get(i).text();
//
//                    if (elements.get(i).text().endsWith("*")) {
//                        qText = qText.substring(0, qText.length() - 1);
//                        listIterableObjects.add(qText);
//                    }
//                    
////                    wholeSentence.append(qText);
////                    wholeSentence.append("|");
//
//                    try {
//                        new URL(qText);
//                    } catch (MalformedURLException e) {
//                        uriFlag = false;
//                        listNodes.add(NodeFactory.createLiteral(qText));
//                    }
//                    if (uriFlag) {
//                        listNodes.add(NodeFactory.createURI(qText));
//                    }
//
//                }
//
//            }
//            
//    		
//    	
////    	String[] objs = wholeSentence.toString().split("\\|");
////    	for(String obj: objs) {
////    		try {
////                new URL(obj);
////            } catch (MalformedURLException e) {
////                listNodes.add(NodeFactory.createLiteral(obj));
////            }	
////    	}
//    	
//    	
//        
//
//        return listNodes;
//
//    }

}
