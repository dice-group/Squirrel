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
public class AbertosXunta extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriDetailsPage = "http://abertos.xunta.gal/catalogo/cultura-ocio-deporte/-/dataset/0400/calendario-2019-publicacion-estatisticas-ige";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/abertos_xunta/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://abertos.xunta.gal/busca-de-datos")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://abertos.xunta.gal#")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Calendario 2019 de publicación de estatísticas do IGE"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Calendario de 2019 dos días nos que se publican as estatísticas conxunturais , de periodicidade mensual e trimestral, do Instituto Galego de Estatística. A información que proporciona inclúe a data de referencia na que estarán dispoñibles os resultados. As actividades estruturais, de periodicidade igual ou superior ao ano, móstranse debaixo do calendario de cada mes e difúndense na páxina web do IGE ao longo dese período. As estatísticas cuxa referencia se contempla no calendario, xunto coas siglas polas que se recoñecen, son: EC - Estatística de construción de edificios AfiSS - Explotación das afiliacións á Seguridade Social IPC - Índice de prezos ao consumo CEXT - Comercio exterior e intracomunitario IVU - Índices de valor unitario para o comercio exterior EPA - Enquisa de poboación activa EPAx - Enquisa de poboación activa. Estudo sobre a relación coa actividade da poboación xuvenil IASS - Indicadores de actividade e de VEB do sector servizos IPRI - Índice de prezos industriais ECF - Enquisa conxuntural a fogares BORME - Explotación do Boletín Oficial do Rexistro Mercantil IC - Índices de competitividade EPAF - Estatística de fluxos da poboación activa AfiSSC - Afiliacións á Seguridade Social por concello de residencia do/a afiliado/a IPI - Índice de produción industrial IVCM - Índice de vendas de comercio polo miúdo O calendario está dispoñible en formato .ics (estándar iCalendar que lle permite á persoa usuaria subscribirse aos datos de forma que poida aplicalos a un calendario do seu ordenador, dispositivo móbil...)."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("08-01-2019"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://purl.org/dsnotify/vocab/eventset/sourceDataset"),
                    new ResourceImpl("http://www.ige.eu/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.license.toString()),
                    new ResourceImpl("http://creativecommons.org/licenses/by/3.0/es/deed.gl")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.modified.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("08-01-2019"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://abertos.xunta.gal/catalogo/cultura-ocio-deporte/-/dataset/0400/calendario-2019-publicacion-estatisticas-ige.rdf")
                )
            )
        });
        return testConfigs;
    }

    public AbertosXunta(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
