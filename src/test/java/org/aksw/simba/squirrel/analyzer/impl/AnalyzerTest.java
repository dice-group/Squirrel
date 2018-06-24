package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.SimpleUriCollector;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.fetcher.http.HTTPFetcher;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.sink.impl.mem.InMemorySink;
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
	private String fileToTest = "new_york.rdf";
	private int expectedUris = 2829;


    @Before
	public void prepare() throws URISyntaxException, UnknownHostException {

		this.sink = new InMemorySink();
		this.collector = new SimpleUriCollector(new GzipJavaUriSerializer());

        analyzer = new RDFAnalyzer(collector);

        curi = new CrawleableUri(new URI(uriToFetch));
		curi.setIpAddress(InetAddress.getByName("dbpedia.org"));
		curi.setType(UriType.DEREFERENCEABLE);

        sink.openSinkForUri(curi);
		collector.openSinkForUri(curi);

        ClassLoader classLoader = getClass().getClassLoader();

        data = new File(classLoader.getResource(fileToTest).getFile());

    }


	@Test
	public void test() {
		Iterator<byte[]> uris =  analyzer.analyze(curi, data, sink);

        int cont = 0;
		while(uris.hasNext()) {
			cont ++;
            uris.next();
        }

		Assert.assertEquals(expectedUris, cont);

    }

}
