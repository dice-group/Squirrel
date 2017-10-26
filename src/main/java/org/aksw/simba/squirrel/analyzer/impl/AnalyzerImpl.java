package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.collect.SimpleUriCollector;
import org.aksw.simba.squirrel.sink.collect.UriCollector;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.JenaException;

public class AnalyzerImpl implements Analyzer {
	
	private URI uri;
	private Model model;
	private Sink sink;
	
	public AnalyzerImpl(CrawleableUri curi, File data, Sink sink) {
		this.uri = curi.getUri();
		this.model = ModelFactory.createDefaultModel().read(data.getPath());
		this.sink = sink;
	}
	

	@Override
	public Iterator<String> analyze() {
		
		CrawleableUri curi = new CrawleableUri(uri);
		
		
		UriCollector collector = new SimpleUriCollector(sink);
		

		try {
	       
	        StmtIterator si;
	        si = model.listStatements();

	        while(si.hasNext()) {
	            Statement s=si.nextStatement();
	            sink.addTriple(curi, s.asTriple());
	            collector.addTriple(curi, s.asTriple());

	        }
	    }
	    catch(JenaException | NoSuchElementException c) {}
		    
		return collector.getUris();
	}
	

}
