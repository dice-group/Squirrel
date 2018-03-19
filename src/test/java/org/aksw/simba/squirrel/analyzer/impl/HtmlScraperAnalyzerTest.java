package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.analyzer.htmlscraper.HtmlScraper;
import org.aksw.simba.squirrel.collect.SimpleUriCollector;
import org.aksw.simba.squirrel.collect.SqlBasedUriCollector;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.mem.InMemorySink;
import org.aksw.simba.squirrel.utils.TempFileHelper;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HtmlScraperAnalyzerTest {
	
	private File configurationFile;
	File fetchedFile;
	private Analyzer analyzer;
	
	private UriCollector collector;
	private Sink sink;
	private HtmlScraper scraper;
	private Serializer serializer;

	
	@Before
	public void prepareGeneral() throws URISyntaxException, IOException {
		String dbdir = TempFileHelper.getTempDir("dbTest", "").getAbsolutePath() + File.separator + "test";
		serializer = new GzipJavaUriSerializer();
		this.sink = new InMemorySink();
		this.collector = SqlBasedUriCollector.create(serializer, dbdir);

		configurationFile = new File("src/test/resources/html_scraper_analyzer/yaml");
		scraper = new HtmlScraper(configurationFile);
		analyzer = new HTMLScraperAnalyzer(collector,scraper);

	}
	
	
	@Test
	public void scrapMcloud() throws Exception {
		CrawleableUri curi = new CrawleableUri(new URI("https://www.mcloud.de/web/guest/suche/-/results/detail/verkehrslageaufautobahnenschleifenhamburg"));
		fetchedFile = new File("src/test/resources/html_scraper_analyzer/mcloudexample.html");
		 Iterator<byte[]> iterator = analyzer.analyze(curi, fetchedFile, sink);
		
		 System.out.println(UriUtils.getDomainName(curi.getUri().toString()));
		
	}
	
	

}
