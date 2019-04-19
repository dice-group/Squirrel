package org.dice_research.squirrel.analyzer.impl.data_portals;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.*;
import org.apache.jena.tdb.assembler.Vocab;
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
public class OsniSpatialTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchResultPage = "http://osni-spatial-ni.opendata.arcgis.com/datasets?sort_by=relevance";
        String uriDetailsPage = "http://osni-spatial-ni.opendata.arcgis.com/datasets/1cdb3f26958046799f84a3de58dcc349_6";
        testConfigs.add(new Object[] {
            new CrawleableUri(new URI(uriSearchResultPage)),
            new File("src/test/resources/html_scraper_analyzer/osni_spatial/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets/d9dfdaf77847401e81efc9471dcd09e1_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets/a55726475f1b460c927d1816ffde6c72_2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets/1cdb3f26958046799f84a3de58dcc349_6")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets/ae71b79359634fbaaad18a6826710ed5_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets/de0116be5fe5499f921c112568218a39_1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets/55cd419b2d2144de9565c9b8f73a226d_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets/4db1ea247b4e48ad8b55c3014f062f34_0")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendatani.blob.core.windows.net/lpsopendata/OSNI_OPEN_DATA_Midscale_Raster.zip")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendatani.blob.core.windows.net/lpsopendata/10M-DTM-Sheets251-293.zip")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://opendatani.blob.core.windows.net/lpsopendata/10M-DTM-Sheets101-150.zip")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=8")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=9")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=3")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=4")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=5")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=6")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets?q=*&sort_by=updated_at&page=7")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://osni-spatial-ni.opendata.arcgis.com/datasets?q=*&sort_by=updated_at")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("SpatialNI_Admin"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchResultPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("1-10 of 83 results"), null)
                )
            )
        });

        testConfigs.add(new Object[] {
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/osni_spatial/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("OSNI Open Data 50k Admin Boundaries - Townlands"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Mid scale vector polygon dataset showing townland boundaries. Published here for OpenData. By download or use of this dataset you agree to abide by the Open Government Data Licence."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("SpatialNI_Admin"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://purl.org/dsnotify/vocab/eventset/sourceDataset"),
                    new ResourceImpl("https://gisservices.spatialni.gov.uk/arcgisc/rest/services/OpenData/OSNIOpenData_50KBoundaries/MapServer/6")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.accessURL.toString()),
                    new ResourceImpl("https://www.arcgis.com/home/item.html?id=1cdb3f26958046799f84a3de58dcc349")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.license.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("License Open Government Licence applies Land & Property Services (LPS) has made a number of Ordnance Survey of Northern Ireland® (OSNI®) branded datasets available free of charge under the terms of the current Open Government Licence (OGL). These datasets – which include raster and vector mapping, boundary, gazetteer, height, street and townland products – are available for download. Each dataset is clearly marked with the OGL symbol shown belowThe OGL allows you to: copy, distribute and transmit the data; adapt the data; and exploit the data commercially, whether by sub-licensing it, combining it with other data, or including it in your own product or application. You are therefore able to use the LPS datasets in any way and for any purpose. We simply ask that you acknowledge the copyright and the source of the data by including the following attribution statement:Contains public sector information licensed under the terms of the Open Government Licence v3.0.You must also: include the same acknowledgement requirement in any sub-licences of the data that you grant, and a requirement that any further sub-licences do the same; ensure that you do not use the data in a way that suggests LPS endorses you or your use of the data; and make sure you do not misrepresent the data or its source. N.B. Any dataset that does not expressly state that it has been released under OGL will require a licence from LPS and the appropriate licence fee will be applied."), null)
                )
            )
        });
        return testConfigs;
    }

    public OsniSpatialTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
