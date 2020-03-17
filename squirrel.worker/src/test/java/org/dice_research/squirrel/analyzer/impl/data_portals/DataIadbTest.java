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
public class DataIadbTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "https://data.iadb.org/DataCatalog/Dataset";
        String uriDetailsPage = "https://data.iadb.org/DataCatalog/Dataset#DataCatalogID=75uh-zme7";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/data_iadb/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.iadb.org#DataCatalogID=fmzp-pctv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.iadb.org#DataCatalogID=75uh-zme7")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.iadb.org#DataCatalogID=rq4a-d9gg")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.iadb.org#DataCatalogID=hg7w-u675")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.iadb.org#DataCatalogID=u7bw-exzv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.iadb.org#DataCatalogID=2ffa-hhe4")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.iadb.org#DataCatalogID=v2c9-36h7")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.iadb.org#DataCatalogID=2dqw-u35p")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.iadb.org#DataCatalogID=11319/9095")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.iadb.org#DataCatalogID=11319/9069")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://data.iadb.org#")
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/data_iadb/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.title,
                    new ResourceImpl("https://data.iadb.org")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.description,
                    new LiteralImpl(NodeFactory.createLiteral("This dataset contains information on 404 Centros infantiles del Buen Vivir (CIBVs), Ecuador's primary providers of public child care services for infant and toddlers at the time of data collection in 2012. In 2012, The Inter-American Development Bank administered a battery of widely-used instruments to measure the quality of child care services in the CIBVs, including four internationally-recognized quality instruments that were then adapted for use in Ecuador."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.description,
                    new LiteralImpl(NodeFactory.createLiteral(""), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.publisher,
                    new LiteralImpl(NodeFactory.createLiteral("Protección social y salud"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.license,
                    new ResourceImpl("https://creativecommons.org/licenses/by-nc-nd/3.0/igo/legalcode")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.modified,
                    new LiteralImpl(NodeFactory.createLiteral("Jan 15, 2019"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.issued,
                    new LiteralImpl(NodeFactory.createLiteral("Jan 15, 2019"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://mydata.iadb.org/resource/fmzp-pctv.CSV")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://data.iadb.orgjavascript:download('https://mydata.iadb.org/api/views/fmzp-pctv/rows.JSON?accessType=DOWNLOAD','fmzp-pctv.JSON');")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://mydata.iadb.org/resource/fmzp-pctv.RDF")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://data.iadb.orgjavascript:download('https://mydata.iadb.org/api/views/fmzp-pctv/rows.RSS?accessType=DOWNLOAD','fmzp-pctv.RSS');")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://data.iadb.orgjavascript:download('https://mydata.iadb.org/api/views/fmzp-pctv/rows.XML?accessType=DOWNLOAD','fmzp-pctv.XML');")
                )
            )
        });
        return testConfigs;
    }

    public DataIadbTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
