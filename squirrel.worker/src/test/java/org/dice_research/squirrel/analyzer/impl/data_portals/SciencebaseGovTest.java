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
public class SciencebaseGovTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "https://www.sciencebase.gov/catalog/items?q=&filter0=browseCategory%3DData";
        String uriDetailsPage = "https://www.sciencebase.gov/catalog/item/542d8123e4b092f17defc662";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/sciencebase_gov/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4a71e4b07f02db6424da")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4aa8e4b07f02db6675bd")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e488be4b07f02db51c765")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4a59e4b07f02db62f8f2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4799e4b07f02db48fcde")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e477ae4b07f02db47f7b0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4a6fe4b07f02db640ed5")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4b09e4b07f02db69bdeb")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4adce4b07f02db68693a")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4813e4b07f02db4da961")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4884e4b07f02db51840f")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4aa8e4b07f02db6673be")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4783e4b07f02db4836a5")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4afbe4b07f02db696323")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e479ee4b07f02db492648")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4a70e4b07f02db6418f3")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/542d8123e4b092f17defc662")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/542d833ce4b092f17defc66b")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e479ae4b07f02db490054")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/item/4f4e4ac0e4b07f02db676d5a")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/items?q=&filter0=browseCategory%3DData&offset=20&max=20")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/items?q=&filter0=browseCategory%3DData&offset=80&max=20")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/items?q=&filter0=browseCategory%3DData&offset=40&max=20")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.sciencebase.gov/catalog/items?q=&filter0=browseCategory%3DData&offset=60&max=20")
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/sciencebase_gov/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Mean Minimum Winter Temperature (deg. C) for Northeast, Projected for 2060, RCP4.5, Ensemble GCM Results"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("To evaluate the potential effects of climate change on wildlife habitat and ecological integrity in the northeastern United States from 2010 to 2080, a University of Massachusetts Amherst team derived a set of climate projections at a fine spatial resolution for the entire Northeast. The projections are based upon publicly available climate models.This dataset represents the mean of the minimum air temperature (degrees C) for December, January, and February using one of two IPCC greenhouse gas concentration scenarios (RCP4.5). The dataset is intended to represent typical winter temperatures in the decade centered on 2060 rather than the actual temperatures during 2060. MAP UNITS ARE TEMP. IN DEGREES C MULTIPLIED BY 100 (which allows for more efficient data storage). Detailed documentation for all of the UMass climate datasets is available from: http://jamba.provost.ads.umass.edu/web/lcc/DSL_documentation_climate.pdf . The climate work is part of the Designing Sustainable Landscapes project led by Professor Kevin McGarigal of UMass Amherst and sponsored by the North Atlantic Landscape Conservation Cooperative; for more information about the entire project see: http://www.umass.edu/landeco/research/dsl/dsl.html The dataset was derived from the following sources: - An average or ensemble of results from 14 Atmospheric-Ocean Circulation Models (AOGCMs) publicly available from the World Climate Research Programme's (WCRP) Coupled Model Intercomparison Project phase 5 (CMIP5). These complex models produce long-term climate projections by integrating oceanic and atmospheric processes. The results have been downscaled (projected to a finer resolution) using the Bias Corrected Spatial Disaggregation (BCSD) approach. Results were developed for the two scenarios of greenhouse gas concentrations (RCP4.5 and RCP8.5) that were available for every CMIP5 AOGCM; this dataset is based on RCP4.5. Output are at a resolution of approximately 12 km resolution. - The results were further refined to approximately 600 m resolution by reference to the Parameter-elevation Relationships on Independent Slopes Model (PRISM) dataset, This model takes into account elevation, aspect, proximity to the coast, and other factors to predict climate based on results from 10,000 weather stations. Thirty year average data for 1981-2010 (i.e., centered on 1995) were used. This dataset is one of multiple climate datasets consisting of: - Projections for the years 2010, 2020, 2030, 2040, 2050, 2060, 2070, and 2080 - Projections for both the RCP4.5 and RCP8.5 greenhouse gas concentration scenarios - The following datasets expected to have important effects in determining the occurrence and survival of fish, wildlife, and plant populations: 1) Total annual precipitation 2) Precipitation during the growing season (May-Sept.) 3) Average annual temperature 4) Mean minimum winter temperature 5) Mean maximum summer temperature 6) Mean July temperature 7) Growing degree days (number of days in which the average temperature is >10 degrees C) 8) Heat index 30 (number of days in which the maximum temperature is >30 degrees C) 9) Heat index 35 (number of days in which the maximum temperature is >35 degrees C)"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.modified.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("2013-09-10"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("2013-09-10"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("tmin60_45_md.xml"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("tmin60_45.zip"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("tmin60_45.sd"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("thumbnail.png"), null)
                )
            )
        });

        return testConfigs;
    }

    public SciencebaseGovTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
