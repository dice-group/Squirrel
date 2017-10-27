package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.collect.UriCollector;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.JenaException;

public class AnalyzerImpl implements Analyzer {
	
	private UriCollector collector;
	private Model model;
	
	
	
	



	@Override
	public Iterator<String> analyze(CrawleableUri curi, File data, Sink sink) {
		sink.openSinkForUri(curi);
		try {
	       
	        StmtIterator si;
	        si = model.listStatements();

	        while(si.hasNext()) {
	            Statement s=si.nextStatement();
	            collector.addTriple(curi, s.asTriple());
	            collector.addTriple(curi, s.asTriple());

	        }
	    }
	    catch(JenaException | NoSuchElementException c) {}
		    
		return collector.getUris();
	}
	

}
