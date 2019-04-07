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
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/1959435a5a81409992b45cc976ac6d1b_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://prototype-gismontgomery.opendata.arcgis.com/datasets/d3ba2027c4c8422c83282459c8c29c14_0")));
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

    @Test
    public void scrapeSearchResultPageIllinoisAirports() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://illinoisairports-cmtengr.opendata.arcgis.com/datasets/5cf21901a65c4ba69acf1219df5ab27c_1")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://illinoisairports-cmtengr.opendata.arcgis.com/datasets/b755db2c2ba0454ca4e334760359f6a9_1")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://illinoisairports-cmtengr.opendata.arcgis.com/datasets/5cf21901a65c4ba69acf1219df5ab27c_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://illinoisairports-cmtengr.opendata.arcgis.com/datasets/b755db2c2ba0454ca4e334760359f6a9_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.arcgis.com/home/webmap/viewer.html?webmap=215cac6989e742a588aa4d9bc8289b2c")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.arcgis.com/home/webmap/viewer.html?webmap=c24d61dc4e9f4ec7aa9243a4a4737c59")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.arcgis.com/home/webmap/viewer.html?webmap=db4740a131c94f34ae721b46a18a8cba")));
        CrawleableUri curi = new CrawleableUri(new URI("http://illinoisairports-cmtengr.opendata.arcgis.com/datasets"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/illinois_airports/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageIllinoisAirports() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"TREE\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"Boyd\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://54.163.215.232:6080/arcgis/rest/services/Misc_Services/SPITTEST/MapServer/1")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("https://www.arcgis.com/home/item.html?id=5cf21901a65c4ba69acf1219df5ab27c")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://illinoisairports-cmtengr.opendata.arcgis.com#")));
        CrawleableUri curi = new CrawleableUri(new URI("http://illinoisairports-cmtengr.opendata.arcgis.com/datasets/b755db2c2ba0454ca4e334760359f6a9_1"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/illinois_airports/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageDataSofartdk() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-sofartdk.opendata.arcgis.com/datasets/944f233ffc2049e79d19bfe87bde8759_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-sofartdk.opendata.arcgis.com/datasets/ffc374744b6d43bcb7ef7a9d07b37229_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-sofartdk.opendata.arcgis.com/datasets/86b43ad590b54fcdab9e574a65360d31_0")));
        CrawleableUri curi = new CrawleableUri(new URI("http://data-sofartdk.opendata.arcgis.com/datasets"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_sofartdk/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageSofartdk() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Skibsruter TSS\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Skibsruter og TSS Information på http://data.soefartsstyrelsen.dk\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"sofart.dk\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://map.dma.dk/arcgis/rest/services/Skibsruter_TSS/MapServer/0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("https://www.arcgis.com/home/item.html?id=944f233ffc2049e79d19bfe87bde8759")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://data-sofartdk.opendata.arcgis.com#")));
        CrawleableUri curi = new CrawleableUri(new URI("http://data-sofartdk.opendata.arcgis.com/datasets/944f233ffc2049e79d19bfe87bde8759_0"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_sofartdk/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageDataReitroutenPlaner() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://data-reitroutenplaner.opendata.arcgis.com/datasets/56941fd831b34abc9da66f405670036a_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-reitroutenplaner.opendata.arcgis.com/datasets/56941fd831b34abc9da66f405670036a_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"1-1 of 1 results\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"schoolgis_chr.sailer\"")));
        CrawleableUri curi = new CrawleableUri(new URI("http://data-reitroutenplaner.opendata.arcgis.com/datasets"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_reitroutenplaner/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageReitroutenPlaner() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Altersheim Restis\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"schoolgis_chr.sailer\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("https://services1.arcgis.com/6RDtDcHz3yZdtEVu/arcgis/rest/services/Altersheim_Restis/FeatureServer/0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("https://www.arcgis.com/home/item.html?id=56941fd831b34abc9da66f405670036a")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("\"License No license specified\"")));
        CrawleableUri curi = new CrawleableUri(new URI("http://data-reitroutenplaner.opendata.arcgis.com/datasets/56941fd831b34abc9da66f405670036a_0"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_reitroutenplaner/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageDataGisAu() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-gis-au.opendata.arcgis.com/datasets/ce14e9631cdc41e1ba7b43aa547c52d4_3")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-gis-au.opendata.arcgis.com/datasets/3fd26498dee34a87a62a39006b6d31cf_2")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-gis-au.opendata.arcgis.com/datasets/ce14e9631cdc41e1ba7b43aa547c52d4_2")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-gis-au.opendata.arcgis.com/datasets/3fd26498dee34a87a62a39006b6d31cf_1")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-gis-au.opendata.arcgis.com/datasets/ce14e9631cdc41e1ba7b43aa547c52d4_1")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-gis-au.opendata.arcgis.com/datasets/3fd26498dee34a87a62a39006b6d31cf_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-gis-au.opendata.arcgis.com/datasets/ce14e9631cdc41e1ba7b43aa547c52d4_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-gis-au.opendata.arcgis.com/datasets/aafdaf30aa1849f397df520b492a2221_0")));
        CrawleableUri curi = new CrawleableUri(new URI("http://data-gis-au.opendata.arcgis.com/datasets"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_gis_au/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageGisAu() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"BaltSvingelArtsOverv\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Baltisk Svingel fundsteder fra naturdatabasen\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"larskeh_gisau\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("https://services1.arcgis.com/x9D40zPeQJc4nxza/ArcGIS/rest/services/BaltSvingel/FeatureServer/3")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("https://www.arcgis.com/home/item.html?id=ce14e9631cdc41e1ba7b43aa547c52d4")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("\"License No license specified\"")));
        CrawleableUri curi = new CrawleableUri(new URI("http://data-gis-au.opendata.arcgis.com/datasets/ce14e9631cdc41e1ba7b43aa547c52d4_3"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_gis_au/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeIndexPagePageAmbar() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset")));
        CrawleableUri curi = new CrawleableUri(new URI("http://ambar.utpl.edu.ec"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/ambar/index.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageAmbar() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/feminicidios-latam")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/knowledge-graph-about-historical-figures-of-ecuador")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/problematicas-y-conflictos-socioambientales")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/national-health-and-nutrition-examination-survey-nhanes")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/nutrition-physical-activity-and-obesity-behavioral-risk-factor-surveillance-system")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/nutricion-ninez")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/tasa-de-mortalidad-infantil")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/education-statistics-world-bank")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/educacion-de-calidad")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/datos-encuestas-sobre-percepcion-de-audiencias-de-tv-local-en-ecuador")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/fallos-de-casacion-1994-2004")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/marcas-de-preguntas-matematica")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/web-gad-ec")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/estados-financieros-de-las-companias-en-el-ecuador-en-2016")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/organismo-de-ciencia-y-tecnologia-de-iberoamerica")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/consumo-de-agua-ciudad-victoria")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/datos-de-publicaciones-de-la-cuenta-municipal-mi-puyango-en-facebook")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/caracteres-problemas")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/crecimiento-sustentable-van-horne-2017")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/catalogo-de-los-recursos-gastronomicos-mancomunidad-bosque-seco")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset?page=2")));
        CrawleableUri curi = new CrawleableUri(new URI("http://ambar.utpl.edu.ec/dataset"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/ambar/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageAmbar() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"feminicidios-LatAm\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"A medida que las tecnologías de la información y la comunicación incrementan su impacto social, más académicos se interesan en vincular la tecnología con las necesidades reales de la sociedad. En este documento se presenta el desarrollo de una iniciativa de monitoreo de feminicidios en América Latina, que se ha desarrollado con la colaboración de estudiantes de ingeniería en sistemas quienes a través de la metodología de aprendizaje mediante retos lograron desarrollar prototipos de visualizaciones cívicas para dar a conocer al mundo esta problemática y como aporte en datos abiertos al problema latente en nuestra región.\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"Ambar\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://www.opendefinition.org/licenses/cc-by")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"October 29, 2018, 16:33\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"October 29, 2018, 16:24\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://ambar.utpl.edu.ec/dataset/5e16693b-b693-4e87-9948-0d2a3d82fc81/resource/8e3c2eec-83ca-4a2b-af92-e8e06f8c87e0/download/datafeminicidiosmundo.xlsx")));
        CrawleableUri curi = new CrawleableUri(new URI("http://ambar.utpl.edu.ec/dataset/feminicidios-latam"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/ambar/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageCatalougeDataGovtNz() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/directory-of-educational-institutions")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/emergency-management-basemap")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/nzflora-brownsey-perrie-2018-davalliaceae")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/nzflora-fife-2018-ptychomitriaceae")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/nzflora-brownsey-perrie-2015-ophioglossaceae")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/phylogeny-of-new-zealand-ascomycetes")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/nzflora-fife-2018-rhabdoweisiaceae")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/nzflora-fife-2015-meesiaceae")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/nzflora-brownsey-perrie-2018-selaginellaceae")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/nzflora-brownsey-perrie-2018-tectariaceae")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/regional-gross-domestic-product")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/family-services-directory")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/pharmaceutical-schedule-database")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/pharmac-chief-executive-expenses")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/international-migration")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/international-travel")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/accommodation-survey")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/gambling-expenditure-statistics")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/gaming-machine-statistics")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/food-price-index")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset?page=2")));
        CrawleableUri curi = new CrawleableUri(new URI("https://catalogue.data.govt.nz/dataset"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/catalogue_data_govt_nz/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageCatalougeDataGovtNz() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Directory of Educational Institutions\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"The following key areas summarise the overall focus of the Ministry's work: 1) More children gaining strong learning foundations; 2) More students participating in and achieving... read more\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"Ministry of Education\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/creator"), NodeFactory.createURI("http://purl.org/dc/terms/creator"), NodeFactory.createURI("\"Ministry of Education\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"2018-10-31\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"Unknown\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#distribution"), NodeFactory.createURI("http://www.w3.org/ns/dcat#distribution"), NodeFactory.createURI("http://www.educationcounts.govt.nz/statistics/tertiary_education/27436")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/language"), NodeFactory.createURI("http://purl.org/dc/terms/language"), NodeFactory.createURI("\"English\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/spatial"), NodeFactory.createURI("http://purl.org/dc/terms/spatial"), NodeFactory.createURI("\"\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/2756db90-a096-4ffa-9fb4-73b74ad279d9/resource/26f44973-b06d-479d-b697-8d7943c97c57/download/ecedirectory-19-03-2019-110021.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://www.educationcounts.govt.nz/__data/assets/file/0009/63873/Directory-Maori-Medium-current.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://www.educationcounts.govt.nz/__data/assets/file/0004/74344/Directory-Private-Schools-Current.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://catalogue.data.govt.nz/dataset/2756db90-a096-4ffa-9fb4-73b74ad279d9/resource/bdfe0e4c-1554-4701-a8fe-ba1c8e0cc2ce/download/schooldirectory-19-03-2019-233049.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://www.educationcounts.govt.nz/__data/assets/file/0005/62573/Directory-Tertiary-Current.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0008/170684/School-Closures_Mergers-and-New_Nov_2016.xlsx")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0007/162259/School-Closures_Mergers-and-New_Dec_2015.xlsx")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0008/145709/School-Closures_Mergers-and-New_Nov_2014.xlsx")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0009/115758/School-Closures_Mergers-and-New_Nov_Dec_2013.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0014/107330/School-Closures_Mergers-and-New_Dec_2012.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0009/101313/School-Closures_Mergers-and-New_Dec_2011.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0018/71505/School-Closures-Mergers-New-2010.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0010/38962/School-Closures-Mergers-New-2009.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0008/33983/School_Closures_Mergers_and_New_December_2008.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/file/0003/7770/Edcounts2007.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/file/0006/7773/mergers-closures-new-sch-jan06-jan07.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.educationcounts.govt.nz/__data/assets/file/0005/7772/mergers-closures-new-sch-jan05-jan06.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://www.educationcounts.govt.nz/__data/assets/file/0004/7771/mergers-closures-new-sch-jan04-jan05.xls")));
        CrawleableUri curi = new CrawleableUri(new URI("https://catalogue.data.govt.nz/dataset/directory-of-educational-institutions"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/catalogue_data_govt_nz/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeIndexPageLaosOpenDev() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/search/data/")));
        CrawleableUri curi = new CrawleableUri(new URI("https://laos.opendevelopmentmekong.net/"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/laos_opendevelopmentmekong/index.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageLaosOpenDev() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/search/data/")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=economic-corridors-of-laos&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=railway-lines-of-laos&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=airports-of-laos&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=registering-property-indicators-laos&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=soil-types-of-laos&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=river-networks-of-laos&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=laos-aqueduct-atlas&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=residence-areas-of-lao-ethno-linguistic-group-lao-tai&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=main-tourist-sites-of-laos&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=faostat-lao-people-s-democratic-republic&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=gms-major-urban-areas-laos&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/dataset/?id=laos-protected-areas-and-heritage-sites&search_query=Pw==")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/search/data/?page=2")));
        CrawleableUri curi = new CrawleableUri(new URI("https://laos.opendevelopmentmekong.net/search/data/"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/laos_opendevelopmentmekong/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageLaosOpenDev() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("https://laos.opendevelopmentmekong.net/search/data/")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Registering property indicators, Laos\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Three of the Laos' property indicators from the World Bank Doing Business report, 2016. Collated for the Laos Land page: https://opendevelopmentmekong.net/topics/land-laos\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"Source: World Bank. Doing Business Report 2016. http://www.doingbusiness.org/custom-query\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/creator"), NodeFactory.createURI("http://purl.org/dc/terms/creator"), NodeFactory.createURI("\"Source: World Bank. Doing Business Report 2016. http://www.doingbusiness.org/custom-query\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"24 Jun 2016\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"17 May 2016\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#distribution"), NodeFactory.createURI("http://www.w3.org/ns/dcat#distribution"), NodeFactory.createURI("\"Source: World Bank. Doing Business Report 2016. http://www.doingbusiness.org/custom-query\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/language"), NodeFactory.createURI("http://purl.org/dc/terms/language"), NodeFactory.createURI("\"English\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("https://creativecommons.org/licenses/by/4.0/")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://data.opendevelopmentmekong.net/dataset/9d9a2f88-0a89-41bb-8f81-6741253c6e89/resource/6ae58114-7a60-4631-bd2d-085b5c8c6650/download/registering-property-indicators-laos.csv")));
        CrawleableUri curi = new CrawleableUri(new URI("https://laos.opendevelopmentmekong.net/dataset/?id=registering-property-indicators-laos&search_query=Pw=="));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/laos_opendevelopmentmekong/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageSciencebaseGov() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4a71e4b07f02db6424da")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4aa8e4b07f02db6675bd")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e488be4b07f02db51c765")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4a59e4b07f02db62f8f2")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4799e4b07f02db48fcde")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e477ae4b07f02db47f7b0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4a6fe4b07f02db640ed5")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4b09e4b07f02db69bdeb")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4adce4b07f02db68693a")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4813e4b07f02db4da961")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4884e4b07f02db51840f")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4aa8e4b07f02db6673be")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4783e4b07f02db4836a5")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4afbe4b07f02db696323")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e479ee4b07f02db492648")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4a70e4b07f02db6418f3")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e479ae4b07f02db490054")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/4f4e4ac0e4b07f02db676d5a")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/542d8123e4b092f17defc662")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"),  NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/item/542d833ce4b092f17defc66b")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("https://www.sciencebase.gov/catalog/items?q=&filter0=browseCategory%3DData&offset=20&max=20")));
        CrawleableUri curi = new CrawleableUri(new URI("https://www.sciencebase.gov/catalog/items?q=&filter0=browseCategory%3DData"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/sciencebase_gov/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageSciencebaseGov() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Mean Minimum Winter Temperature (deg. C) for Northeast, Projected for 2060, RCP4.5, Ensemble GCM Results\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"To evaluate the potential effects of climate change on wildlife habitat and ecological integrity in the northeastern United States from 2010 to 2080, a University of Massachusetts Amherst team derived a set of climate projections at a fine spatial resolution for the entire Northeast. The projections are based upon publicly available climate models.This dataset represents the mean of the minimum air temperature (degrees C) for December, January, and February using one of two IPCC greenhouse gas concentration scenarios (RCP4.5). The dataset is intended to represent typical winter temperatures in the decade centered on 2060 rather than the actual temperatures during 2060. MAP UNITS ARE TEMP. IN DEGREES C MULTIPLIED BY 100 (which allows for more efficient data storage). Detailed documentation for all of the UMass climate datasets is available from: http://jamba.provost.ads.umass.edu/web/lcc/DSL_documentation_climate.pdf . The climate work is part of the Designing Sustainable Landscapes project led by Professor Kevin McGarigal of UMass Amherst and sponsored by the North Atlantic Landscape Conservation Cooperative; for more information about the entire project see: http://www.umass.edu/landeco/research/dsl/dsl.html The dataset was derived from the following sources: - An average or ensemble of results from 14 Atmospheric-Ocean Circulation Models (AOGCMs) publicly available from the World Climate Research Programme's (WCRP) Coupled Model Intercomparison Project phase 5 (CMIP5). These complex models produce long-term climate projections by integrating oceanic and atmospheric processes. The results have been downscaled (projected to a finer resolution) using the Bias Corrected Spatial Disaggregation (BCSD) approach. Results were developed for the two scenarios of greenhouse gas concentrations (RCP4.5 and RCP8.5) that were available for every CMIP5 AOGCM; this dataset is based on RCP4.5. Output are at a resolution of approximately 12 km resolution. - The results were further refined to approximately 600 m resolution by reference to the Parameter-elevation Relationships on Independent Slopes Model (PRISM) dataset, This model takes into account elevation, aspect, proximity to the coast, and other factors to predict climate based on results from 10,000 weather stations. Thirty year average data for 1981-2010 (i.e., centered on 1995) were used. This dataset is one of multiple climate datasets consisting of: - Projections for the years 2010, 2020, 2030, 2040, 2050, 2060, 2070, and 2080 - Projections for both the RCP4.5 and RCP8.5 greenhouse gas concentration scenarios - The following datasets expected to have important effects in determining the occurrence and survival of fish, wildlife, and plant populations: 1) Total annual precipitation 2) Precipitation during the growing season (May-Sept.) 3) Average annual temperature 4) Mean minimum winter temperature 5) Mean maximum summer temperature 6) Mean July temperature 7) Growing degree days (number of days in which the average temperature is >10 degrees C) 8) Heat index 30 (number of days in which the maximum temperature is >30 degrees C) 9) Heat index 35 (number of days in which the maximum temperature is >35 degrees C)\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"2013-09-10\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"2013-09-10\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"tmin60_45_md.xml\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"tmin60_45.zip\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"tmin60_45.sd\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"thumbnail.png\"")));
        CrawleableUri curi = new CrawleableUri(new URI("https://www.sciencebase.gov/catalog/item/542d8123e4b092f17defc662"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/sciencebase_gov/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeIndexPageUsgs() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("https://www.usgs.gov/products/data-and-tools/science-datasets")));
        CrawleableUri curi = new CrawleableUri(new URI("https://www.usgs.gov/products/data-and-tools/overview"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/usgs/index.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageUsgs() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://doi.org/10.5066/P9W4SF05")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://dx.doi/10.5066/P9KKB3H2")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://doi.org/10.5066/P9XDVRMT")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://doi.org/10.5066/F75B01RW")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://doi.org/10.5066/P9Z0SBKZ")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://doi.org/10.5066/P93R9UEL")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://doi.org/10.5066/P9B1VZNJ")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://dx.doi.org/10.5066/F79885XC")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://doi.org/10.5066/P9PMMSHX")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://doi.org/10.5066/F7SX6BPF")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("https://www.usgs.gov/products/data-and-tools/science-datasets?page=1")));
        CrawleableUri curi = new CrawleableUri(new URI("https://www.usgs.gov/products/data-and-tools/science-datasets"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/usgs/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageUsgs() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Data release for the Land Change Causes for the United States Interior Highlands (2001 to 2006 and 2006 to 2011 time intervals)\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"These data were created to describe the causes of land cover change that occurred in the Interior Highland region of the United States for the time intervals of 2001 to 2006 and 2006 to 2011. This region, which covers approximately 17.5 million hectares, includes portions of the U.S. states of Arkansas, Missouri, Oklahoma, and Kansas. Most of the area is covered by gently rolling hills of forests and pastureland. Two raster maps were created at a 30-meter resolution showing the causes of land change using automated and manual photo interpretation techniques. There were 30 categories of land change causes (i.e., forest harvest or surficial mining) discovered over the Interior Highlands. These categories can be used by researchers to summarize the historical patterns of land change for the region and to understand the impacts that these land change causes may have on the areas’ ecology, hydrology, wildlife, and climate.\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"2019-03-13\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"2019-03-13\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_01to06_cause_data_release.aux\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_01to06_cause_data_release.tif.aux.xml\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_01to06_cause_data_release.tif.vat.cpg\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_06to11_cause_data_release.aux\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_01to06_cause_data_release.tif.vat.dbf\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_01to06_cause_data_release.tfw\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_06to11_cause_data_release.tif.vat.cpg\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_06to11_cause_data_release.tif.xml\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_06to11_cause_data_release.tif.aux.xml\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_01to06_cause_data_release.tif.xml\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_06to11_cause_data_release.tfw\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_06to11_cause_data_release.tif.vat.dbf\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_01to06_cause_data_release.tif.ovr\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_06to11_cause_data_release.tif.ovr\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_01to06_cause_data_release.tif\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"interior_highlands_06to11_cause_data_release.tif\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("\"Int_Highlands_metadata_reconciliation_031219.xml\"")));
        CrawleableUri curi = new CrawleableUri(new URI("https://www.sciencebase.gov/catalog/item/5bb541c2e4b0fc368e877f33"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/usgs/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageDonnesVille() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://donnees.ville.montreal.qc.ca/dataset/inspection-aliments-contrevenants")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://donnees.ville.montreal.qc.ca/dataset/agriculture-urbaine-sondage")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://donnees.ville.montreal.qc.ca/dataset/cuisine-de-rue")));
        CrawleableUri curi = new CrawleableUri(new URI("http://donnees.ville.montreal.qc.ca/group/agriculture-alimentation"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/donnes_ville/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageDonnesVille() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Inspection des aliments – contrevenants\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Liste des établissements alimentaires situés sur le territoire de l’agglomération montréalaise et sous la responsabilité de la Division de l’inspection des aliments de la Ville de Montréal ayant fait l’objet d’une condamnation pour une infraction à la Loi sur les produits alimentaires (L.R.Q., c. P-29) et ses règlements.\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"La localisation des contrevenants sur une carte est disponible sur le portail du MAPAQ.\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"Service de l'environnement - Division de l'inspection des aliments\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/spatial"), NodeFactory.createURI("http://purl.org/dc/terms/spatial"), NodeFactory.createURI("\"Territoire de la ville de Montréal\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/language"), NodeFactory.createURI("http://purl.org/dc/terms/language"), NodeFactory.createURI("\"Français\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://creativecommons.org/licenses/by/4.0/")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"2019-02-27 17:07\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"2013-10-08 12:19\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://donnees.ville.montreal.qc.ca/dataset/inspection-aliments-contrevenants/resource/54d7ffa0-04bf-442c-bacd-a84c6aab888d")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://donnees.ville.montreal.qc.ca/dataset/a5c1f0b9-261f-4247-99d8-f28da5000688/resource/54d7ffa0-04bf-442c-bacd-a84c6aab888d/download/inspection-aliments-contrevenants.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://donnees.ville.montreal.qc.ca/dataset/inspection-aliments-contrevenants/resource/92719d9b-8bf2-4dfd-b8e0-1021ffcaee2f")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://donnees.ville.montreal.qc.ca/dataset/a5c1f0b9-261f-4247-99d8-f28da5000688/resource/92719d9b-8bf2-4dfd-b8e0-1021ffcaee2f/download/inspection-aliments-contrevenants.xml")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://donnees.ville.montreal.qc.ca/dataset/inspection-aliments-contrevenants/resource/51026016-7d82-49dc-93e0-2176df8790c6")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://donnees.ville.montreal.qc.ca/dataset/a5c1f0b9-261f-4247-99d8-f28da5000688/resource/51026016-7d82-49dc-93e0-2176df8790c6/download/inspection-aliments-contrevenants.xlsx")));
        CrawleableUri curi = new CrawleableUri(new URI("http://donnees.ville.montreal.qc.ca/dataset/inspection-aliments-contrevenants"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/donnes_ville/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageEnvGovUk() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/system/_bulkCollectionTypes")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_catchment-planning")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/_category")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_CEFAS")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_data-standards")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/_def")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_DEFRA")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_EA")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/ea-organization/_ea_areas")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_ea-organization")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/_entity-type")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/system/_form-templates")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_JNCC")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/system/_links")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_medin")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_MMO")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/mmo-experimental/_data-theme")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_mmo-experimental")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/mmo-experimental/_topic-category")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/def/_NE")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("\"Next\"")));
        CrawleableUri curi = new CrawleableUri(new URI("http://environment.data.gov.uk/registry/"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/environment_data_gov_uk/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageEnvGovUk() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_advisory-committee-on-pesticides")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_advisory-committee-on-releases-to-the-environment")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_agricultural-dwelling-house-advisory-committees-x16")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_agricultural-wages-committee-x13")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_agriculture-and-horticulture-development-board")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_animal-health-and-veterinary-laboratories-agency")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_broads-authority")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_centre-for-environment-fisheries-and-aquaculture-science")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_consumer-council-for-water")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_covent-garden-market-authority")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_dartmoor-national-park-authority")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_department-for-environment-food-rural-affairs")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_drinking-water-inspectorate")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_environment-agency")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_exmoor-national-park-authority")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_the-food-and-environment-research-agency")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_forestry-commission")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_gangmasters-licensing-authority")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_independent-agricultural-appeals-panel")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/_joint-nature-conservation-committee")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("\"Next\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Register: Organizations\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Codes used to identify each organization in the DEFRA family.\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#theme"), NodeFactory.createURI("http://www.w3.org/ns/dcat#theme"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/category/System")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/department-for-environment-food-rural-affairs")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/creator"), NodeFactory.createURI("http://purl.org/dc/terms/creator"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org/department-for-environment-food-rural-affairs")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://environment.data.gov.uk/registry/?entity=http%3a%2f%2fwww.nationalarchives.gov.uk%2fdoc%2fopen-government-licence%2fversion%2f2%2f")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://environment.data.gov.uk/registry/?entity=http%3a%2f%2fschema.theodi.org%2fodrs%23RightsStatement")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"5 Mar 2014 17:24:17.517\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org?_format=ttl&_view=with_metadata")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org?_format=rdf&_view=with_metadata")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org?_format=jsonld&_view=with_metadata")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://environment.data.gov.uk/registry/structure/org?_format=csv&_view=with_metadata")));
        CrawleableUri curi = new CrawleableUri(new URI("http://environment.data.gov.uk/registry/structure/_org"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/environment_data_gov_uk/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageDataIadb() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://data.iadb.org#DataCatalogID=fmzp-pctv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://data.iadb.org#DataCatalogID=75uh-zme7")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://data.iadb.org#DataCatalogID=rq4a-d9gg")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://data.iadb.org#DataCatalogID=hg7w-u675")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://data.iadb.org#DataCatalogID=u7bw-exzv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://data.iadb.org#DataCatalogID=2ffa-hhe4")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://data.iadb.org#DataCatalogID=v2c9-36h7")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://data.iadb.org#DataCatalogID=2dqw-u35p")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://data.iadb.org#DataCatalogID=11319/9095")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("https://data.iadb.org#DataCatalogID=11319/9069")));
        CrawleableUri curi = new CrawleableUri(new URI("https://data.iadb.org/DataCatalog/Dataset"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_iadb/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageDataIadb() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("https://data.iadb.org")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"This dataset contains information on 404 Centros infantiles del Buen Vivir (CIBVs), Ecuador's primary providers of public child care services for infant and toddlers at the time of data collection in 2012. In 2012, The Inter-American Development Bank administered a battery of widely-used instruments to measure the quality of child care services in the CIBVs, including four internationally-recognized quality instruments that were then adapted for use in Ecuador.\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"Protección social y salud\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("https://creativecommons.org/licenses/by-nc-nd/3.0/igo/legalcode")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"Jan 15, 2019\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"Jan 15, 2019\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://mydata.iadb.org/resource/fmzp-pctv.CSV")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://data.iadb.orgjavascript:download('https://mydata.iadb.org/api/views/fmzp-pctv/rows.JSON?accessType=DOWNLOAD','fmzp-pctv.JSON');")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://mydata.iadb.org/resource/fmzp-pctv.RDF")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://data.iadb.orgjavascript:download('https://mydata.iadb.org/api/views/fmzp-pctv/rows.RSS?accessType=DOWNLOAD','fmzp-pctv.RSS');")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://data.iadb.orgjavascript:download('https://mydata.iadb.org/api/views/fmzp-pctv/rows.XML?accessType=DOWNLOAD','fmzp-pctv.XML');")));
        CrawleableUri curi = new CrawleableUri(new URI("https://data.iadb.org/DataCatalog/Dataset#DataCatalogID=75uh-zme7"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_iadb/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeSearchResultPageDataNaerArcgis() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-naer.opendata.arcgis.com/datasets/e7e6b3e7c5704d48bbb221d95e0802be_0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://projekt-opal.de/dataset#link"), NodeFactory.createURI("http://data-naer.opendata.arcgis.com/datasets/9f66cc9c7d6246d8857bfbef18edc3ff_0")));
        CrawleableUri curi = new CrawleableUri(new URI("http://data-naer.opendata.arcgis.com/datasets"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_naer_arcgis/search_result_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageDataNaerArcgis() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"CHRsogn0015\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"tinkas\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("https://services2.arcgis.com/DqYc4VbfKJbiqFCu/arcgis/rest/services/CHRsogn0015/FeatureServer/0")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("https://www.arcgis.com/sharing/rest/content/items/e7e6b3e7c5704d48bbb221d95e0802be/info/metadata/metadata.xml?format=default&output=html")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("\"License No license specified\"")));
        CrawleableUri curi = new CrawleableUri(new URI("http://data-naer.opendata.arcgis.com/datasets/e7e6b3e7c5704d48bbb221d95e0802be_0"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_naer_arcgis/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageTransparencia() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://transparencia.gijon.es/page/1808-catalogo-de-datos")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Relación de subvenciones fachadas y ascensores\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Portales\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Descripción Disposiciones de Gasto del económico 78009\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://transparencia.gijon.es/og_categories/show/113-ayuntamiento-de-gijon")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"Viernes, 4 de Enero de 2019\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("\"URL del dataset http://opendata.gijon.es/descargar.php?id=723\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://transparencia.gijon.es/og_categories/show/112-creative-commons-reconocimiento-3-0-espana")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"Viernes, 4 de Enero de 2019\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://opendata.gijon.es/descargar.php?id=723&tipo=CSV")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://opendata.gijon.es/descargar.php?id=723&tipo=EXCEL")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://opendata.gijon.es/descargar.php?id=723&tipo=JSON")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://opendata.gijon.es/descargar.php?id=723&tipo=PDF")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://opendata.gijon.es/descargar.php?id=723&tipo=XHTML")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://opendata.gijon.es/descargar.php?id=723&tipo=XML")));
        CrawleableUri curi = new CrawleableUri(new URI("http://transparencia.gijon.es/risp_datasets/show/albergues"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/transparencia_gijon/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageCatalogDataGov() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"National Student Loan Data System\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"The National Student Loan Data System (NSLDS) is the national database of information about loans and grants awarded to students under Title IV of the Higher Education Act (HEA) of 1965. NSLDS provides a centralized, integrated view of Title IV loans and grants during their complete life cycle, from aid approval through disbursement, repayment, deferment, delinquency, and closure.\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"Office of Federal Student Aid\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"January 7, 2016\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("https://catalog.data.gov/harvest/object/b764a3d1-d5cc-4456-8d05-a6a17e21cc7b")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://www.opendefinition.org/licenses/cc-zero")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"August 9, 2018\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://ifap.ed.gov/fedschcodelist/attachments/1617FedSchoolCodeList.xlsx")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/about/data-center/student/title-iv/sites/default/files/fsawg/datacenter/library/FL_Dashboard_AY2009_2010_Q1.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/about/data-center/student/title-iv/sites/default/files/fsawg/datacenter/library/FL_Dashboard_AY2009_2010_Q2.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/about/data-center/student/title-iv/sites/default/files/fsawg/datacenter/library/FL_Dashboard_AY2009_2010_Q3.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/about/data-center/student/title-iv/sites/default/files/fsawg/datacenter/library/FL_Dashboard_AY2009_2010_Q4.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/PortfolioSummary.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/PortfoliobyLoanType.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/LocationofFFELPLoans.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/PortfoliobyLoanStatus.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/DLbyDefermentType.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/DLbyForbearanceType.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/FFELbyDefermentType.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/DLPortfoliobyRepaymentPlan.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/DLPortfoliobyDelinquencyStatus.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/ECFReport.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sites/default/files/fsawg/datacenter/library/DLEnteringDefaults.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://studentaid.ed.gov/sites/default/files/fsawg/datacenter/library/TLF.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://catalog.data.gov/harvest/object/b764a3d1-d5cc-4456-8d05-a6a17e21cc7b")));
        CrawleableUri curi = new CrawleableUri(new URI("https://catalog.data.gov/dataset/demographic-statistics-by-zip-code-acfc9"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/catalog_data_gov/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageBrooklineArcgis() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Building Permits\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"Town-of-Brookline.MA\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://gisweb.brooklinema.gov/arcgis/rest/services/OpenDataPortal/MapServer/14")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("https://www.arcgis.com/home/item.html?id=094e5eda61db4e8b855e736343a37b3a")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("\"License No license specified\"")));
        CrawleableUri curi = new CrawleableUri(new URI("http://data-brookline.opendata.arcgis.com/datasets/094e5eda61db4e8b855e736343a37b3a_14"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/data_brookline_arcgis/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageAbertosXunta() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://abertos.xunta.gal/busca-de-datos")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://abertos.xunta.gal#")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Calendario 2019 de publicación de estatísticas do IGE\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Calendario de 2019 dos días nos que se publican as estatísticas conxunturais , de periodicidade mensual e trimestral, do Instituto Galego de Estatística. A información que proporciona inclúe a data de referencia na que estarán dispoñibles os resultados. As actividades estruturais, de periodicidade igual ou superior ao ano, móstranse debaixo do calendario de cada mes e difúndense na páxina web do IGE ao longo dese período. As estatísticas cuxa referencia se contempla no calendario, xunto coas siglas polas que se recoñecen, son: EC - Estatística de construción de edificios AfiSS - Explotación das afiliacións á Seguridade Social IPC - Índice de prezos ao consumo CEXT - Comercio exterior e intracomunitario IVU - Índices de valor unitario para o comercio exterior EPA - Enquisa de poboación activa EPAx - Enquisa de poboación activa. Estudo sobre a relación coa actividade da poboación xuvenil IASS - Indicadores de actividade e de VEB do sector servizos IPRI - Índice de prezos industriais ECF - Enquisa conxuntural a fogares BORME - Explotación do Boletín Oficial do Rexistro Mercantil IC - Índices de competitividade EPAF - Estatística de fluxos da poboación activa AfiSSC - Afiliacións á Seguridade Social por concello de residencia do/a afiliado/a IPI - Índice de produción industrial IVCM - Índice de vendas de comercio polo miúdo O calendario está dispoñible en formato .ics (estándar iCalendar que lle permite á persoa usuaria subscribirse aos datos de forma que poida aplicalos a un calendario do seu ordenador, dispositivo móbil...).\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"08-01-2019\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://www.ige.eu/")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://creativecommons.org/licenses/by/3.0/es/deed.gl")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"08-01-2019\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://abertos.xunta.gal/catalogo/cultura-ocio-deporte/-/dataset/0400/calendario-2019-publicacion-estatisticas-ige.rdf")));
        CrawleableUri curi = new CrawleableUri(new URI("http://abertos.xunta.gal/catalogo/cultura-ocio-deporte/-/dataset/0400/calendario-2019-publicacion-estatisticas-ige"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/abertos_xunta/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageOpendataBordeaux() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("\"Les données\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Budget 2003 par nomenclature\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Description : Budget 2003 par nomenclature.\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("\"Propriétaire : Ville de Bordeaux\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"06/05/2015\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://purl.org/dc/terms/license"), NodeFactory.createURI("http://opendata.bordeaux.fr/content/licence-ouverte")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/language"), NodeFactory.createURI("http://purl.org/dc/terms/language"), NodeFactory.createURI("\"Langue : Français\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"06/05/2015\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://databordeaux.blob.core.windows.net/data/dref/budgetnomenc2003.xls")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://databordeaux.blob.core.windows.net/data/doc/vbdx_notice.pdf")));
        CrawleableUri curi = new CrawleableUri(new URI("http://opendata.bordeaux.fr/content/adresses-gravees"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/opendata_bordeaux/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageOpendataOpennorth() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://opendata.opennorth.se/dataset")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://projekt-opal.de/dataset#pagination"), NodeFactory.createURI("http://opendata.opennorth.se/dataset/energy-water-consumption-properties-skelleftea")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("http://purl.org/dc/terms/modified"), NodeFactory.createURI("\"March 13, 2019, 08:59 (UTC)\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"April 13, 2016, 08:38 (UTC)\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://purl.org/dsnotify/vocab/eventset/sourceDataset"), NodeFactory.createURI("http://wiki.opennorth.se/index.php/Metadata/energy-water-consumption-properties-skelleftea")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://openumea-storage.s3.amazonaws.com/2016-04-18T13:35:47/energy_water_skelleftea.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://openumea-storage.s3.amazonaws.com/2016-05-01T16:02:06/energy_water_skelleftea.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://openumea-storage.s3.amazonaws.com/2016-06-01T16:02:14/energy_water_skelleftea.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://openumea-storage.s3.amazonaws.com/2016-09-01T16:02:05/energy_water_skelleftea.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://openumea-storage.s3.amazonaws.com/2016-10-01T16:02:28/energy_water_skelleftea.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://openumea-storage.s3.amazonaws.com/2016-11-01T17:02:18/energy_water_skelleftea.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("https://openumea-storage.s3.amazonaws.com/2017-09-22T06:26:27/energy_water_skelleftea.csv")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://ckan.openumea.se/dataset/ea9d210a-ba7d-48c5-8b71-83cd0ab67bcd/resource/d6b59b76-c83c-42ed-b50a-189e2d7ea5b2/download/energy_water_skelleftea.csv")));
        CrawleableUri curi = new CrawleableUri(new URI("http://opendata.opennorth.se/dataset/customer-service-errands-skelleftea"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/opendata_opennorth/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void scrapeDetailPageGeonodeMsri() throws Exception {
        List<Triple> expectedTriples = new ArrayList<>();
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createURI("\"Forest density in Kyrgyzstan\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("http://purl.org/dc/terms/description"), NodeFactory.createURI("\"Forest density in Kyrgyzstan, data from Global Forest Watch\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://purl.org/dc/terms/publisher"), NodeFactory.createURI("http://geonode.msri.io/people/profile/maksim.kulikov/")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("http://purl.org/dc/terms/issued"), NodeFactory.createURI("\"June 8, 2018, 2:38 a.m.\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#accessURL"), NodeFactory.createURI("\"Download Metadata\"")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/geoserver/gwc/service/gmaps?layers=geonode:forests&zoom={z}&x={x}&y={y}&format=image/png8")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/geoserver/wms/kml?layers=geonode%3Aforests&mode=refresh")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/geoserver/wms/kml?layers=geonode%3Aforests&mode=download")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/geoserver/wcs?format=image%2Ftiff&request=GetCoverage&version=2.0.1&service=WCS&coverageid=geonode%3Aforests")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/geoserver/wcs?format=application%2Fx-gzip&request=GetCoverage&version=2.0.1&service=WCS&coverageid=geonode%3Aforests")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/geoserver/wms?layers=geonode%3Aforests&width=1539&bbox=67.9995833333%2C38.999583212199994%2C82.0004177867%2C44.000416945599994&service=WMS&format=image%2Fpng&srs=EPSG%3A4326&request=GetMap&height=550")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/geoserver/wms?layers=geonode%3Aforests&width=1539&bbox=67.9995833333%2C38.999583212199994%2C82.0004177867%2C44.000416945599994&service=WMS&format=application%2Fpdf&srs=EPSG%3A4326&request=GetMap&height=550")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/geoserver/wms?layers=geonode%3Aforests&width=1539&bbox=67.9995833333%2C38.999583212199994%2C82.0004177867%2C44.000416945599994&service=WMS&format=image%2Fjpeg&srs=EPSG%3A4326&request=GetMap&height=550")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/catalogue/csw?outputschema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/catalogue/csw?outputschema=http%3A%2F%2Fwww.opengis.net%2Fcat%2Fcsw%2Fcsdgm&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/catalogue/csw?outputschema=urn%3Aoasis%3Anames%3Atc%3Aebxml-regrep%3Axsd%3Arim%3A3.0&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/catalogue/csw?outputschema=http%3A%2F%2Fwww.opengis.net%2Fcat%2Fcsw%2F2.0.2&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/catalogue/csw?outputschema=http%3A%2F%2Fgcmd.gsfc.nasa.gov%2FAboutus%2Fxml%2Fdif%2F&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")));
        expectedTriples.add(new Triple(NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://www.w3.org/ns/dcat#downloadURL"), NodeFactory.createURI("http://31.186.50.220/catalogue/csw?outputschema=http%3A%2F%2Fwww.w3.org%2F2005%2FAtom&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")));
        CrawleableUri curi = new CrawleableUri(new URI("http://geonode.msri.io/layers/geonode%3Aforests#more"));
        fetchedFile = new File("src/test/resources/html_scraper_analyzer/geonode_msri/details_page.html");

        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(scraper.scrape(curi, fetchedFile));

        for (int i=0; i<expectedTriples.size(); i++){
            Triple expected = expectedTriples.get(i);
            Triple actual = listTriples.get(i);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }
}
