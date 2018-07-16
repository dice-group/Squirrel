package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.net.URI;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
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
	        collector.addNewUri(curi, new CrawleableUri(new URI(ts.getSubject().toString())));
	        collector.addNewUri(curi, new CrawleableUri(new URI(ts.getPredicate().toString())));
	        collector.addNewUri(curi, new CrawleableUri(new URI(ts.getObject().toString())));
	    }
	}catch(Exception e) {
		LOGGER.error("An error occured when processing the HDT file",e);
	}
    
		return collector.getUris(curi);
	}

}
