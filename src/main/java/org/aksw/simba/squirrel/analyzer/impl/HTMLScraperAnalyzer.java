package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.analyzer.htmlscraper.HtmlScraper;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;

public class HTMLScraperAnalyzer implements Analyzer{
	
	private UriCollector collector;
	private HtmlScraper htmlScraper = new HtmlScraper();
	
	public HTMLScraperAnalyzer(UriCollector collector) {
		this.collector = collector;
	}

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		try {
			htmlScraper.scrape(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		new HTMLScraperAnalyzer(null).analyze(null, null, null);
	}

}
