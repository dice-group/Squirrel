package org.dice_research.squirrel.analyzer.impl.data_portals;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.*;
import org.apache.jena.vocabulary.DCTerms;
import org.dice_research.squirrel.analyzer.impl.data_portals.utils.AbstractDataPortalTest;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class DataReitroutenPlannerTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "http://data-reitroutenplaner.opendata.arcgis.com/datasets";
        String uriDetailsPage = "http://data-reitroutenplaner.opendata.arcgis.com/datasets/56941fd831b34abc9da66f405670036a_0";

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/data_reitroutenplaner/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://data-reitroutenplaner.opendata.arcgis.com/datasets/56941fd831b34abc9da66f405670036a_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://data-reitroutenplaner.opendata.arcgis.com/datasets/56941fd831b34abc9da66f405670036a_0")
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/data_reitroutenplaner/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("Altersheim Restis"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.publisher,
                    new LiteralImpl(NodeFactory.createLiteral("schoolgis_chr.sailer"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://purl.org/dsnotify/vocab/eventset/sourceDataset"),
                    new ResourceImpl("https://services1.arcgis.com/6RDtDcHz3yZdtEVu/arcgis/rest/services/Altersheim_Restis/FeatureServer/0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://www.w3.org/ns/dcat#accessURL"),
                    new ResourceImpl("https://www.arcgis.com/home/item.html?id=56941fd831b34abc9da66f405670036a")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.license,
                    new LiteralImpl(NodeFactory.createLiteral("License No license specified"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.modified,
                    new LiteralImpl(NodeFactory.createLiteral("Updated: 5 years ago"), null)
                )
            )
        });
        return testConfigs;
    }

    public DataReitroutenPlannerTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
