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
public class OpendataBordeauxTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriDetailsPage = "http://opendata.bordeaux.fr/content/adresses-gravees";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/opendata_bordeaux/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new LiteralImpl(NodeFactory.createLiteral("Les données"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Budget 2003 par nomenclature"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Description : Budget 2003 par nomenclature."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Propriétaire : Ville de Bordeaux"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("06/05/2015"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.license.toString()),
                    new ResourceImpl("http://opendata.bordeaux.fr/content/licence-ouverte")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.language.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Langue : Français"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.modified.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("06/05/2015"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://databordeaux.blob.core.windows.net/data/dref/budgetnomenc2003.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://databordeaux.blob.core.windows.net/data/doc/vbdx_notice.pdf")
                )
            )
        });
        return testConfigs;
    }

    public OpendataBordeauxTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
