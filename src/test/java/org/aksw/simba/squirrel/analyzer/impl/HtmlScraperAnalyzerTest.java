package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.simba.squirrel.analyzer.htmlscraper.HtmlScraper;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
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
		
		 for(Triple triple : listTriples) {
			 System.out.println(triple);
		 }
	}
	
	@Test
	public void scrapDetailGovData() throws Exception {
		CrawleableUri curi = new CrawleableUri(new URI("https://www.govdata.de/web/guest/daten/-/details/jahresbericht-der-bundespolizei-2014"));
		fetchedFile = new File("src/test/resources/html_scraper_analyzer/govdata/govdata_detail.html");
		
		 List<Triple> listTriples = new ArrayList<Triple>();
		 listTriples.addAll(scraper.scrape(curi.getUri().toString(), fetchedFile));
		 
		 
	}
	
	

}
