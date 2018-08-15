package org.aksw.simba.squirrel.analyzer.manager;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class responsible for managing analyzers injected by the Spring Context
 * 
 * @author gsjunior sjunior@mail.uni-paderborn.de
 *
 */
public class SimpleAnalyzerManager implements Analyzer{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAnalyzerManager.class);
	
	private Map<String, Analyzer> analyzers;
	
	
	
	/**
	 * Receives the array of String injected by Spring
	 * and crates new instances of classes that implements the Analyzer
	 * interface
	 * 
	 * @param uriCollector The collector used to create a new Analyzer
	 * @param analyzers Array of Strings, containing the full class name used to create a new instance of an Analyzer
	 */
	public SimpleAnalyzerManager(UriCollector uriCollector,String... analyzers) {
		this.analyzers = new HashMap<String, Analyzer>();
		for(String analyzer: analyzers) {
			try {
				Class<?> cls = Class.forName(analyzer);
					if(Analyzer.class.isAssignableFrom(cls)) {
						Analyzer a = (Analyzer) Class.forName(cls.getName()).getConstructor(UriCollector.class).newInstance(uriCollector);
						this.analyzers.put(cls.getName(), a);
					}else {
						LOGGER.error("The class " + cls.getName() + " does not implement the Analyzer interface");

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
		
		Iterator<byte[]> iterator = null;
		
		for(Entry<String, Analyzer> analyzerEntry : analyzers.entrySet()) {
			if(analyzerEntry.getValue().isElegible(curi, data)) {
				return analyzerEntry.getValue().analyze(curi, data, sink);
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
