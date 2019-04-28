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
public class GeonodeMsriTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriDetailsPage = "http://geonode.msri.io/layers/geonode%3Aforests#more";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/geonode_msri/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Forest density in Kyrgyzstan"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Forest density in Kyrgyzstan, data from Global Forest Watch"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new ResourceImpl("http://geonode.msri.io/people/profile/maksim.kulikov/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("June 8, 2018, 2:38 a.m."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.accessURL.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Download Metadata"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/geoserver/gwc/service/gmaps?layers=geonode:forests&zoom={z}&x={x}&y={y}&format=image/png8")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/geoserver/wms/kml?layers=geonode%3Aforests&mode=refresh")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/geoserver/wms/kml?layers=geonode%3Aforests&mode=download")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/geoserver/wcs?format=image%2Ftiff&request=GetCoverage&version=2.0.1&service=WCS&coverageid=geonode%3Aforests")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/geoserver/wcs?format=application%2Fx-gzip&request=GetCoverage&version=2.0.1&service=WCS&coverageid=geonode%3Aforests")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/geoserver/wms?layers=geonode%3Aforests&width=1539&bbox=67.9995833333%2C38.999583212199994%2C82.0004177867%2C44.000416945599994&service=WMS&format=image%2Fpng&srs=EPSG%3A4326&request=GetMap&height=550")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/geoserver/wms?layers=geonode%3Aforests&width=1539&bbox=67.9995833333%2C38.999583212199994%2C82.0004177867%2C44.000416945599994&service=WMS&format=application%2Fpdf&srs=EPSG%3A4326&request=GetMap&height=550")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/geoserver/wms?layers=geonode%3Aforests&width=1539&bbox=67.9995833333%2C38.999583212199994%2C82.0004177867%2C44.000416945599994&service=WMS&format=image%2Fjpeg&srs=EPSG%3A4326&request=GetMap&height=550")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/catalogue/csw?outputschema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/catalogue/csw?outputschema=http%3A%2F%2Fwww.opengis.net%2Fcat%2Fcsw%2Fcsdgm&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/catalogue/csw?outputschema=urn%3Aoasis%3Anames%3Atc%3Aebxml-regrep%3Axsd%3Arim%3A3.0&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/catalogue/csw?outputschema=http%3A%2F%2Fwww.opengis.net%2Fcat%2Fcsw%2F2.0.2&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/catalogue/csw?outputschema=http%3A%2F%2Fgcmd.gsfc.nasa.gov%2FAboutus%2Fxml%2Fdif%2F&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://31.186.50.220/catalogue/csw?outputschema=http%3A%2F%2Fwww.w3.org%2F2005%2FAtom&service=CSW&request=GetRecordById&version=2.0.2&elementsetname=full&id=e279bbae-6aee-11e8-9d66-000c29ef5152")
                )
            )
        });
        return testConfigs;
    }

    public GeonodeMsriTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
