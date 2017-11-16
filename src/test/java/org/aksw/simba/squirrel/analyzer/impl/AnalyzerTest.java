package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.fetcher.http.HTTPFetcher;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.collect.SimpleUriCollector;
import org.aksw.simba.squirrel.sink.collect.UriCollector;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;






@SuppressWarnings("deprecation")
public class AnalyzerTest {
	
	private Analyzer analyzer;
	private CrawleableUri curi;
	private UriCollector collector;
	private Sink sink;
	private File data;
	private String uriToFetch = "http://dbpedia.org/resource/New_York";
	private HTTPFetcher fetcher = new HTTPFetcher();
	private int expectedUris = 2829;
	
	
	@Before
	public void prepare() throws URISyntaxException, UnknownHostException {
		this.sink = new FileBasedSink(new File("/home/gsjunior/test_folder"),false);
		this.collector = new SimpleUriCollector(sink);
		
		analyzer = new RDFAnalyzer(collector);
		
		curi = new CrawleableUri(new URI(uriToFetch));
		curi.setIpAddress(InetAddress.getByName("dbpedia.org"));
		curi.setType(UriType.DEREFERENCEABLE);
		
		data = fetcher.fetch(curi);
		
		
	}
	
	
	@Test
	public void test() {
		Iterator<String> uris =  analyzer.analyze(curi, data, sink);
		
		int cont = 0;
		while(uris.hasNext()) {
			System.out.println(uris.next());
			cont ++;
		}
		
		Assert.assertEquals(expectedUris, cont);
		
	}

}
