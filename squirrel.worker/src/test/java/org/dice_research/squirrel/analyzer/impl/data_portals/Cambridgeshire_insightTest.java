package org.dice_research.squirrel.analyzer.impl.data_portals;


import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.*;
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
public class Cambridgeshire_insightTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed";
        String uriDetailsPage = "https://data.cambridgeshireinsight.org.uk/dataset/empty-homes";

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/cambridgeshireinsight/cambridgeshireinsight_detail.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Empty homes"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("This data on empty homes is presented in two sections: our original data is provided for the whole of England, collated from local authority returns to the Department of Communities and Local Government (DCLG) for 2010 to 2014. Re-formatting the data and releasing it locally helps us see and use the data locally to monitor this issue - especially useful in an area of high housing pressure. Our second section of empty homes data, published in 2019, is presented under six side-headings, with one line of data for the whole of England followed by data for our eight Housing Board districts only, rather than districts across the whole country. The data comes from returns made to the Government and is simply re-presented to make it easier to use locally, and slightly more accessible. The 2019 data comes from a variety of government returns which can be found on the MHCLG web pages; is provided for 2004 to 2017, and is broken down into All vacants All long-term vacants Local authority owned vacants Private registered provider vacants (aka housing associations) Private registered provider long tem vacants (aka housing associations) Other public sector vacants (discontinued in 2015, so no values in 2016 or 2017). Notes are provided in the data dictionary for each dataset, setting out further detail."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new PropertyImpl("http://projekt-opal.de/agent/empty-homes")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://projekt-opal.de/agent/empty-homes"),
                    new PropertyImpl("http://xmlns.com/foaf/0.1/homepage"),
                    new PropertyImpl("https://data.cambridgeshireinsight.org.uk/group/housing-board")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://projekt-opal.de/agent/empty-homes"),
                    new PropertyImpl("http://xmlns.com/foaf/0.1/name"),
                    new LiteralImpl(NodeFactory.createLiteral("The Housing Board"), null)
                )
            )
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/cambridgeshireinsight/cambridgeshireinsight_search.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-road-traffic-collision-counts")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-county-council-hr-information")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/gis-maps")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/peterborough-transparency-code-payments-over-%C2%A3500")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-county-council-expenditure-over-%C2%A3500")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-historic-population-1801-2011")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/greater-cambridge-partnership-big-conversation-origindestination-data")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/innovate-cultivate-fund-adult-social-care-costings")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-policy-challenges-cambridge-university-science-and-policy-exchange-cuspe")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/location-automatic-road-traffic-and-cycle-counters-cambridgeshire")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-annual-cycle-counts-2018")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/cambridgeshire-daily-automatic-cycle-counter-count-june-2018")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/cultivate-monitoring-document")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/innovate-monitoring-documents")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#link"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/dataset/innovation-stage-2-application-templates")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C4")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C5")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C6")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C7")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C8")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C1")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C2")))
                .add(
                    new StatementImpl(
                        new ResourceImpl(uriSearchPage),
                        new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                        new ResourceImpl("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C3")))
                .add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset?sort_by=changed&q=search/field_topics/type/dataset&page=0%2C14")))

        });
        return testConfigs;
    }

    public Cambridgeshire_insightTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
