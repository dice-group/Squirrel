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
	private Analyzer analyzer;
	private CrawleableUri curi;
	private UriCollector collector;
	private Sink sink;
	private HtmlScraper scraper;
	private List<Triple> expectedTriples;
	private Serializer serializer;

	
	@Before
	public void prepare() throws URISyntaxException, IOException {
		String dbdir = TempFileHelper.getTempDir("dbTest", "").getAbsolutePath() + File.separator + "test";
		serializer = new GzipJavaUriSerializer();
		this.sink = new InMemorySink();
		this.collector = SqlBasedUriCollector.create(serializer, dbdir);
		curi = new CrawleableUri(new URI("http://aksw.org/test"));
		configurationFile = new File("src/test/resources/yaml");
		scraper = new HtmlScraper(configurationFile);
		analyzer = new HTMLScraperAnalyzer(collector,scraper);
		
		expectedTriples = new ArrayList<Triple>();
		
		Node s = NodeFactory.createBlankNode
				("https://www.mcloud.de/web/guest/suche/-/results/detail/luftverkehrsnetznachinspire");
		expectedTriples.add(new Triple(s,
				NodeFactory.createBlankNode("rdfs:comment"),
				NodeFactory.createLiteral("INSPIRE WMS Luftverkehrsnetz (INSPIRE TN-A) mit Daten der Deutschen Flugsicherung GmbH (DFS)")));
		
		expectedTriples.add(new Triple(s,
				NodeFactory.createBlankNode("schema:downloadUrl"),
				NodeFactory.createURI("http://atlas.dlz-it.de/dfs/tn-a/wms?service=wms&request=getcapabilities")));
		
		expectedTriples.add(new Triple(s,
				NodeFactory.createBlankNode("schema:downloadUrl"),
				NodeFactory.createURI("http://atlas.dlz-it.de/dfs/tn-a/wfs?service=wfs&request=getcapabilities")));
		
		expectedTriples.add(new Triple(s,
				NodeFactory.createBlankNode("schema:license"),
				NodeFactory.createURI("http://www.gesetze-im-internet.de/geonutzv/index.html")));
		
		expectedTriples.add(new Triple(s,
				NodeFactory.createBlankNode("rdfs:label"),
				NodeFactory.createLiteral("Luftverkehrsnetz nach INSPIRE")));
		
		expectedTriples.add(new Triple(s,
				NodeFactory.createBlankNode("schema:provider"),
				NodeFactory.createURI("http://www.dfs.de")));
		
		expectedTriples.add(new Triple(s,
				NodeFactory.createBlankNode("schema:provider"),
				NodeFactory.createURI("https://www.itzbund.de")));
		
		for(Triple triple: expectedTriples) {
			collector.addTriple(curi, triple);
		}
	}
	
	@Test
	public void scrap() throws Exception {
			List<Triple> listTriples = scraper.scrape(curi.toString());
			
			Assert.assertEquals(listTriples,expectedTriples);
		
	}
	
	

}
