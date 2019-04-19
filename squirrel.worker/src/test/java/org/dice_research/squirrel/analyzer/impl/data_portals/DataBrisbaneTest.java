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
public class DataBrisbaneTest extends AbstractDataPortalTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
/*        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("")),
            new File("src/test/resources/"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("data.brisbane.qld.gov.au"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new PropertyImpl("https://www.data.brisbane.qld.gov.au/data/dataset")
                )
            )
        });*/
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("https://www.data.brisbane.qld.gov.au/data/dataset/brisbane-parking-stations")),
            new File("src/test/resources/databrisbane/databrisbane_detail.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/brisbane-parking-stations"),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Parking — Stations"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/brisbane-parking-stations"),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Number of vacant spaces at Brisbane City Council’s 2 parking stations. Brisbane City Council operates two car parks in the Brisbane Central Business District (CBD) at King George Square and Wickham Terrace. Both car parks operate with extended hours throughout the year and are open on most public holidays and during some special events. See the Brisbane City Council website for information on the car parks including location, opening hours, fees and to see if there are any special offers currently available."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/brisbane-parking-stations"),
                    new PropertyImpl(DCTerms.license.toString()),
                    new PropertyImpl("http://creativecommons.org/licenses/by/4.0")
                )
            )
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("https://www.data.brisbane.qld.gov.au/data/dataset")),
            new File("src/test/resources/html_scraper_analyzer/databrisbane/databrisbane_search.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/brisbane-parking-stations")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/traffic-data-at-intersection-api")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/flood-study-citywide-overland-flow-pullen-pullen-sub-model")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/flood-study-coastal")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/flood-study-citywide-overland-flow-lower-bulimba-sub-model")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-regional-infrastructure-corridors-and-substations-overlay-major-transport-infras")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-streetscape-hierarchy-overlay-streetscape-hierarchy")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/stormwater-end-structures-existing")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/planned-temporary-road-occupancies")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/traffic-signal-location-reference")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/flood-study-citywide-overland-flow-enoggera-sub-model")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-regional-infrastructure-corridors-and-substations-overlay-high-voltage-powerline")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-regional-infrastructure-corridors-and-substations-overlay-high-voltage-easements"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-regional-infrastructure-corridors-and-substations-overlay-petroleum-pipelines"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/stormwater-quality-improvement-device-existing"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/stormwater-pipe-existing"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/stormwater-gully-existing"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/stormwater-junction-existing"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset?q=&page=1"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset?q=&page=2"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset?q=&page=3"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au#"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset?q=&page=9"))
            )
        });
        return testConfigs;
    }

    public DataBrisbaneTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
