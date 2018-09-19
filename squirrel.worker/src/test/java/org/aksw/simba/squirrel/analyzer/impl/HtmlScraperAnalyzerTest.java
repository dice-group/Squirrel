package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.analyzer.impl.html.scraper.HTMLScraperAnalyzer;
import org.aksw.simba.squirrel.analyzer.impl.html.scraper.HtmlScraper;
import org.aksw.simba.squirrel.collect.SimpleUriCollector;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.mem.InMemorySink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HtmlScraperAnalyzerTest {
	
	private File configurationFile;
	private File fetchedFile;
	private List<Triple> expectedTriplesMcloudDetail;
	private List<Triple> expectedTriplesMcloudResultPage;
	private UriCollector collector = new SimpleUriCollector(new GzipJavaUriSerializer());
	private Sink sink = new InMemorySink();

	private HtmlScraper scraper;


	
	@Before
	public void prepareGeneral() throws URISyntaxException, IOException {
		configurationFile = new File("src/test/resources/html_scraper_analyzer/yaml");
		scraper = new HtmlScraper(configurationFile);
	}
	
	@Before
	public void prepareDetailMcloud() {
		expectedTriplesMcloudDetail = new ArrayList<Triple>();
		Node s = NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/verkehrslageaufautobahnenschleifenhamburg");
		Node downloadUrl = NodeFactory.createURI("http://schema.org/downloadUrl");
		expectedTriplesMcloudDetail.add(new Triple(s, downloadUrl
				, NodeFactory.createURI("http://geodienste.hamburg.de/HH_WFS_Verkehr_opendata?SERVICE=WFS&REQUEST=GetFeature&VERSION=1.1.0&TYPENAME=bab_vkl")));
		
		expectedTriplesMcloudDetail.add(new Triple(s, downloadUrl
				, NodeFactory.createURI("http://geodienste.hamburg.de/HH_WMS_Verkehr_opendata?REQUEST=GetCapabilities&SERVICE=WMS")));
		
		expectedTriplesMcloudDetail.add(new Triple(s, downloadUrl
				, NodeFactory.createURI("http://geoportal-hamburg.de/verkehrsportal/")));
		
		expectedTriplesMcloudDetail.add(new Triple(s, downloadUrl
				, NodeFactory.createURI("http://geodienste.hamburg.de/HH_WFS_Verkehr_opendata?REQUEST=GetCapabilities&SERVICE=WFS")));
		
		expectedTriplesMcloudDetail.add(new Triple(s, NodeFactory.createURI("http://www.w3.org/2000/01/rdf-schema#label")
				, NodeFactory.createLiteral("Verkehrslage auf Autobahnen (Schleifen) Hamburg")));
		
		expectedTriplesMcloudDetail.add(new Triple(s, NodeFactory.createURI("http://www.w3.org/2000/01/rdf-schema#comment")
				, NodeFactory.createLiteral("Darstellung der Verkehrslage auf Autobahnen, die auf Grundlage von Schleifendaten ermittelt und erzeugt wird.")));
		
		expectedTriplesMcloudDetail.add(new Triple(s, NodeFactory.createURI("http://schema.org/provider")
				, NodeFactory.createURI("http://www.hamburg.de/bwvi/verkehr-strassenwesen/")));
		
		expectedTriplesMcloudDetail.add(new Triple(s, NodeFactory.createURI("http://schema.org/license")
				, NodeFactory.createURI("https://www.govdata.de/dl-de/by-2-0")));
		
	}
	
	@Before
	public void prepareResultPageMCloud() {
		Node s = NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/searchAction?_mysearchportlet_aggsChoice=extras.subgroups%3A%22roads%22");
		Node p = NodeFactory.createURI("http://sindice.com/vocab/search#link");
		
		expectedTriplesMcloudResultPage = new ArrayList<Triple>();
		
		expectedTriplesMcloudResultPage.add(new Triple(s,p,
				NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/stadtbonnfahrraddialog2017?_mysearchportlet_backURL=https%3A%2F%2Fwww.mcloud.de%2Fweb%2Fguest%2Fsuche%2F-%2Fresults%2FsearchAction%3F_mysearchportlet_currentAggs%3Dextras.subgroups%253A%2522roads%2522%26_mysearchportlet_page%3D0")));
		
		expectedTriplesMcloudResultPage.add(new Triple(s,p,
				NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/stadtbonnverwarn-undbugelderruhenderverkehr?_mysearchportlet_backURL=https%3A%2F%2Fwww.mcloud.de%2Fweb%2Fguest%2Fsuche%2F-%2Fresults%2FsearchAction%3F_mysearchportlet_currentAggs%3Dextras.subgroups%253A%2522roads%2522%26_mysearchportlet_page%3D0")));
		
		expectedTriplesMcloudResultPage.add(new Triple(s,p,
				NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/vbb-fahrplandatenapi?_mysearchportlet_backURL=https%3A%2F%2Fwww.mcloud.de%2Fweb%2Fguest%2Fsuche%2F-%2Fresults%2FsearchAction%3F_mysearchportlet_currentAggs%3Dextras.subgroups%253A%2522roads%2522%26_mysearchportlet_page%3D0")));
		
		expectedTriplesMcloudResultPage.add(new Triple(s,p,
				NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/stadtbonnverkehrsdatenpkwlkwbussdrmessergebnisse?_mysearchportlet_backURL=https%3A%2F%2Fwww.mcloud.de%2Fweb%2Fguest%2Fsuche%2F-%2Fresults%2FsearchAction%3F_mysearchportlet_currentAggs%3Dextras.subgroups%253A%2522roads%2522%26_mysearchportlet_page%3D0")));
		
		expectedTriplesMcloudResultPage.add(new Triple(s,p,
				NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/vrs-fahrplandatenapi?_mysearchportlet_backURL=https%3A%2F%2Fwww.mcloud.de%2Fweb%2Fguest%2Fsuche%2F-%2Fresults%2FsearchAction%3F_mysearchportlet_currentAggs%3Dextras.subgroups%253A%2522roads%2522%26_mysearchportlet_page%3D0")));
		
		expectedTriplesMcloudResultPage.add(new Triple(s,p,
				NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/berlinelektro-ladestationeninberlin?_mysearchportlet_backURL=https%3A%2F%2Fwww.mcloud.de%2Fweb%2Fguest%2Fsuche%2F-%2Fresults%2FsearchAction%3F_mysearchportlet_currentAggs%3Dextras.subgroups%253A%2522roads%2522%26_mysearchportlet_page%3D0")));
		
		expectedTriplesMcloudResultPage.add(new Triple(s,p,
				NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/berlinverkehrlichevorkommnisseincidents?_mysearchportlet_backURL=https%3A%2F%2Fwww.mcloud.de%2Fweb%2Fguest%2Fsuche%2F-%2Fresults%2FsearchAction%3F_mysearchportlet_currentAggs%3Dextras.subgroups%253A%2522roads%2522%26_mysearchportlet_page%3D0")));
		
		expectedTriplesMcloudResultPage.add(new Triple(s,p,
				NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/mautdatenbund?_mysearchportlet_backURL=https%3A%2F%2Fwww.mcloud.de%2Fweb%2Fguest%2Fsuche%2F-%2Fresults%2FsearchAction%3F_mysearchportlet_currentAggs%3Dextras.subgroups%253A%2522roads%2522%26_mysearchportlet_page%3D0")));
		
		expectedTriplesMcloudResultPage.add(new Triple(s,p,
				NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/mdmarbeitsstellenlngererdaueraufbabinsachsen-anhalt?_mysearchportlet_backURL=https%3A%2F%2Fwww.mcloud.de%2Fweb%2Fguest%2Fsuche%2F-%2Fresults%2FsearchAction%3F_mysearchportlet_currentAggs%3Dextras.subgroups%253A%2522roads%2522%26_mysearchportlet_page%3D0")));
		
		expectedTriplesMcloudResultPage.add(new Triple(s,p,
				NodeFactory.createURI("https://www.mcloud.de/web/guest/suche/-/results/detail/mdmarbeitsstellenlngererdaueraufbabinthringen?_mysearchportlet_backURL=https%3A%2F%2Fwww.mcloud.de%2Fweb%2Fguest%2Fsuche%2F-%2Fresults%2FsearchAction%3F_mysearchportlet_currentAggs%3Dextras.subgroups%253A%2522roads%2522%26_mysearchportlet_page%3D0")));
		
		
	}
	
	@Test
	public void scrapDetailMcloud() throws Exception {
		CrawleableUri curi = new CrawleableUri(new URI("https://www.mcloud.de/web/guest/suche/-/results/detail/verkehrslageaufautobahnenschleifenhamburg"));
		fetchedFile = new File("src/test/resources/html_scraper_analyzer/mcloud/mcloud_detail.html");
		
		 List<Triple> listTriples = new ArrayList<Triple>();
		 listTriples.addAll(scraper.scrape(curi.getUri().toString(), fetchedFile));
		
		 Assert.assertEquals(expectedTriplesMcloudDetail, listTriples);	
		
	}
	
	@Test
	public void scrapResultPagelMcloud() throws Exception {
		CrawleableUri curi = new CrawleableUri(new URI("https://www.mcloud.de/web/guest/suche/-/results/searchAction?_mysearchportlet_aggsChoice=extras.subgroups%3A%22roads%22"));
		fetchedFile = new File("src/test/resources/html_scraper_analyzer/mcloud/mcloud_resultpage.html");
		
		 List<Triple> listTriples = new ArrayList<Triple>();
		 listTriples.addAll(scraper.scrape(curi.getUri().toString(), fetchedFile));
		
		 Assert.assertEquals(expectedTriplesMcloudResultPage, listTriples);	
	}
	
	@Test
	public void scrapDetailGovData() throws Exception {
		CrawleableUri curi = new CrawleableUri(new URI("https://www.govdata.de/web/guest/daten/-/details/jahresbericht-der-bundespolizei-2014"));
		fetchedFile = new File("src/test/resources/html_scraper_analyzer/govdata/govdata_detail.html");
		
		 List<Triple> listTriples = new ArrayList<Triple>();
		 listTriples.addAll(scraper.scrape(curi.getUri().toString(), fetchedFile));
		 
		 
	}
	
	public void testAnalyzer() throws URISyntaxException {
		CrawleableUri curi = new CrawleableUri(new URI("https://www.govdata.de/web/guest/daten/-/details/jahresbericht-der-bundespolizei-2014"));
		fetchedFile = new File("src/test/resources/html_scraper_analyzer/govdata/govdata_detail.html");
	
		sink.openSinkForUri(curi);
		collector.openSinkForUri(curi);
		Analyzer analyzer = new HTMLScraperAnalyzer(collector);
		
	
		
		analyzer.analyze(curi, fetchedFile, sink);
		
	}
	
	

}