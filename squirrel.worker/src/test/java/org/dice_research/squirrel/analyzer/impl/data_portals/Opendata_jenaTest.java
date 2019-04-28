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
public class Opendata_jenaTest extends AbstractDataPortalTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "https://opendata.jena.de/dataset";
        String uriDetailsPage = "https://opendata.jena.de/dataset/vornamen";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/opendata_jena/opendatajena_detail.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Vornamen"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Auflistung aller für die jeweiligen Jahre vergebenen Vornamen. Folgende Felder sind enthalten: Spalte 1: Vorname, Spalte 2: Anzahl, Spalte 3: Geschlecht (m=männlich,w=weiblich) Hinweise Bei Erst- und Folgevornamen wurde nur der erste Vorname gezählt. Vornamen, die zwei mal oder weniger als zwei Mal vergeben wurden, werden mit der Anzahl \"0\" gelistet. Die Dateien sind Unicode UTF-8 formatiert."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.creator.toString()),
                    new ResourceImpl("http://projekt-opal.de/agent/vornamen")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://projekt-opal.de/agent/vornamen"),
                    new PropertyImpl("http://xmlns.com/foaf/0.1/mbox"),
                    new ResourceImpl("https://opendata.jena.demailto:statistik@jena.de")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://projekt-opal.de/agent/vornamen"),
                    new PropertyImpl("http://xmlns.com/foaf/0.1/name"),
                    new LiteralImpl(NodeFactory.createLiteral("Melderegister Stadt Jena"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new PropertyImpl("https://opendata.jena.de/dataset/45cb0cf5-5b09-4b14-8f5b-bed05371fe93/resource/77b83a14-1087-4d0a-bdba-db254b71b674/download/2015_geburten_namensstatistik.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new PropertyImpl("https://opendata.jena.de/dataset/45cb0cf5-5b09-4b14-8f5b-bed05371fe93/resource/9837f9c5-f883-439a-b87b-c460aa757f6a/download/vornamen2018_opendata_anonymisiert.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new PropertyImpl("https://opendata.jena.de/dataset/45cb0cf5-5b09-4b14-8f5b-bed05371fe93/resource/7ca5d3ea-0a48-4bd3-b710-d23f6bc856b2/download/2016_geburten_namensstatistik.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new PropertyImpl("https://opendata.jena.de/dataset/45cb0cf5-5b09-4b14-8f5b-bed05371fe93/resource/5851cb71-c329-4816-9119-22eb487e0ea0/download/vornamen2017_opendata_jena_anonymisiert_utf8.csv")
                )
            )
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/opendata_jena/opendatajena_search.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/hundekottutenspender")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/einwohner-hauptwohnung-nach-alter-und-geschlecht")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/wohngebaude-und-wohungen")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/adressen-verortet")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/baumkataster")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/fahrradabstellanlagen")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/naturdenkmale")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/einwohner-gesamt")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/buromarktzonen")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/katasterflache")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/entwicklungsprojekte")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/einwohner-hauptwohnung-alterskohorten")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/offener-haushalt")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/statistische-privathaushalte")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/kraftfahrzeugbestand")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/wohnberechtigte-bevolkerung")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/entsorgungstermine-des-kommunalservice-jena")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/statische-ladepunkte-jena")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/mangel")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendata.jena.de/dataset/vornamen")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://opendata.jena.de/dataset?page=3")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://opendata.jena.de/dataset?page=4")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://opendata.jena.de/dataset?page=1"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://opendata.jena.de/dataset?page=2"))
            )
        });
        return testConfigs;
    }

    public Opendata_jenaTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}

