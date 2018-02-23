package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;

public class HTMLScraperAnalyzer implements Analyzer{
	
	private UriCollector collector;
	
	public HTMLScraperAnalyzer(UriCollector collector) {
		this.collector = collector;
	}

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		// TODO Auto-generated method stub
		return null;
	}

}
