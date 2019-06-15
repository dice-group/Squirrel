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
public class IllinoisAirportsTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "http://illinoisairports-cmtengr.opendata.arcgis.com/datasets?";
        String uriDetailsPage = "http://illinoisairports-cmtengr.opendata.arcgis.com/datasets/b755db2c2ba0454ca4e334760359f6a9_1";

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/illinois_airports/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://illinoisairports-cmtengr.opendata.arcgis.com/datasets/5cf21901a65c4ba69acf1219df5ab27c_1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://illinoisairports-cmtengr.opendata.arcgis.com/datasets/b755db2c2ba0454ca4e334760359f6a9_1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://illinoisairports-cmtengr.opendata.arcgis.com/datasets/5cf21901a65c4ba69acf1219df5ab27c_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://illinoisairports-cmtengr.opendata.arcgis.com/datasets/b755db2c2ba0454ca4e334760359f6a9_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.arcgis.com/home/webmap/viewer.html?webmap=215cac6989e742a588aa4d9bc8289b2c")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.arcgis.com/home/webmap/viewer.html?webmap=c24d61dc4e9f4ec7aa9243a4a4737c59")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.arcgis.com/home/webmap/viewer.html?webmap=db4740a131c94f34ae721b46a18a8cba")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("1-7 of 7 results"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    DCTerms.publisher,
                    new LiteralImpl(NodeFactory.createLiteral("Boyd"), null)
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/illinois_airports/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("TREE"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.publisher,
                    new LiteralImpl(NodeFactory.createLiteral("Boyd"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.modified,
                    new LiteralImpl(NodeFactory.createLiteral("Updated: 2 months ago"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://purl.org/dsnotify/vocab/eventset/sourceDataset"),
                    new ResourceImpl("http://54.163.215.232:6080/arcgis/rest/services/Misc_Services/SPITTEST/MapServer/1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://www.w3.org/ns/dcat#accessURL"),
                    new ResourceImpl("https://www.arcgis.com/home/item.html?id=5cf21901a65c4ba69acf1219df5ab27c")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://dbpedia.org/ontology/dateLastUpdated"),
                    new LiteralImpl(NodeFactory.createLiteral("Updated: 2 months ago"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.license,
                    new ResourceImpl("http://illinoisairports-cmtengr.opendata.arcgis.com#")
                )
            )
        });
        return testConfigs;
    }

    public IllinoisAirportsTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
