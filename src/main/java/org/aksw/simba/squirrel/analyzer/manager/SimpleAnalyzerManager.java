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

public class SimpleAnalyzerManager implements Analyzer{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAnalyzerManager.class);
	
	private Map<String, Analyzer> analyzers;
	
	
	
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
		return false;
	}

}
