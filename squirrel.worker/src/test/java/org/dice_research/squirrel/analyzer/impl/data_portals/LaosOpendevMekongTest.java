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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class LaosOpendevMekongTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriIndexPage = "https://laos.opendevelopmentmekong.net/";
        String uriSearchPage = "https://laos.opendevelopmentmekong.net/search/data/";
        String uriDetailsPage = "https://laos.opendevelopmentmekong.net/dataset/";

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriIndexPage)),
            new File("src/test/resources/html_scraper_analyzer/laos_opendevelopmentmekong/index.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriIndexPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/search/data/")
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/laos_opendevelopmentmekong/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/search/data/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=economic-corridors-of-laos&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=railway-lines-of-laos&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=airports-of-laos&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=registering-property-indicators-laos&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=soil-types-of-laos&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=river-networks-of-laos&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=laos-aqueduct-atlas&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=residence-areas-of-lao-ethno-linguistic-group-lao-tai&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=main-tourist-sites-of-laos&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=faostat-lao-people-s-democratic-republic&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=gms-major-urban-areas-laos&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/dataset/?id=laos-protected-areas-and-heritage-sites&search_query=Pw==")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/search/data/?page=3")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/search/data/?page=2")
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/laos_opendevelopmentmekong/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://laos.opendevelopmentmekong.net/search/data/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("Registering property indicators, Laos"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.description,
                    new LiteralImpl(NodeFactory.createLiteral("Three of the Laos' property indicators from the World Bank Doing Business report, 2016. Collated for the Laos Land page: https://opendevelopmentmekong.net/topics/land-laos"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.publisher,
                    new LiteralImpl(NodeFactory.createLiteral("Source: World Bank. Doing Business Report 2016. http://www.doingbusiness.org/custom-query"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.creator,
                    new LiteralImpl(NodeFactory.createLiteral("Source: World Bank. Doing Business Report 2016. http://www.doingbusiness.org/custom-query"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.modified,
                    new LiteralImpl(NodeFactory.createLiteral("24 Jun 2016"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.issued,
                    new LiteralImpl(NodeFactory.createLiteral("17 May 2016"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.distribution,
                    new LiteralImpl(NodeFactory.createLiteral("Source: World Bank. Doing Business Report 2016. http://www.doingbusiness.org/custom-query"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.language,
                    new LiteralImpl(NodeFactory.createLiteral("English"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.license,
                    new ResourceImpl("https://creativecommons.org/licenses/by/4.0/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://data.opendevelopmentmekong.net/dataset/9d9a2f88-0a89-41bb-8f81-6741253c6e89/resource/6ae58114-7a60-4631-bd2d-085b5c8c6650/download/registering-property-indicators-laos.csv")
                )
            )
        });
        return testConfigs;
    }

    public LaosOpendevMekongTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
