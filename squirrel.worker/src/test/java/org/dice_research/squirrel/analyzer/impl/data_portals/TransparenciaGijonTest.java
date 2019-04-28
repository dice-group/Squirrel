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
public class TransparenciaGijonTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriDetailsPage = "http://transparencia.gijon.es/risp_datasets/show/albergues";

        testConfigs.add(new Object[] {
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/transparencia_gijon/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://transparencia.gijon.es/page/1808-catalogo-de-datos")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Relación de subvenciones fachadas y ascensores"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Portales"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Descripción Disposiciones de Gasto del económico 78009"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new ResourceImpl("http://transparencia.gijon.es/og_categories/show/113-ayuntamiento-de-gijon")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Viernes, 4 de Enero de 2019"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://purl.org/dsnotify/vocab/eventset/sourceDataset"),
                    new LiteralImpl(NodeFactory.createLiteral("URL del dataset http://opendata.gijon.es/descargar.php?id=723"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.license.toString()),
                    new ResourceImpl("http://transparencia.gijon.es/og_categories/show/112-creative-commons-reconocimiento-3-0-espana")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.modified.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Viernes, 4 de Enero de 2019"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://opendata.gijon.es/descargar.php?id=723&tipo=CSV")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://opendata.gijon.es/descargar.php?id=723&tipo=EXCEL")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://opendata.gijon.es/descargar.php?id=723&tipo=JSON")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://opendata.gijon.es/descargar.php?id=723&tipo=PDF")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://opendata.gijon.es/descargar.php?id=723&tipo=XHTML")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://opendata.gijon.es/descargar.php?id=723&tipo=XML")
                )
            )
        });
        return testConfigs;
    }

    public TransparenciaGijonTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
