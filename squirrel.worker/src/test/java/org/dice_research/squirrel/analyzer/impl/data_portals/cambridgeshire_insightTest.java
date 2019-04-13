package org.dice_research.squirrel.analyzer.impl.data_portals;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.vocabulary.DCTerms;
import org.dice_research.squirrel.analyzer.impl.data_portals.utils.AbstractDataPortalTest;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.vocab.DCAT;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class cambridgeshire_insightTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        Node DCTermsDatasetlink = NodeFactory.createURI("http://projekt-opal.de/dataset#link");
        Node DCTermsPagination = NodeFactory.createURI("http://projekt-opal.de/dataset#pagination");
        /*testConfigs.add(new Object[]{
            new CrawleableUri(new URI("http://data.cambridgeshireinsight.org.uk")),
            new File("src/test/resources/html_scraper_analyzer/cambridgeshireinsight/cambridgeshire_index.html"),
            new ArrayList<Triple>() {{
                add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed")));
            }}
        });*/
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("https://data.cambridgeshireinsight.org.uk/dataset/empty-homes")),
            new File("src/test/resources/html_scraper_analyzer/cambridgeshireinsight/cambridgeshireinsight_detail.html"),
            new ArrayList<Triple>() {{
                add(new Triple(DCTerms.title.asNode(), DCTerms.title.asNode(),
                    NodeFactory.createLiteral("Empty homes")));
                add(new Triple(DCTerms.description.asNode(), DCTerms.description.asNode(),
                    NodeFactory.createLiteral("This data on empty homes is presented in two sections: our original data is provided for the whole of England, collated from local authority returns to the Department of Communities and Local Government (DCLG) for 2010 to 2014. Re-formatting the data and releasing it locally helps us see and use the data locally to monitor this issue - especially useful in an area of high housing pressure. Our second section of empty homes data, published in 2019, is presented under six side-headings, with one line of data for the whole of England followed by data for our eight Housing Board districts only, rather than districts across the whole country. The data comes from returns made to the Government and is simply re-presented to make it easier to use locally, and slightly more accessible. The 2019 data comes from a variety of government returns which can be found on the MHCLG web pages; is provided for 2004 to 2017, and is broken down into All vacants All long-term vacants Local authority owned vacants Private registered provider vacants (aka housing associations) Private registered provider long tem vacants (aka housing associations) Other public sector vacants (discontinued in 2015, so no values in 2016 or 2017). Notes are provided in the data dictionary for each dataset, setting out further detail.")));
                add(new Triple(DCTerms.publisher.asNode(), DCTerms.publisher.asNode(),
                    NodeFactory.createURI("http://projekt-opal.de/agent/empty-homes")));
                add(new Triple(NodeFactory.createURI("http://xmlns.com/foaf/0.1/homepage"), NodeFactory.createURI("http://xmlns.com/foaf/0.1/homepage"),
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/group/housing-board")));
                add(new Triple(NodeFactory.createURI("http://xmlns.com/foaf/0.1/name"), NodeFactory.createURI("http://xmlns.com/foaf/0.1/name"),
                    NodeFactory.createLiteral("The Housing Board")));
            }}
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed")),
            new File("src/test/resources/html_scraper_analyzer/cambridgeshireinsight/cambridgeshireinsight_search.html"),
            new ArrayList<Triple>() {{
                add(new Triple(DCTermsDatasetlink,DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-road-traffic-collision-counts")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-county-council-hr-information")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/gis-maps")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/peterborough-transparency-code-payments-over-%C2%A3500")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-county-council-expenditure-over-%C2%A3500")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-historic-population-1801-2011")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/greater-cambridge-partnership-big-conversation-origindestination-data")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/innovate-cultivate-fund-adult-social-care-costings")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-policy-challenges-cambridge-university-science-and-policy-exchange-cuspe")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/location-automatic-road-traffic-and-cycle-counters-cambridgeshire")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-annual-cycle-counts-2018")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-daily-automatic-cycle-counter-count-june-2018")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/cultivate-monitoring-document")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/innovate-monitoring-documents")));
                add(new Triple(DCTermsDatasetlink, DCTermsDatasetlink,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/dataset/innovation-stage-2-application-templates")));
                add(new Triple(DCTermsPagination, DCTermsPagination,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C4")));
                add(new Triple(DCTermsPagination, DCTermsPagination,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C5")));
                add(new Triple(DCTermsPagination, DCTermsPagination,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C6")));
                add(new Triple(DCTermsPagination, DCTermsPagination,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C7")));
                add(new Triple(DCTermsPagination, DCTermsPagination,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C8")));
                add(new Triple(DCTermsPagination, DCTermsPagination,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C1")));
                add(new Triple(DCTermsPagination, DCTermsPagination,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C2")));
                add(new Triple(DCTermsPagination, DCTermsPagination,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C3")));
                add(new Triple(DCTermsPagination, DCTermsPagination,
                    NodeFactory.createURI("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C14")));
            }}
        });
        return testConfigs;
    }

    public cambridgeshire_insightTest(CrawleableUri uri, File fileToScrape, List<Triple> expectedTriples) {
        super(uri, fileToScrape, expectedTriples);
    }
}
