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
public class OtvorenipodatociTest extends AbstractDataPortalTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("https://www.otvorenipodatoci.gov.mk/dataset/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha")),
            new File("src/test/resources/html_scraper_analyzer/otvorenipodatoci/otvorenipodatoci_detail.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/dataset/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha"),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Paid funds by measure for 2017"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/dataset/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha"),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Paid funds by measure of the Program for Financial Support of Agriculture for 2017"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/dataset/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha"),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("12-11-2018"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/dataset/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha"),
                    new PropertyImpl(DCTerms.creator.toString()),
                    new PropertyImpl("http://projekt-opal.de/agent/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/dataset/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha"),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new PropertyImpl("https://data.gov.mk/dataset/60870dab-a37b-4e3e-a9a1-fc77556a44e7/resource/e68043f2-aeb3-41a4-bd7d-a9bb577769ba/download/2.1.7-2017-.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://projekt-opal.de/agent/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha"),
                    new PropertyImpl("http://xmlns.com/foaf/0.1/mbox"),
                    new LiteralImpl(NodeFactory.createLiteral("katerina.darkoska@ipardpa.gov.mk"),null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://projekt-opal.de/agent/ncnjiatehn-cpedctba-no-mepkn-3a-2017-rodnha"),
                    new PropertyImpl("http://xmlns.com/foaf/0.1/name"),
                    new LiteralImpl(NodeFactory.createLiteral("Катерина Даркоска"), null)
                )
            )
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("https://www.otvorenipodatoci.gov.mk/en/dataset")),
            new File("src/test/resources/html_scraper_analyzer/otvorenipodatoci/otvorenipodatoci_resultpage.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/ttokapaktepnctnhhn-dejia-od-o6jiacta-ha-ekohomcknot-n-kpnmnhaji-od-2010-do-2107-rodnha")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/camoy6nctba-od-2010-do-2017-rodnha")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/pernctap-3a-mecehhn-n3bewtan")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/ttodatoun-3a-kopnchnun-ha-boda-od-pb-ctydehhnua-3a-nepnod-1992-2018-rodnha")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/n3dawhoct-ha-n3bopot-ctydehhnua-3a-2019-rodnha-ttpotok")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/n3dawhoct-ha-n3bopot-ctydehhnua-3a-2018-rodnha-bodoctoj")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/n3dawhoct-ha-n3bopot-ctydehhnua-3a-2018-rodnha-ttpotok")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/cymapeh-n3bewtaj-deua-no-tnn-ha-yctahoba-n-onwtnha")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/cymapeh-n3bewtaj-deua-no-kateropnja-ha-yctahoba")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/uehn-ha-3emjodejickn-npon3bodn")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/n3bewtaj-3a-cemejho-hacnjictbo")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/n3dawhoct-ha-n3bopot-ctydehhnua-3a-2019-rodnha-bodoctoj")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/cymapeh-nperjied-3a-dbnxehbe-ha-npedmetnte-3a-meceu-02-2019")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/pernctap-ha-otkynybahn-ha-3emjodejickn-npon3bodn")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/bpeme-ha-npouecnpahbe-ha-tpah3nthn-dokymehtn-ha-rpahnua")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/jabhn-orjiacn-3a-bpa6otybahbe-ha-admnhnctpatnbhn-cjiyx6ehnun-2019")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/nhtephn-orjiacn-3a-yhanpedybahbe-ha-admnhnctpatnbhn-cjiyx6ehnun-2019")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/o6pa6otehn-xaji6n-n-npnrobopn-ha-admnhnctpatnbhn-cjiyx6ehnun-2019")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/ncnntn-3a-admnhnctpatnbho-ynpabybahbe-2019")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset/cnpobedehn-aktnbhoctn-no-orjiacn-2019")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset?page=1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset?page=2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset?page=3")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk#")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.otvorenipodatoci.gov.mk/en/dataset?page=10")
                )
            )
        });
        return testConfigs;
    }

    public OtvorenipodatociTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
