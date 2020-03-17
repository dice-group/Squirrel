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
public class DataSofartdkTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "http://data-sofartdk.opendata.arcgis.com/datasets?";
        String uriDetailsPage = "http://data-sofartdk.opendata.arcgis.com/datasets/944f233ffc2049e79d19bfe87bde8759_0";

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/data_sofartdk/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://data-sofartdk.opendata.arcgis.com/datasets/944f233ffc2049e79d19bfe87bde8759_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://data-sofartdk.opendata.arcgis.com/datasets/ffc374744b6d43bcb7ef7a9d07b37229_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://data-sofartdk.opendata.arcgis.com/datasets/86b43ad590b54fcdab9e574a65360d31_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("1-3 of 3 results"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    DCTerms.publisher,
                    new LiteralImpl(NodeFactory.createLiteral("sofart.dk"), null)
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/data_sofartdk/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("Skibsruter TSS"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.description,
                    new LiteralImpl(NodeFactory.createLiteral("Skibsruter og TSS Information på http://data.soefartsstyrelsen.dk"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.publisher,
                    new LiteralImpl(NodeFactory.createLiteral("sofart.dk"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://purl.org/dsnotify/vocab/eventset/sourceDataset"),
                    new ResourceImpl("http://map.dma.dk/arcgis/rest/services/Skibsruter_TSS/MapServer/0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://www.w3.org/ns/dcat#accessURL"),
                    new ResourceImpl("https://www.arcgis.com/home/item.html?id=944f233ffc2049e79d19bfe87bde8759")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.license,
                    new ResourceImpl("http://data-sofartdk.opendata.arcgis.com#")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.modified,
                    new LiteralImpl(NodeFactory.createLiteral("Updated: 3 years ago"), null)
                )
            )
        });
        return testConfigs;
    }

    public DataSofartdkTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
