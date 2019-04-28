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
public class Datagov_skTest extends AbstractDataPortalTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("https://data.gov.sk/dataset/register-adries-register-budov")),
            new File("src/test/resources/html_scraper_analyzer/datagovsk/datagovsk_detail.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-budov"),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Register Adries - Register budov (súpisných čísiel)"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-budov"),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Obsahuje informácie o budovách (súpisných číslach) v SR."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-budov"),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new PropertyImpl("http://projekt-opal.de/agent/register-adries-register-budov")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://projekt-opal.de/agent/register-adries-register-budov"),
                    new PropertyImpl("http://xmlns.com/foaf/0.1/homepage"),
                    new PropertyImpl("https://data.gov.sk/en/organization/2DF13D50-0B6F-48BA-884E-BE66DC0A2934")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://projekt-opal.de/agent/register-adries-register-budov"),
                    new PropertyImpl("http://xmlns.com/foaf/0.1/name"),
                    new LiteralImpl(NodeFactory.createLiteral("Ministerstvo vnútra SR"), null)
                )
            )
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("https://data.gov.sk/en/dataset")),
            new File("src/test/resources/html_scraper_analyzer/datagovsk/datagovsk_search.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-budov")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-krajov")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-casti-obci")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-vchodov")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-obci")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-ulic")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-okresov")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-register-bytov")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/register-adries-ra-zmenove-davky")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/zoznam-datasetov-mo-sr")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/vestnik-verejneho-obstaravania-20192")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/objednavky-rok-2018")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/data-o-kontrolach")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/zoznam-schvalenych-zo-nfp-opii")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/vyvoj-certifikovaneho-cerpania-v-ramci-opii")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/zoznam-narodnych-projektov-opii")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/phm")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/faktury-na-zverejnenie")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/konsolidacia-uctovnej-uzavierky"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://data.gov.sk/dataset/zoznam-elektronickych-sluzieb-a-formularov-na-upvs"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://data.gov.sk/dataset?page=1"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://data.gov.sk/dataset?page=2"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://data.gov.sk/dataset?page=3"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://data.gov.sk#"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://data.gov.sk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://data.gov.sk/dataset?page=92"))
            )
        });
        return testConfigs;
    }

    public Datagov_skTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
