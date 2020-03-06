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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class Dados_fortalezaTest extends AbstractDataPortalTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "http://dados.fortaleza.ce.gov.br/catalogo/dataset";
        String uriDetailsPage = "http://dados.fortaleza.ce.gov.br/catalogo/dataset/http-www-fortaleza-ce-gov-br-sites-default-files-rede-de-atencao-e-cuidados-de-fortaleza-pdf";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/dados_fortaleza/dados_fortaleza_detail.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("Rede de Atenção e Cuidado de Fortaleza"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.description,
                    new LiteralImpl(NodeFactory.createLiteral("A Rede de Atenção e Cuidado de Fortaleza, no que diz respeito à temática Drogas e outras substâncias psicoativas, compreende diversas instituições sendo ONG’s, Organizações Governamentais e do Terceiro Setor assim distribuídas: Unidades de Saúde, Residências terapêuticas, Farmácias Polo, Conveniadas e Popular, OCAS, Grupos de Ajuda Mútua (A.A, N.A., AL-ANON, NAR-ANON), Serviços especializados para a população em situação de rua, Hospitais, Unidades de saúde, SAMU, Leitos para desintoxicação, CAPS, CREAS e CRAS."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.creator,
                    new ResourceImpl("http://projekt-opal.de/agent/http-www-fortaleza-ce-gov-br-sites-default-files-rede-de-atencao-e-cuidados-de-fortaleza-pdf")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://projekt-opal.de/agent/http-www-fortaleza-ce-gov-br-sites-default-files-rede-de-atencao-e-cuidados-de-fortaleza-pdf"),
                    new PropertyImpl("http://xmlns.com/foaf/0.1/mbox"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.brmailto:renata.pinheiro@fortaleza.ce.gov.br")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://projekt-opal.de/agent/http-www-fortaleza-ce-gov-br-sites-default-files-rede-de-atencao-e-cuidados-de-fortaleza-pdf"),
                    new PropertyImpl("http://xmlns.com/foaf/0.1/name"),
                    new LiteralImpl(NodeFactory.createLiteral("Renata Pinheiro"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new PropertyImpl("http://dados.fortaleza.ce.gov.br/dataset/f5db028c-002c-4f3d-96b0-ee835a79bcfa/resource/611c97d1-f599-4d7b-9a5f-47d45b287597/download/rededeatencaoecuidadosdefortaleza.pdf")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new PropertyImpl("http://dados.fortaleza.ce.gov.br/dataset/f5db028c-002c-4f3d-96b0-ee835a79bcfa/resource/3de5388b-b695-4187-8215-341db81d0b2e/download/rededeatencaoecuidadosdefortaleza.pdf")
                )
            )
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/dados_fortaleza/dados_fortaleza_search.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/chamadas-da-policia")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/dados-de-onibus-11-03-2015")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/realizacoes-da-prefeitura-municipal-entre-2012-2016")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/areas-verdes-da-prefeitura-municipal-de-fortaleza")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/zepo")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/zeph")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/zedus")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/zei")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/zea")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/limite-bairros")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/inventario-patrimonial")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/territorios-da-cidadania")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/bairros-regionais")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/bens-tombados")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/limite-regional")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/limite-municipal-de-fortaleza")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/limite-de-bairros-com-as-informacoes-para-os-mapas")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/dados-e-acoes-da-coordenadoria-da-juventude-ref-2015")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/http-www-fortaleza-ce-gov-br-sites-default-files-rede-de-atencao-e-cuidados-de-fortaleza-pdf")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset/paradas-de-onibus-ref-03-2015")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset?q=&sort=score+desc%2C+metadata_modified+desc&page=1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset?q=&sort=score+desc%2C+metadata_modified+desc&page=2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset?q=&sort=score+desc%2C+metadata_modified+desc&page=3"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br#"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://dados.fortaleza.ce.gov.br/dataset?q=&sort=score+desc%2C+metadata_modified+desc&page=15"))
            )
        });
        return testConfigs;
    }

    public Dados_fortalezaTest (CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
