package org.dice_research.squirrel.analyzer.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.analyzer.impl.html.scraper.HTMLScraperAnalyzer;
import org.dice_research.squirrel.analyzer.impl.html.scraper.HtmlScraper;
import org.dice_research.squirrel.collect.SimpleUriCollector;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.sink.impl.mem.InMemorySink;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


// Ignore for the release
@Ignore
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
		 listTriples.addAll(scraper.scrape(curi, fetchedFile));
		
		 Assert.assertEquals(expectedTriplesMcloudDetail, listTriples);	
		
	}
	
	@Test
	public void scrapResultPagelMcloud() throws Exception {
		CrawleableUri curi = new CrawleableUri(new URI("https://www.mcloud.de/web/guest/suche/-/results/searchAction?_mysearchportlet_aggsChoice=extras.subgroups%3A%22roads%22"));
		fetchedFile = new File("src/test/resources/html_scraper_analyzer/mcloud/mcloud_resultpage.html");
		
		 List<Triple> listTriples = new ArrayList<Triple>();
		 listTriples.addAll(scraper.scrape(curi, fetchedFile));
		
		 Assert.assertEquals(expectedTriplesMcloudResultPage, listTriples);	
	}
	
	@Test
	public void scrapDetailGovData() throws Exception {
		CrawleableUri curi = new CrawleableUri(new URI("https://www.govdata.de/web/guest/daten/-/details/jahresbericht-der-bundespolizei-2014"));
		fetchedFile = new File("src/test/resources/html_scraper_analyzer/govdata/govdata_detail.html");
		
		 List<Triple> listTriples = new ArrayList<Triple>();
		 listTriples.addAll(scraper.scrape(curi, fetchedFile));
		 
		 
	}

	@Test
    public void scrapeDetailGovDataJS() throws Exception{
        CrawleableUri curi = new CrawleableUri(new URI("https://www.govdata.de/web/guest/suchen/-/searchresult/q/Patienten/s/relevance_desc"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/govdata/govdata_patienten.html");
        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapDetailcambridgeshireinsight() throws Exception {
        Node s = NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/empty-homes");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,NodeFactory.createURI("http://purl.org/dc/terms/title"),
            NodeFactory.createLiteral("Empty homes")));
        expectedTriples.add(new Triple(s,NodeFactory.createURI("http://purl.org/dc/terms/description"),
            NodeFactory.createLiteral("This data on empty homes is presented in two sections: our original data is provided for the whole of England, collated from local authority returns to the Department of Communities and Local Government (DCLG) for 2010 to 2014. Re-formatting the data and releasing it locally helps us see and use the data locally to monitor this issue - especially useful in an area of high housing pressure. Our second section of empty homes data, published in 2019, is presented under six side-headings, with one line of data for the whole of England followed by data for our eight Housing Board districts only, rather than districts across the whole country. The data comes from returns made to the Government and is simply re-presented to make it easier to use locally, and slightly more accessible. The 2019 data comes from a variety of government returns which can be found on the MHCLG web pages; is provided for 2004 to 2017, and is broken down into All vacants All long-term vacants Local authority owned vacants Private registered provider vacants (aka housing associations) Private registered provider long tem vacants (aka housing associations) Other public sector vacants (discontinued in 2015, so no values in 2016 or 2017). Notes are provided in the data dictionary for each dataset, setting out further detail.")));
        expectedTriples.add(new Triple(s,NodeFactory.createURI("http://xmlns.com/foaf/0.1/homepage"),
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/group/housing-board")));
        expectedTriples.add(new Triple(s,NodeFactory.createURI("http://xmlns.com/foaf/0.1/name"),
            NodeFactory.createLiteral("The Housing Board")));
        expectedTriples.add(new Triple(s,NodeFactory.createURI("http://xmlns.com/foaf/0.1/homepage"),
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/group/housing-board")));

        CrawleableUri curi = new CrawleableUri(new URI("https://data.cambridgeshireinsight.org.uk/dataset/empty-homes"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/cambridgeshireinsight/cambridgeshireinsight_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);

    }

    @Test
    public void scrapeCambridgeshireinsightHU() throws Exception{
        Node s = NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset");
        Node datasetlink = NodeFactory.createURI("http://projekt-opal.de/dataset#link");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-road-traffic-collision-counts")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-county-council-hr-information")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/gis-maps")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/peterborough-transparency-code-payments-over-%C2%A3500")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-county-council-expenditure-over-%C2%A3500")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-historic-population-1801-2011")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/greater-cambridge-partnership-big-conversation-origindestination-data")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/innovate-cultivate-fund-adult-social-care-costings")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-policy-challenges-cambridge-university-science-and-policy-exchange-cuspe")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/location-automatic-road-traffic-and-cycle-counters-cambridgeshire")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-annual-cycle-counts-2018")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-daily-automatic-cycle-counter-count-june-2018")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cultivate-monitoring-document")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/innovation-stage-2-application-templates")));

        CrawleableUri curi = new CrawleableUri(new URI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/cambridgeshireinsight/cambridgeshireinsight_search.html");
        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);
    }

    @Test
    public void scrapDetailOtvorenipodatoci() throws Exception {
        Node s = NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/dataset/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,NodeFactory.createURI("http://purl.org/dc/terms/title"),
            NodeFactory.createLiteral("Paid funds by measure for 2017")));

        CrawleableUri curi = new CrawleableUri(new URI("https://www.otvorenipodatoci.gov.mk/dataset/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/otvorenipodatoci/otvorenipodatoci_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);

    }

    @Test
    public void scrapOtvorenipodatociHU() throws Exception {
        Node s = NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset");
        Node datasetlink = NodeFactory.createURI("http://projekt-opal.de/dataset#link");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/ttokapaktepnctnhhn-dejia-od-o6jiacta-ha-ekohomcknot-n-kpnmnhaji-od-2010-do-2107-rodnha")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/camoy6nctba-od-2010-do-2017-rodnha")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/pernctap-3a-mecehhn-n3bewtan")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/ttodatoun-3a-kopnchnun-ha-boda-od-pb-ctydehhnua-3a-nepnod-1992-2018-rodnha")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/n3dawhoct-ha-n3bopot-ctydehhnua-3a-2019-rodnha-ttpotok")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/n3dawhoct-ha-n3bopot-ctydehhnua-3a-2018-rodnha-bodoctoj")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/n3dawhoct-ha-n3bopot-ctydehhnua-3a-2018-rodnha-ttpotok")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/cymapeh-n3bewtaj-deua-no-tnn-ha-yctahoba-n-onwtnha")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/cymapeh-n3bewtaj-deua-no-kateropnja-ha-yctahoba")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/uehn-ha-3emjodejickn-npon3bodn")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/n3bewtaj-3a-cemejho-hacnjictbo")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/n3dawhoct-ha-n3bopot-ctydehhnua-3a-2019-rodnha-bodoctoj")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/cymapeh-nperjied-3a-dbnxehbe-ha-npedmetnte-3a-meceu-02-2019")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/pernctap-ha-otkynybahn-ha-3emjodejickn-npon3bodn")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/bpeme-ha-npouecnpahbe-ha-tpah3nthn-dokymehtn-ha-rpahnua")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/jabhn-orjiacn-3a-bpa6otybahbe-ha-admnhnctpatnbhn-cjiyx6ehnun-2019")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/nhtephn-orjiacn-3a-yhanpedybahbe-ha-admnhnctpatnbhn-cjiyx6ehnun-2019")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/o6pa6otehn-xaji6n-n-npnrobopn-ha-admnhnctpatnbhn-cjiyx6ehnun-2019")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/ncnntn-3a-admnhnctpatnbho-ynpabybahbe-2019")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.otvorenipodatoci.gov.mk/en/dataset/cnpobedehn-aktnbhoctn-no-orjiacn-2019")));

        CrawleableUri curi = new CrawleableUri(new URI("https://www.otvorenipodatoci.gov.mk/en/dataset"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/otvorenipodatoci/otvorenipodatoci_search.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);

    }

    @Test
    public void scrapDetailDatagovsk() throws Exception {
        Node s = NodeFactory.createURI("https://data.gov.sk/dataset/register-adries-register-budov");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,NodeFactory.createURI("http://purl.org/dc/terms/title"),
            NodeFactory.createLiteral("Register Adries - Register budov (súpisných čísiel)")));


        CrawleableUri curi = new CrawleableUri(new URI("https://data.gov.sk/dataset/register-adries-register-budov"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/datagovsk/datagovsk_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);

    }

    @Test
    public void scrapDatagovskHU() throws Exception {
        Node s = NodeFactory.createURI("https://data.gov.sk/en/dataset");
        Node datasetlink = NodeFactory.createURI("http://projekt-opal.de/dataset#link");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/register-adries-register-casti-obci")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/register-adries-register-obci")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/register-adries-register-bytov")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/register-adries-register-vchodov")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/register-adries-register-ulic")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/register-adries-register-krajov")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/register-adries-register-budov")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/register-adries-register-okresov")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/register-adries-ra-zmenove-davky")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/statistika-navstevnosti-a-trzieb-slovenskeho-banskeho-muzea-v-banskej-stiavnici")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/zoznam-datasetov-rezortu-mzp-sr-pre-portal-otvorenych-dat-2017")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/pocet-rozhodnuti-podani-a-notifikacii-zaslanych-cez-upvs")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/zoznam-rozhodnuti-pmu-sr")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/zoznam-faktur-pmu-sr")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/pocet-pristupov-na-www-antimon-gov-sk")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/zoznam-objednavok-pmu-sr")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/np3106rr")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/0021")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/0022")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://data.gov.sk/en/dataset/0023")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("")));

        CrawleableUri curi = new CrawleableUri(new URI("https://data.gov.sk/en/dataset"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/datagovsk/datagovsk_search.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);

    }

    @Test
    public void scrapSSA() throws Exception {
        Node s = NodeFactory.createURI("https://www.ssa.gov/open/data");
        Node datasetlink = NodeFactory.createURI("http://projekt-opal.de/dataset#link");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/data")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/plan-progress-chart.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/retirement-insurance-online-apps-2012-onward.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/retirement-insurance-online-apps-2012-onward.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/retirement-insurance-online-apps.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/initial-disability-insurance-online-apps-2012-onward.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/initial-disability-insurance-online-apps-2012-onward.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/initial-disability-insurance-online-apps.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/medicare-replacement-card-online-apps.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/medicare-replacement-card-online-apps.html#satisfactionWithServiceForInternetMedicareReplacementCards")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/direct-deposit-online-apps.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/direct-deposit-online-apps.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/direct-deposit-online-apps.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/change-of-address-online-apps.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/change-of-address-online-apps.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/change-of-address-online-apps.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/AAPI-Language-Preferences-yearly-SSRS.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/AAPI-Language-Preferences-yearly-SSRS.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/AAPI-Language-Preferences-yearly-SSRS.html")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.ssa.gov/open/data/AAPI-Language-Preferences-quarterly-SSRS.html")));

        CrawleableUri curi = new CrawleableUri(new URI("https://www.ssa.gov/open/data"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/ssagov/ssagov_datasets.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);

    }

    @Test
    public void scrapDetailBilbao() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("http://www.bilbao.eus/opendata/catalogo/dato-agenda-cultural"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/bilbao/bilbao_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapDetailDatago() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("https://www.data.go.jp/data/dataset/mlit_20190201_0005"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/datago/datago_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapDetailOpendatajena() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("https://opendata.jena.de/dataset/vornamen"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/opendatajena/opendatajena_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapDetailottawaca() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("http://data.ottawa.ca/dataset/ball-diamonds"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/ottawaca/ottawaca_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapebermuda() throws Exception {
        Node s = NodeFactory.createURI("http://bermuda.io/dataset?q=&sort=score+desc%2C+metadata_modified+desc");
        Node datasetlink = NodeFactory.createURI("http://projekt-opal.de/dataset#link");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/budget-book-estimates-of-revenue-and-expenditure-for-the-year")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/financial-instructions")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/pati-information-statements")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/corporation-of-hamilton-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/bermuda-college-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/consolidated-fund-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/historical-forward-planning-documents")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/board-of-trustees-of-the-golf-courses-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/government-employees-health-insurance-gehi-fund-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/reports-on-education")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/berkeley-institute-capitation-grant-account-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/cedarbridge-academy-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/national-drug-commission-ndc-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/bermuda-housing-corporation-bhc-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/public-service-superannuation-fund-pssf-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/ministers-and-members-of-the-legislature-pensions-fund-mmlpf-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/contributory-pension-fund-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/bermuda-hospitals-board-bhb-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/bermuda-land-development-corporation-bldc-audited-financials")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("http://bermuda.io/dataset/bermuda-digest-of-statistics")));


        CrawleableUri curi = new CrawleableUri(new URI("http://bermuda.io/dataset?q=&sort=score+desc%2C+metadata_modified+desc"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/bermuda/bermuda_search.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);

    }

    @Test
    public void scrapDetailbermuda() throws Exception {
        Node s = NodeFactory.createURI("http://bermuda.io/dataset/budget-book-estimates-of-revenue-and-expenditure-for-the-year");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,NodeFactory.createURI("http://purl.org/dc/terms/title"),
            NodeFactory.createLiteral("budget book estimates of revenue and expenditure for the year")));

        CrawleableUri curi = new CrawleableUri(new URI("http://bermuda.io/dataset/budget-book-estimates-of-revenue-and-expenditure-for-the-year"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/bermuda/bermuda_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);

    }

    @Test
    public void scrapeDataBrisbane() throws Exception {
        Node s = NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset");
        Node datasetlink = NodeFactory.createURI("http://projekt-opal.de/dataset#link");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/traffic-data-at-intersection-api")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/brisbane-parking-stations")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-streetscape-hierarchy-overlay-streetscape-hierarchy")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/planned-temporary-road-occupancies")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/traffic-signal-location-reference")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/traffic-corridors-average-peak-travel-times")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/flood-study-citywide-overland-flow-airport-sub-model")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/flood-study-citywide-overland-flow-reference")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/flood-study-citywide-overland-flow-upper-kedron-sub-model")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/flood-study-citywide-overland-flow-inner-west-sub-model\n")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/flood-study-citywide-overland-flow-central-sub-model")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/flood-study-citywide-overland-flow-norman-sub-model")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/grants")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/citycat-cityferry-cityhopper-timetables")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/corporate-website-analytics-2019")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/food-safety-permits")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/waterway-health-recreational")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/disability-permit-parking-locations")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/road-restrictions")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/vegetation-2015-regional-ecosystems-cover-codes")));
        expectedTriples.add(new Triple(s,datasetlink,
            NodeFactory.createURI("")));


        CrawleableUri curi = new CrawleableUri(new URI("https://www.data.brisbane.qld.gov.au/data/dataset"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/databrisbane/databrisbane_search.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);

    }

    @Test
    public void scrapeDetailDataBrisbane() throws Exception {
        Node s = NodeFactory.createURI("https://www.data.brisbane.qld.gov.au/data/dataset/brisbane-parking-stations");
        Node datasetlink = NodeFactory.createURI("http://projekt-opal.de/dataset#link");

        List<Triple> expectedTriples = new ArrayList<>();

        expectedTriples.add(new Triple(s,NodeFactory.createURI("http://purl.org/dc/terms/title"),
            NodeFactory.createLiteral("brisbane parking stations")));

        CrawleableUri curi = new CrawleableUri(new URI("https://www.data.brisbane.qld.gov.au/data/dataset/brisbane-parking-stations"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/databrisbane/databrisbane_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        Assert.assertEquals(expectedTriples, listTriples);

    }

    @Test
    public void scrapeEgis3Lacounty() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("https://egis3.lacounty.gov/dataportal/data-catalog/"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/egis3_lacounty/egis3_lacounty_datacatalog.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapeDetailEgis3Lacounty() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("http://egis3.lacounty.gov/dataportal/2011/01/27/county-lighting-maintenance-district/"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/egis3_lacounty/egis3_lacounty_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }//
    @Test
    public void scrapeDadosFortaleza() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("http://dados.fortaleza.ce.gov.br/catalogo/dataset?q=&sort=score+desc%2C+metadata_modified+desc"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/dados_fortaleza/dados_fortaleza__search.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapeDetailDadosFortaleza() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("http://dados.fortaleza.ce.gov.br/catalogo/dataset/http-www-fortaleza-ce-gov-br-sites-default-files-rede-de-atencao-e-cuidados-de-fortaleza-pdf"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/dados_fortaleza/dados_fortaleza_detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapeUsviber() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("http://www.usviber.org/archived-data/"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/usviber/usviber_data.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapeDetailUsviber() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("http://www.usviber.org/cruise-visitor-arrivals/"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/usviber/usviber__detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapePalaugov() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("https://www.palaugov.pw/?s="));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/palaugov/palaugov_search.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

    @Test
    public void scrapeDetailPalaugov() throws Exception {
        CrawleableUri curi = new CrawleableUri(new URI("https://www.palaugov.pw/president-remengesau-hosts-state-banquet-in-honor-of-president-tsai-ing-wen/"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/palaugov/palaugov__detail.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
    }

	public void testAnalyzer() throws URISyntaxException {
		CrawleableUri curi = new CrawleableUri(new URI("https://data.cambridgeshireinsight.org.uk/dataset/empty-homes"));
		fetchedFile = new File("src/test/resources/html_scraper_analyzer/cambridgeshireinsight/cambridgeshireinsight_detail.html");
	
		sink.openSinkForUri(curi);
		collector.openSinkForUri(curi);
		Analyzer analyzer = new HTMLScraperAnalyzer(collector);
		
	
		
		analyzer.analyze(curi, fetchedFile, sink);
		
	}
	
	

}
