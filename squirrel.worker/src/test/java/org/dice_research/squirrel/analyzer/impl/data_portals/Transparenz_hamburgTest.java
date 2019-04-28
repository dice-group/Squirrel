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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class Transparenz_hamburgTest extends AbstractDataPortalTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "http://suche.transparenz.hamburg.de/";
        String uriDetailsPage = "http://suche.transparenz.hamburg.de/dataset/100-jahre-stadtgrun-stadtpark-und-volkspark11";

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/transparenz_hamburg/transparenzhamburg_detail.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("100 Jahre Stadtgrün - Stadtpark und Volkspark"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Anlässlich des 100. Geburtstags vom Hamburger Stadtpark und Altonaer Volkspark werden für beide Parks Informationen zu Service-Einrichtungen und Sehenswürdigkeiten im Park dargeboten. Dazu zählen zum einen die Standorte von Restaurants, Minigolfanlagen, StadtRAD-Stationen, Grillwiese, öffentliche Toilette etc., zum anderen Standorte von Sehenswürdigkeiten und Kunstskulpturen. Hinweis: Im Datensatz verlinkte Fotos unterliegen nicht der Veröffentlichungspflicht nach Hamburgischem Transparenzgesetz und sind nicht Teil der freien Lizenz. Weitere Informationen: www.hamburg.de/parkanlagen"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Behörde für Umwelt und Energie (BUE), Präsidialabteilung (BUE), P 3 - Öffentlichkeitsarbeit und Kommunikation, P 32 - Presse und Online-Kommunikation"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("19.02.2019"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://purl.org/dc/terms/license"),
                    new PropertyImpl("https://www.govdata.de/dl-de/by-2-0")
                )
            )
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/transparenz_hamburg/transparenzhamburg_search.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/abbruch-bestandsbalkone-errichtung-neu-vorgestellter-balkone-austausch-der-fenster-und-erweiter1?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/abbruch-des-gesamten-gebaeudebestandes-fuer-neue-wohnbebauung?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/aenderung-tiefgarage-und-kellergeschoss?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/anbau-an-ein-fernkaeltewerk2?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/aufstockung-eines-bestehenden-buerogebaeudes-um-zwei-vollgeschosse-und-technik-im-bereich-kalkh1?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/ausbau-zweier-dachgeschosswohnungen-verlegung-des-muellstandortes-in-den-vorgarten1?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/beherbergung-im-reiseverkehr-in-hamburg-dezember-2018-vorlaeufige-ergebnisse?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-eimsbuettel-si-2019-365-sitzung-des-hauptausschusses-vom-17-01-2019?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-hamburg-mitte-drucksache-21-5082?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-hamburg-mitte-si-2019-461-sitzung-des-kulturausschusses-vom-17-01-2019?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-wandsbek-drucksache-20-6749-1?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-wandsbek-drucksache-20-6943-1?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-wandsbek-drucksache-20-6958-1?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-wandsbek-drucksache-20-6980-1?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-wandsbek-drucksache-20-6987?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-wandsbek-drucksache-20-7014?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-wandsbek-drucksache-20-7015?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-wandsbek-drucksache-20-7019?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-wandsbek-drucksache-20-7020?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset/bezirk-wandsbek-drucksache-20-7022?forceWeb=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset?page=1"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset?page=2"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset?page=3"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de#"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://suche.transparenz.hamburg.de/dataset?page=4719"))
            )
        });
        return testConfigs;
    }

    public Transparenz_hamburgTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
