package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.tika.Tika;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HDTAnalyzer implements Analyzer{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HDTAnalyzer.class);

    private UriCollector collector;
    
   
    
    public HDTAnalyzer(UriCollector collector) {
    	this.collector = collector;
	}
	
	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {

	try {
	    HDT hdt = HDTManager.loadHDT(data.getAbsolutePath(), null);
	 
	    // Search pattern: Empty string means "any"
	    IteratorTripleString it = hdt.search("", "", "");
	    while(it.hasNext()) {
	        TripleString ts = it.next();
	        
	        Node s = NodeFactory.createURI(ts.getSubject().toString());
	        Node p = NodeFactory.createURI(ts.getPredicate().toString());
	        Node o;
	        
	        try {
		        new URI("ts.getPredicate().toString()");
		        o = NodeFactory.createURI(ts.getPredicate().toString());
	        }catch(URISyntaxException e) {
	        	o = NodeFactory.createLiteral(ts.getPredicate().toString());
	        }
	        
	        Triple t = new Triple(s,p,o);
	        collector.addTriple(curi, t);
	        sink.addTriple(curi, t);
	        
	    }
	    
	}catch(IOException e) {
		LOGGER.error("An error occured when processing the HDT file",e);
	}catch (org.rdfhdt.hdt.exceptions.NotFoundException e1) {
		LOGGER.error("An error occured when processing the HDT file",e1);
	}
	
    
		return collector.getUris(curi);
	}

	@Override
	public boolean isElegible(CrawleableUri curi, File data) {
		Tika tika = new Tika();
		boolean isElegible = false;
		InputStream is = null;
		try {
			is = new FileInputStream(data);
			String mimeType = tika.detect(is);
			String contentType = (String) curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);
			if(contentType.equals("application/octet-stream") || mimeType.equals("application/octet-stream")) {
				 isElegible = true;
			}
			
		} catch (Exception e) {
			LOGGER.error("An error was found when trying to analyze ",e);
		}finally {
			try {
				is.close();
			} catch (IOException e) {
				LOGGER.error("Was not possible to close File Input Stream in HDTAnalyzer",e);
			}
		}
		
		return isElegible;
	}

}
