package org.dice_research.squirrel.analyzer.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dice_research.squirrel.analyzer.AbstractAnalyzer;
import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.ActivityUtil;
import org.dice_research.squirrel.sink.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class responsible for managing analyzers injected by the Spring Context
 * 
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 *
 */
public class SimpleAnalyzerManager implements Analyzer{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAnalyzerManager.class);
	public static final String LIST_ANALYZERS = "LIST_ANALYZERS";
	
	private Map<String, Analyzer> analyzers;
	
	
	
	/**
	 * Receives the array of String injected by Spring
	 * and crates new instances of classes that implements the Analyzer
	 * interface
	 * 
	 * @param uriCollector The collector used to create a new Analyzer
	 * @param analyzers Array of Strings, containing the full class name used to create a new instance of an Analyzer
	 */
	@Deprecated
	public SimpleAnalyzerManager(UriCollector uriCollector,String... analyzers) {
		this.analyzers = new HashMap<String, Analyzer>();
		for(String analyzer: analyzers) {
			try {
				Class<?> cls = Class.forName(analyzer);
					if(AbstractAnalyzer.class.isAssignableFrom(cls)) {
						Analyzer a = (Analyzer) Class.forName(cls.getName()).getConstructor(UriCollector.class).newInstance(uriCollector);
						this.analyzers.put(cls.getName(), a);
					}else {
						LOGGER.error("The class " + cls.getName() + " does not extends the AbstractAnalyzer class");
					}
		
			} catch (ClassNotFoundException e) {
				LOGGER.error("Cannot find the class of type: " + e.getMessage(),e);
			} catch (InstantiationException e) {
				LOGGER.error("Cannot instantiate the class of type: " + e.getMessage(),e);
			} catch (IllegalAccessException e) {
				LOGGER.error("Cannot access the class of type: " + e.getMessage(),e);
			} catch (Exception e) {
				LOGGER.error("A problem occured in the class of type: " + e.getMessage(),e);
			}
		}
		
	}
	
	
	/**
     * Receives the array of String injected by Spring
     * and crates new instances of classes that implements the Analyzer
     * interface
     * 
     * @param uriCollector The collector used to create a new Analyzer
     * @param analyzers Array of Strings, containing the full class name used to create a new instance of an Analyzer
     */
	
	public SimpleAnalyzerManager(List<AbstractAnalyzer> listAnalyzers) {
	    this.analyzers = new HashMap<String, Analyzer>();
	    for(Analyzer analyzer: listAnalyzers) {
	        analyzers.put(analyzer.getClass().getName(), analyzer);
	    }
    }
	
	/**
	 * 
	 * It iterates over all the Analyzers created and added to the
	 * analyzers map and check if they are eligible to be executed by
	 * invoking isEligible().
	 * 
	 * @curi the Crawleable uri that will be analyzed
	 * @data the File that will be analyzed
	 * @sink the Sink where the found data will be stored
	 */
	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
	    LOGGER.info(">> Analyzing");
	
		Iterator<byte[]> iterator = null;
		
		for(Entry<String, Analyzer> analyzerEntry : analyzers.entrySet()) {
			if(analyzerEntry.getValue().isElegible(curi, data)) {
		         LOGGER.info(">> Analyzer " + analyzerEntry.getValue().getClass().getName() + " is elegible for the uri: " + curi.getUri().toString());
				if(curi.getData().containsKey(LIST_ANALYZERS)) {
					@SuppressWarnings("unchecked")
					List<String> analyzers = (List<String>) curi.getData().get(LIST_ANALYZERS);
					analyzers.add(analyzerEntry.getValue().getClass().getName());
					curi.addData(LIST_ANALYZERS, analyzers);
				}else {
					List<String> analyzers = new ArrayList<String>();
					analyzers.add(analyzerEntry.getValue().getClass().getName());
					curi.addData(LIST_ANALYZERS, analyzers);
				}
				
				ActivityUtil.addStep(curi, analyzerEntry.getValue().getClass());
                LOGGER.info(">> Using analyzer " + analyzerEntry.getValue().getClass().getName() + ".");
				iterator = analyzerEntry.getValue().analyze(curi, data, sink);
			}
		}
		return iterator;
	}



	@Override
	public boolean isElegible(CrawleableUri curi, File data) {
		// TODO Auto-generated method stub
		return true;
	}

}
