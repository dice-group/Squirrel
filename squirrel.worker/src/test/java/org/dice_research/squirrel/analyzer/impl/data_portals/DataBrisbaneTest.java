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
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("https://www.data.brisbane.qld.gov.au/data/dataset/brisbane-parking-stations")),
            new File("src/test/resources/html_scraper_analyzer/databrisbane/databrisbane_detail.html"),
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
                    new ResourceImpl("http://creativecommons.org/licenses/by/4.0")
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
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/cemetery-locations")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/brisbane-city-plan-2014-qpp-codes")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/golf-course-locations")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-zoning-overlay")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-dwelling-house-character-overlay")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/wild-fire-history")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/planned-burns-history")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-biodiversity-areas-overlay-koala-habitat-areas")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-active-frontages-in-residential-zones-overlay"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-streetscape-hierarchy-overlay-corner-land-dedication"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-transport-air-quality-corridor-overlay-tunnel-ventilation-stack"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-road-hierarchy-overlay-primary-freight-route"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-wetlands-overlay"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-neighbourhood-plan-boundaries"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-road-hierarchy-overlay-road-hierarchy"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.brisbane.qld.gov.au/data/dataset/city-plan-2014-water-resource-catchments-overlay"))
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
