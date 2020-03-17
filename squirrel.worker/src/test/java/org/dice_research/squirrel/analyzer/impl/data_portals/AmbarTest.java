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
public class AmbarTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "http://ambar.utpl.edu.ec/dataset";
        String uriDetailsPage = "http://ambar.utpl.edu.ec/dataset/feminicidios-latam";
        testConfigs.add(new Object[] {
            new CrawleableUri(new URI("http://ambar.utpl.edu.ec")),
            new File("src/test/resources/html_scraper_analyzer/ambar/index.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("http://ambar.utpl.edu.ec"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset")
                )
            ),
        });
        testConfigs.add(new Object[] {
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/ambar/details_page.html"),
            ModelFactory.createDefaultModel().add(
              new StatementImpl(
                  new ResourceImpl(uriDetailsPage),
                  new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                  new PropertyImpl("http://ambar.utpl.edu.ec/dataset")
              )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("feminicidios-LatAm"), null)
              )
            ).add(
            new StatementImpl(
                new ResourceImpl(uriDetailsPage),
                DCTerms.description,
                new LiteralImpl(NodeFactory.createLiteral("A medida que las tecnologías de la información y la comunicación incrementan su impacto social, más académicos se interesan en vincular la tecnología con las necesidades reales de la sociedad. En este documento se presenta el desarrollo de una iniciativa de monitoreo de feminicidios en América Latina, que se ha desarrollado con la colaboración de estudiantes de ingeniería en sistemas quienes a través de la metodología de aprendizaje mediante retos lograron desarrollar prototipos de visualizaciones cívicas para dar a conocer al mundo esta problemática y como aporte en datos abiertos al problema latente en nuestra región."), null)
              )
            ).add(
            new StatementImpl(
                new ResourceImpl(uriDetailsPage),
                DCTerms.publisher,
                new LiteralImpl(NodeFactory.createLiteral("Ambar"), null)
              )
            ).add(
            new StatementImpl(
                new ResourceImpl(uriDetailsPage),
                DCTerms.license,
                new PropertyImpl("http://www.opendefinition.org/licenses/cc-by")
              )
            ).add(
            new StatementImpl(
                new ResourceImpl(uriDetailsPage),
                DCTerms.modified,
                new LiteralImpl(NodeFactory.createLiteral("October 29, 2018, 16:33"), null)
              )
            ).add(
            new StatementImpl(
                new ResourceImpl(uriDetailsPage),
                DCTerms.issued,
                new LiteralImpl(NodeFactory.createLiteral("October 29, 2018, 16:24"), null)
              )
            ).add(
            new StatementImpl(
                new ResourceImpl(uriDetailsPage),
                DCAT.downloadURL,
                new PropertyImpl("http://ambar.utpl.edu.ec/dataset/5e16693b-b693-4e87-9948-0d2a3d82fc81/resource/8e3c2eec-83ca-4a2b-af92-e8e06f8c87e0/download/datafeminicidiosmundo.xlsx")
              )
            )
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/ambar/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/feminicidios-latam")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/knowledge-graph-about-historical-figures-of-ecuador")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/problematicas-y-conflictos-socioambientales")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/national-health-and-nutrition-examination-survey-nhanes")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/nutrition-physical-activity-and-obesity-behavioral-risk-factor-surveillance-system")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/nutricion-ninez")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/tasa-de-mortalidad-infantil")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/education-statistics-world-bank")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/educacion-de-calidad")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/datos-encuestas-sobre-percepcion-de-audiencias-de-tv-local-en-ecuador")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/fallos-de-casacion-1994-2004")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/marcas-de-preguntas-matematica")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/web-gad-ec")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/estados-financieros-de-las-companias-en-el-ecuador-en-2016")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/organismo-de-ciencia-y-tecnologia-de-iberoamerica")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/consumo-de-agua-ciudad-victoria")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/datos-de-publicaciones-de-la-cuenta-municipal-mi-puyango-en-facebook")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/caracteres-problemas")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/crecimiento-sustentable-van-horne-2017")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset/catalogo-de-los-recursos-gastronomicos-mancomunidad-bosque-seco")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset?page=1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset?page=2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset?page=3")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://ambar.utpl.edu.ec/dataset?page=28")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://ambar.utpl.edu.ec#")
                )
            )
        });
        return testConfigs;
    }

    public AmbarTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
