package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.analyzer.htmlscraper.HtmlScraper;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Triple;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLScraperAnalyzer implements Analyzer{
	
    private static final Logger LOGGER = LoggerFactory.getLogger(HTMLScraperAnalyzer.class);
	
	private UriCollector collector;
	private HtmlScraper htmlScraper = new HtmlScraper();
	
	public HTMLScraperAnalyzer(UriCollector collector, HtmlScraper htmlScraper) {
		this.collector = collector;
		this.htmlScraper = htmlScraper;
	}
	
	public HTMLScraperAnalyzer(UriCollector collector) {
		this.collector = collector;
	}

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		try {
			List<Triple> listTriples = htmlScraper.scrape(curi.getUri().toString(),data);
			for(Triple triple: listTriples) {
				sink.addTriple(curi, triple);
				collector.addTriple(curi, triple);
			}
			return collector.getUris(curi);
			
		} catch (Exception e) {
			LOGGER.error("Exception while analyzing. Aborting. ", e);
			LOGGER.error(e.getMessage(), e);
		}
		return null;
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
			if((contentType != null && contentType.equals("text/html")) || mimeType.equals("text/html")) {
				isElegible = true;
			}
			
		} catch (Exception e) {
			LOGGER.error("An error was found when trying to analyze ",e);
		}finally {
			try {
				is.close();
			} catch (IOException e) {
				LOGGER.error("Was not possible to close File Input Stream in HTMLScraperAnalyzer",e);
			}
		}
		
		return isElegible;
	}
	


}
