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
public class DataNaerArcgis extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriDetailsPage = "http://data-naer.opendata.arcgis.com/datasets/e7e6b3e7c5704d48bbb221d95e0802be_0";
        String uriSearchPage = "http://data-naer.opendata.arcgis.com/datasets?";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/data_naer_arcgis/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://data-naer.opendata.arcgis.com/datasets/e7e6b3e7c5704d48bbb221d95e0802be_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://data-naer.opendata.arcgis.com/datasets/9f66cc9c7d6246d8857bfbef18edc3ff_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("1-2 of 2 results"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("tinkas"), null)
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/data_naer_arcgis/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("CHRsogn0015"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("tinkas"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://purl.org/dsnotify/vocab/eventset/sourceDataset"),
                    new ResourceImpl("https://services2.arcgis.com/DqYc4VbfKJbiqFCu/arcgis/rest/services/CHRsogn0015/FeatureServer/0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.accessURL.toString()),
                    new ResourceImpl("https://www.arcgis.com/sharing/rest/content/items/e7e6b3e7c5704d48bbb221d95e0802be/info/metadata/metadata.xml?format=default&output=html")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.license.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("License No license specified"), null)
                )
            )
        });
        return testConfigs;
    }

    public DataNaerArcgis(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
