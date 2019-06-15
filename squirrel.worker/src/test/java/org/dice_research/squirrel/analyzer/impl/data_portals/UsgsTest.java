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
public class UsgsTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriIndexPage = "https://www.usgs.gov/products/data-and-tools/overview";
        String uriSearchPage = "https://www.usgs.gov/products/data-and-tools/science-datasets";
        String uriDetailsPage = "https://www.sciencebase.gov/catalog/item/5bb541c2e4b0fc368e877f33";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriIndexPage)),
            new File("src/test/resources/html_scraper_analyzer/usgs/index.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriIndexPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.usgs.gov/products/data-and-tools/science-datasets")
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/usgs/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://doi.org/10.5066/P9W4SF05")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dx.doi/10.5066/P9KKB3H2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://doi.org/10.5066/P9XDVRMT")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://doi.org/10.5066/F75B01RW")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://doi.org/10.5066/P9Z0SBKZ")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://doi.org/10.5066/P93R9UEL")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://doi.org/10.5066/P9B1VZNJ")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://dx.doi.org/10.5066/F79885XC")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://doi.org/10.5066/P9PMMSHX")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://doi.org/10.5066/F7SX6BPF")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.usgs.gov/products/data-and-tools/science-datasets?page=1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.usgs.gov/products/data-and-tools/science-datasets?page=4")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.usgs.gov/products/data-and-tools/science-datasets?page=573")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.usgs.gov/products/data-and-tools/science-datasets?page=3")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.usgs.gov/products/data-and-tools/science-datasets?page=2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.usgs.gov/products/data-and-tools/science-datasets?page=8")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.usgs.gov/products/data-and-tools/science-datasets?page=7")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.usgs.gov/products/data-and-tools/science-datasets?page=6")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.usgs.gov/products/data-and-tools/science-datasets?page=5")
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/usgs/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("Data release for the Land Change Causes for the United States Interior Highlands (2001 to 2006 and 2006 to 2011 time intervals)"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.description,
                    new LiteralImpl(NodeFactory.createLiteral("These data were created to describe the causes of land cover change that occurred in the Interior Highland region of the United States for the time intervals of 2001 to 2006 and 2006 to 2011. This region, which covers approximately 17.5 million hectares, includes portions of the U.S. states of Arkansas, Missouri, Oklahoma, and Kansas. Most of the area is covered by gently rolling hills of forests and pastureland. Two raster maps were created at a 30-meter resolution showing the causes of land change using automated and manual photo interpretation techniques. There were 30 categories of land change causes (i.e., forest harvest or surficial mining) discovered over the Interior Highlands. These categories can be used by researchers to summarize the historical patterns of land change for the region and to understand the impacts that these land change causes may have on the areas’ ecology, hydrology, wildlife, and climate."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.modified,
                    new LiteralImpl(NodeFactory.createLiteral("2019-03-13"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.issued,
                    new LiteralImpl(NodeFactory.createLiteral("2019-03-13"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_01to06_cause_data_release.aux"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_01to06_cause_data_release.tif.aux.xml"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_01to06_cause_data_release.tif.vat.cpg"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_06to11_cause_data_release.aux"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_01to06_cause_data_release.tif.vat.dbf"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_01to06_cause_data_release.tfw"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_06to11_cause_data_release.tif.vat.cpg"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_06to11_cause_data_release.tif.xml"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_06to11_cause_data_release.tif.aux.xml"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_01to06_cause_data_release.tif.xml"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_06to11_cause_data_release.tfw"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_06to11_cause_data_release.tif.vat.dbf"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_01to06_cause_data_release.tif.ovr"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_06to11_cause_data_release.tif.ovr"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_01to06_cause_data_release.tif"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("interior_highlands_06to11_cause_data_release.tif"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new LiteralImpl(NodeFactory.createLiteral("Int_Highlands_metadata_reconciliation_031219.xml"), null)
                )
            )
        });

        return testConfigs;
    }

    public UsgsTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
