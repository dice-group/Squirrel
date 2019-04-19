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
public class PrototypeGismontgomeryTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object []> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchResultPage = "http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=";
        String uriDetailsPage = "http://prototype-gismontgomery.opendata.arcgis.com/datasets/3f2dc8774b934b17b1c8bf9c2d05d45b_0";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchResultPage)),
            new File("src/test/resources/html_scraper_analyzer/prototype_gismontgomery/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/dfe2681e23de458080cc8adf4a4be899_3")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/dfe2681e23de458080cc8adf4a4be899_5")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/dfe2681e23de458080cc8adf4a4be899_4")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/dfe2681e23de458080cc8adf4a4be899_2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/8db37551165b425cb1f14ae4518e54db_1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/69acca3a6bd54eab9972adcb8713448e_2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/8db37551165b425cb1f14ae4518e54db_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/83061a1dc74c4f419d48de65ab227bdc_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/7adbc831d27b49578c1638f043bda7aa_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/1959435a5a81409992b45cc976ac6d1b_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("1-10 of 212 results"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("jay.mukherjee"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("melissa.noakes"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("RockvilleMD"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("on September 13, 2013"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("on May 07, 2018"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("on April 09, 2014"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=3")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=4")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=5")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=6")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=7")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=8")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=9")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=10")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=2")
                )
            )
        });

        testConfigs.add(new Object[] {
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/prototype_gismontgomery/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Full Address"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Full Addresses for a given location"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("jay.mukherjee"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("on March 26, 2014"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://purl.org/dsnotify/vocab/eventset/sourceDataset"),
                    new ResourceImpl("https://mcatlas.org/arcgis3/rest/services/overlays/Address_Labels/MapServer/0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.accessURL.toString()),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.license.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("License No license specified"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/3f2dc8774b934b17b1c8bf9c2d05d45b_0.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/3f2dc8774b934b17b1c8bf9c2d05d45b_0.kml")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://prototype-gismontgomery.opendata.arcgis.com/datasets/3f2dc8774b934b17b1c8bf9c2d05d45b_0.zip")
                )
            )
        });
        return testConfigs;
    }

    public PrototypeGismontgomeryTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
