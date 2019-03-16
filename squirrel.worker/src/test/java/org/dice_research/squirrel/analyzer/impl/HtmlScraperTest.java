package org.dice_research.squirrel.analyzer.impl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.analyzer.impl.html.scraper.HtmlScraper;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

// Ignore for the release
@Ignore
public class HtmlScraperTest {

    private File fetchedFile;
    private HtmlScraper scraper;

    @Before
    public void prepareGeneral() {
        File configurationFile = new File("src/test/resources/html_scraper_analyzer/yaml");
        scraper = new HtmlScraper(configurationFile);
    }

    @Test
    public void scrapeSearchResultPageGisMont() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/3f2dc8774b934b17b1c8bf9c2d05d45b_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/3f2dc8774b934b17b1c8bf9c2d05d45b_1")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/1959435a5a81409992b45cc976ac6d1b_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/d3ba2027c4c8422c83282459c8c29c14_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/7fab007eab034c45a53d858859eec341_17")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/7fab007eab034c45a53d858859eec341_39")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/7fab007eab034c45a53d858859eec341_28")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/7fab007eab034c45a53d858859eec341_15")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/7fab007eab034c45a53d858859eec341_2")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/7fab007eab034c45a53d858859eec341_6")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("\"Next\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"1-10 of 215 results\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"jay.mukherjee\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"melissa.noakes\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"cgmcgove\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"on March 26, 2014\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"on May 07, 2018\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"on June 18, 2014\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"on August 24, 2015\"")));

        CrawleableUri curi = new CrawleableUri(new URI("http://prototype-gismontgomery.opendata.arcgis.com/datasets"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/prototype_gismontgomery/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailsPage() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Full Address\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Full Addresses for a given location\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"jay.mukherjee\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"on March 26, 2014\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("https://mcatlas.org/arcgis3/rest/services/overlays/Address_Labels/MapServer/0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("\"License No license specified\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/3f2dc8774b934b17b1c8bf9c2d05d45b_0.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/3f2dc8774b934b17b1c8bf9c2d05d45b_0.kml")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/3f2dc8774b934b17b1c8bf9c2d05d45b_0.zip")));
        CrawleableUri curi = new CrawleableUri(new URI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/3f2dc8774b934b17b1c8bf9c2d05d45b_0"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/prototype_gismontgomery/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));
        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageOsniSpatial() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://osni-spatial-ni.opendata.arcgis.com/datasets/d9dfdaf77847401e81efc9471dcd09e1_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://osni-spatial-ni.opendata.arcgis.com/datasets/a55726475f1b460c927d1816ffde6c72_2")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://osni-spatial-ni.opendata.arcgis.com/datasets/1cdb3f26958046799f84a3de58dcc349_6")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://osni-spatial-ni.opendata.arcgis.com/datasets/ae71b79359634fbaaad18a6826710ed5_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://osni-spatial-ni.opendata.arcgis.com/datasets/de0116be5fe5499f921c112568218a39_1")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://osni-spatial-ni.opendata.arcgis.com/datasets/55cd419b2d2144de9565c9b8f73a226d_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://osni-spatial-ni.opendata.arcgis.com/datasets/4db1ea247b4e48ad8b55c3014f062f34_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://opendatani.blob.core.windows.net/lpsopendata/OSNI_OPEN_DATA_Midscale_Raster.zip")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://opendatani.blob.core.windows.net/lpsopendata/10M-DTM-Sheets251-293.zip")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://opendatani.blob.core.windows.net/lpsopendata/10M-DTM-Sheets101-150.zip")));
        CrawleableUri curi = new CrawleableUri(new URI("http://osni-spatial-ni.opendata.arcgis.com/datasets"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/osni_spatial/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageOsniSpatial() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"OSNI Open Data 50k Admin Boundaries - Townlands\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Mid scale vector polygon dataset showing townland boundaries. Published here for OpenData. By download or use of this dataset you agree to abide by the Open Government Data Licence.\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"SpatialNI_Admin\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("https://gisservices.spatialni.gov.uk/arcgisc/rest/services/OpenData/OSNIOpenData_50KBoundaries/MapServer/6")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("https://www.arcgis.com/home/item.html?id=1cdb3f26958046799f84a3de58dcc349")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("\"License Open Government Licence applies Land & Property Services (LPS) has made a number of Ordnance Survey of Northern Ireland® (OSNI®) branded datasets available free of charge under the terms of the current Open Government Licence (OGL). These datasets – which include raster and vector mapping, boundary, gazetteer, height, street and townland products – are available for download. Each dataset is clearly marked with the OGL symbol shown belowThe OGL allows you to: copy, distribute and transmit the data; adapt the data; and exploit the data commercially, whether by sub-licensing it, combining it with other data, or including it in your own product or application. You are therefore able to use the LPS datasets in any way and for any purpose. We simply ask that you acknowledge the copyright and the source of the data by including the following attribution statement:Contains public sector information licensed under the terms of the Open Government Licence v3.0.You must also: include the same acknowledgement requirement in any sub-licences of the data that you grant, and a requirement that any further sub-licences do the same; ensure that you do not use the data in a way that suggests LPS endorses you or your use of the data; and make sure you do not misrepresent the data or its source. N.B. Any dataset that does not expressly state that it has been released under OGL will require a licence from LPS and the appropriate licence fee will be applied.\"")));
        CrawleableUri curi = new CrawleableUri(new URI("http://osni-spatial-ni.opendata.arcgis.com/datasets/1cdb3f26958046799f84a3de58dcc349_6"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/osni_spatial/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }
}
