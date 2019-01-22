package org.dice_research.squirrel.analyzer.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.analyzer.impl.RDFAnalyzer;
import org.dice_research.squirrel.collect.SimpleUriCollector;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.sink.impl.mem.InMemorySink;
import org.dice_research.squirrel.utils.TempPathUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RDFAnalyzerTest {

	private Analyzer analyzer;
	private UriCollector collector;
	private Sink sink;
	private Serializer serializer = new GzipJavaUriSerializer();
	private long minimum_expected_new_york = 8603;
	private long expected_genders_en = 8408;
//	private String fileToTest = "rdf_analyzer/genders_en";
//	private String fileToTest = "rdf_analyzer/genders_en.tql";


	@Before
	public void prepare() throws URISyntaxException, UnknownHostException {
		this.sink = new InMemorySink();
	}


	@Test
	public void test_genders_en() throws URISyntaxException {
		CrawleableUri curi = new CrawleableUri(new URI("http://aksw.test.org/genders_en"));
		
		ClassLoader classLoader = getClass().getClassLoader();
		File filesFolder = new File(classLoader.getResource("rdf_analyzer/genders_en").getFile());
		List<File> listFiles =  TempPathUtils.searchPath4Files(filesFolder);
		
		for(File file : listFiles) {
			
			this.collector = new SimpleUriCollector(serializer);
			sink.openSinkForUri(curi);
			collector.openSinkForUri(curi);
			
			analyzer = new RDFAnalyzer(collector);
			analyzer.analyze(curi, file, sink);
			
			Assert.assertEquals(expected_genders_en, ((SimpleUriCollector) this.collector).getSize());
			sink.closeSinkForUri(curi);
			collector.closeSinkForUri(curi);
			
		}
	}
	
	@Test
	public void test_new_york() throws URISyntaxException, IOException {

		CrawleableUri curi = new CrawleableUri(new URI("http://dbpedia.org/resource/New_York"));
		
		ClassLoader classLoader = getClass().getClassLoader();
		File filesFolder = new File(classLoader.getResource("rdf_analyzer/new_york").getFile());
		List<File> listFiles =  TempPathUtils.searchPath4Files(filesFolder);
		
		for(File file : listFiles) {
			
			this.collector = new SimpleUriCollector(serializer);
			sink.openSinkForUri(curi);
			collector.openSinkForUri(curi);
			
			analyzer = new RDFAnalyzer(collector);
			analyzer.analyze(curi, file, sink);
			
			
			
			Assert.assertEquals(minimum_expected_new_york, ((SimpleUriCollector) this.collector).getSize());
			sink.closeSinkForUri(curi);
			collector.closeSinkForUri(curi);
		}
		
	}
}