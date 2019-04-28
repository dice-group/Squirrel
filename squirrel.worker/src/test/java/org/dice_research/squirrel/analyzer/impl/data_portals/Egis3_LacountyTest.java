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
public class Egis3_LacountyTest extends AbstractDataPortalTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "https://egis3.lacounty.gov/dataportal/data-catalog/";
        String uriDetailsPage = "http://egis3.lacounty.gov/dataportal/2011/01/27/county-lighting-maintenance-district/";

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/egis3_lacounty/egis3_lacounty_detail.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("County Lighting Maintenance District"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Abstract: County Lighting Maintenance District shows the boundaries where Los Angeles County Traffic & Lighting Division maintains the street lights. Traffic and Lighting is a Division of the Los Angeles County Department of Public Works. GIS data available on this website is provided AS IS – please read the GIS Data Portal Terms of Use Download Data last updated: 10/02/2017"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Thierno Diallo Geographic Information System Manager I County of Los Angeles Department of Public Works Survey/Mapping and Property Management Division 900 S. Fremont Ave Alhambra, CA 91803 tdiallo@dpw.lacounty.gov 626-458-6920"), null)
                )
            )
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/egis3_lacounty/egis3_lacounty_datacatalog.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://egis3.lacounty.gov/dataportal/comments/feed/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://egis3.lacounty.gov/dataportal/feed/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/03/15/la-county-cams-address-route-markers-landmarks/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/06/19/la-county-address-points/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/06/16/2011-la-county-street-centerline-street-address-file/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/08/11/zip-codes-with-parcel-specific-boundaries/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/11/30/elementary-middle-and-high-school-attendance-areas-2002/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/01/20/school-district-boundaries-2011/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://www.firepreventionfee.org/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/06/06/california-coastal-commission-zone-boundaries/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/06/09/cdc-enterprise-zone/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/06/09/cdc-project-areas/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/01/15/dcfs-office-service-area/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/01/27/dcfs-office-service-areas-by-zip-codes/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/02/23/medical-service-study-areas-2013/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/09/28/health-district-hd-2002/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/03/01/health-districts-hd-2012/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2009/11/07/service-planning-areas-spa-2002/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/03/01/service-planning-areas-spa-2012/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/02/16/park-planning-areas/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/07/17/dpss-program-service-area-boundaries/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/11/building-and-safety-district-boundaries/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/flood-maintenance-district-boundary/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/garbage-disposal-district-boundary/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/county-lighting-maintenance-district/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/los-angeles-county-road-maintenance-district-boundary/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/water-purveyor-service-areas/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/10/29/land-use-policy-commarea-plan/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/05/15/catalina-zoning-land-use-districts/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/11/13/community-standards-districts/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/03/30/community-standards-districts/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/12/29/town-council-areas/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/24/equestrian-districts-eqd/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/10/29/countywide-land-use-policy-not-in-commarea-plan-2011/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/01/09/planning-areas/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/10/29/rural-outdoor-lighting-district-dark-skies/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/10/29/significant-ridgelines/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/06/03/specific-plans/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/04/30/subdivisions/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/24/transit-oriented-districts-tod/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/10/29/zoned-districts-zd/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/03/30/zoning-unincorporated-areas-only/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/04/28/los-angeles-county-fire-department-battalion-boundaries/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/04/28/los-angeles-county-fire-department-division-boundaties/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/04/28/los-angeles-county-fire-department-region-boundaries/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/21/la-city-communities-and-planning-areas/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/12/14/disaster-management-areas/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/19/probation-adult-district/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/?p=7465&preview=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/03/04/regional-centers-2014/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/07/16/la-county-sanitation-districts-gis-page/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/04/10/countywide-zoning/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/03/04/public-safety-answering-point-boundaries/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/?p=1016&preview=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/10/05/law-enforcement-reporting-districts/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/?p=984&preview=true")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/06/03/pest-detection-statewide-trapping-grid-stg-quints/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/06/03/pest-detection-statewide-trapping-grid-stg-grids/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/02/11/township-range-and-section/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/index-map-grid/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/house-numbering-map-grid/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/11/07/los-angeles-county-sanitary-sewers/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/sewer-maintenance-operations-map-sub-grid/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/24/zoning-index-map-grid/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/09/05/zoning-map-grid/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/11/21/township-range-section-rancho-boundaries/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/26/us-national-grid/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/03/15/los-angeles-county-fire-department-jurisdictional-2000-meter-grid/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/04/29/los-angeles-county-fire-department-map-index-grid/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/04/23/historic-maps-of-los-angeles-city-1849-1908/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/12/28/lariac_data_files/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/02/18/rrcc-map-index-2/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/02/18/rrcc-precinct-map-index/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/08/thomas-brothers-page/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/08/thomas-brothers-page-and-grid/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/04/21/usgs-quad-map-grids/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/12/10/parcel-lines/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/03/10/assessor-parcel/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/30/tax-rate-area-table-2014-auditor-controller/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/10/14/tax-rate-area-table-2015-auditor-controller/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/05/23/marina-del-rey-lease-parcels-2/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/11/california-statewide-parcel-boundaries/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/17/2011-redistricting-data/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/26/us-census-summary-file-1-2010/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/26/census_block_groups/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/26/census_blocks/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/08/2010-census-blocks-age/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/26/census_designated_places/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/26/census_tracts/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/08/2010-census-tracts-median-household-income/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/08/2010-census-tracts-sex-by-age/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/02/23/estimated-prevalence-of-serious-mental-illness/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/26/census-tract-city-split-areas-aka-split-tracts-file/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/09/09/population-and-poverty-estimates/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/03/05/crime-data-la-county-sheriff/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/04/09/2006-2-foot-contours-lar-iac/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/12/23/height-raster-dataset-2006/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/19/2006-5-foot-1-7-m-digital-surface-model-dsm/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/wp-admin/post.php?post=526&action=edit")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/17/la-county-bathymetry-data/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/12/29/active-wells/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/soil-types/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/08/06/sensitive-environmental-resource-areas-sera/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/11/19/significant-ecological-areas-sea/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/12/23/normalized-difference-vegetation-index-ndvi-2006/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/12/23/tree-canopy-raster-2006-data/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/12/23/solar-radiation-model-2006/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/04/07/solar-data-summarized-to-2010-parcels/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/06/26/seismic-hazards-la-county-from-state-of-ca/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/12/28/flood-insurance-rate-maps-firms/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/12/28/flood-zones/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/04/28/fire-perimeters-1965-2013/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/05/28/fire-hazard-severity-zones/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/12/30/dam-inundation-and-dam-inundation-e-t-a/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/12/30/tsunami-inundation-and-tsunami-inundation-lines/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/08/08/los-angeles-county-storm-drain-system/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/11/debris-basin-point-location/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/ground-water-basins/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/11/spreading-grounds/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/05/09/hydrologic-points/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/05/09/watershed-boundaries/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/05/09/rivers-streams-water-conveyance-pipelines-aqueducts/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/12/23/1094/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/19/2006-1-foot-orthophotography-national-forest/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/19/2006-4-inch-orthophotography/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/19/2006-4-inch-color-infrared-cir-orthophotography/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/19/2008-1-foot-orthophotography-national-forest/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/06/16/2010-6-inch-post-fire-orthoimagery/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/04/16/2011-4-inch-color-orthophotography/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/05/09/2011-4-inch-and-1-foot-orthophoto-imagery-date-flown/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/lariac/lariac-archives/lariac3-archive/lariac3-downloads/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/08/24/2014-4-inch-color-orthophotography-lariac4/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/lariac/lariac-data/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/11/24/naip-national-agriculture-imagery-program-imagery/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/06/29/marina-del-rey-docks-2/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/06/29/marina-del-rey-boat-slips/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/10/27/cdc-offices/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/10/27/cdc-public-housing-sites-points/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/10/27/cdc-public-housing-sites-polygons/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/03/15/county-facilities-from-the-ceo-building-data-record-bdr-database-2008/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/03/19/3d-models-of-county-owned-buildings/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/05/22/la-county-department-of-mental-health-providers/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/04/department-of-public-works-filed-facilities-points/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/27/street-lights/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/01/08/la-county-land-types/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/14/locationspoints-of-interest-lms-data/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/02/22/la-county-land-types/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/05/23/los-angeles-county-fire-hydrant-layer/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/10/16/countywide-building-outlines-2014/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/25/parking-lot-boundaries-commercial-industrial-government-2014/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/19/2006-buildings-2-foot-raster/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/19/npms-pipelines-and-npms-breakout-tanks-in-l-a-county/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/07/15/port-of-los-angeles-berths-docks-slips/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/17/usgs-geographic-names-information-system-gnis/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/12/03/split-2010-block-groupcity-basa/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/12/03/board-approved-statistical-areas-communities-final-draft/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/06/11/city-annexations/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/11/06/los-angeles-county-boundary/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/09/28/historic-la-county-supervisorial-districts-1971-1991/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2009/12/23/supervisorial-districts-2001/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/12/06/supervisorial-districts/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/11/13/national-forest/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/05/02/la-city-council-districts/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/08/07/la-city-council-districts-2012/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/12/20/2012-precincts-as-of-march-9th/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/11/08/california-state-assembly-districts-2011/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/11/08/california-board-of-equalization-districts-2011/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/11/08/california-state-senate-districts-2011/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/01/14/us-congressional-districts/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/10/30/dcfs-regional-offices/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/04/12/los-angeles-county-hospitals-2011/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/04/09/los-angeles-county-department-of-health-services-community-partners/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/04/12/los-angeles-county-department-of-health-services-facilities/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/02/23/publicly-funded-mental-health-providers/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/17/child-care-provider-facilities/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/09/16/dpss-office-locations/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/?p=7468&preview=true"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/19/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/10/31/polling-places-november-6-2012-election/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2014/02/04/business-locations/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/20/freeways-single-line-for-labels/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/?p=6417"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/01/25/existing-county-trail-access-points-2010/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2015/12/30/department-of-parks-and-recreation-trails-2015/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/06/06/trail-related-facilities/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/05/29/bike-paths/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/25/airport-land-use-commission-aluc-layers/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2012/11/26/master-plan-of-highways/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/01/29/airport-runways/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2013/07/05/southern-california-railroad-mileposts-with-sheriff-reporting-districts/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/03/02/metrolink-lines-with-sheriff-reporting-districts/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2011/03/02/metro-gis-data/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/03/15/mta-park-ride-locations/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2016/01/19/disaster-routes/"))
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Data Catalog"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("The table below lists all of the GIS Data Los Angeles County has identified in its systems, compiled and organized, with active links to where the data can be downloaded. This should make it easier to find and access our data. Data is maintained through the eGIS Data Maintenance Policies and Procedures which ensures minimal standard information is provided for each of the datasets. Current list of LA County GIS Data (May 3rd, 2016) Download Excel File. THEME (Category) DATA SOURCE Dataset Name and Data Portal Access Addressing CAMS CAMS Landmarks Addressing CAMS CAMS point addresses Addressing CAMS CAMS Raw line data Addressing CAMS CAMS Route Markers Addressing CAMS ZIP Codes (Parcel Specific) Addressing CAMS ZIP Codes (Street Specific) Addressing FIRE Fire GeoFile for Dispatching (not available for download) Addressing USPS Zipcode Points Administrative Boundaries BOE School Attendance Areas: Elementary Administrative Boundaries BOE School Attendance Areas: High School Administrative Boundaries BOE School Attendance Areas: Middle School Administrative Boundaries BOE School Districts Administrative Boundaries CA SRA (state responsiblilty area) Administrative Boundaries CALTRANS California Coastal Commission Zones Administrative Boundaries CDC Enterprise Zones Administrative Boundaries CDC Project Area Boundaries Administrative Boundaries DCFS DCFS Office Service Areas Administrative Boundaries DCFS DCFS Office Service Areas by Zipcode Administrative Boundaries DMH Medical Service Study Areas (MSSA) 2013 Administrative Boundaries DPH Health Districts (1990) Administrative Boundaries DPH Health Districts (2002) Administrative Boundaries DPH Health Districts (2012) Administrative Boundaries DPH Service Planning Areas (SPA) – 1994 Administrative Boundaries DPH Service Planning Areas (SPA) – 2002 Administrative Boundaries DPH Service Planning Areas (SPA) – 2012 Administrative Boundaries DPR Park Planning Areas Administrative Boundaries DPSS DPSS CalFresh Approved Intake Service Area Boundaries (Adult) Administrative Boundaries DPSS DPSS CalFresh Approved Intake Service Area Boundaries (Family) Administrative Boundaries DPSS DPSS CalFresh Approved Service Area Boundaries Administrative Boundaries DPSS DPSS CalFresh Approved Service Area Boundaries (Family) Administrative Boundaries DPSS DPSS CalWorks Approved Intake Service Area Boundary (Refugee) Administrative Boundaries DPSS DPSS CalWorks Approved Intake Service Area Boundary (Refugee) Administrative Boundaries DPSS DPSS CalWorks Approved Service Area Boundary Administrative Boundaries DPSS DPSS CalWorks Approved Service Area Boundary (Refugee) Administrative Boundaries DPSS DPSS CAPI Administrative Boundaries DPSS DPSS CAPI Administrative Boundaries DPSS DPSS GAIN Administrative Boundaries DPSS DPSS GAIN Administrative Boundaries DPSS DPSS General Relief Administrative Boundaries DPSS DPSS General Relief Administrative Boundaries DPSS DPSS GROW Administrative Boundaries DPSS DPSS GROW Administrative Boundaries DPSS DPSS IHSS Administrative Boundaries DPSS DPSS MEDI-CAL Approved Adult Area Boundaries Administrative Boundaries DPSS DPSS MEDI-CAL Approved Family Area Boundaries Administrative Boundaries DPSS DPSS MEDI-CAL Intake Adult Area Boundaries Administrative Boundaries DPSS DPSS MEDI-CAL Intake Family Area Boundaries Administrative Boundaries DPW Building and Safety District Boundaries Administrative Boundaries DPW County Flood Maintenance Districts Administrative Boundaries DPW County Garbage Disposal Districts Administrative Boundaries DPW County Lighting Maintenance Districts Administrative Boundaries DPW Road Maintenance District Boundaries 03/05/2007 Administrative Boundaries DPW Water Purveyor Service Areas 03/05/2007 Administrative Boundaries DRP Agricultural Opporuntunity Areas Administrative Boundaries DRP Area/community/neighborhood plan land use policy Administrative Boundaries DRP Catalina Zoning Administrative Boundaries DRP Community Standards Districts (CSD) & Sub Areas Administrative Boundaries DRP Community Standards Districts (CSD) Sub Areas Administrative Boundaries DRP DRP Town Council Areas Administrative Boundaries DRP Equestrian Districts (EQD) Administrative Boundaries DRP Land use policy (areas in 1980 general plan) Administrative Boundaries DRP Planning Areas Administrative Boundaries DRP Rural Lighting Districts (Dark Skies) Administrative Boundaries DRP Significant Ridgelines (within CSDs) Administrative Boundaries DRP Specific Plans (Four in Unincorporated Areas) Administrative Boundaries DRP Subdivision Activity (Unincorporated Areas Only) Administrative Boundaries DRP Transit Oriented Districts (TOD) Administrative Boundaries DRP Zoned Districts (ZD) Administrative Boundaries DRP Zoning (unincorporated only) Administrative Boundaries FIRE Battalion Boundaries (as of 2016) Administrative Boundaries FIRE County Fire Divisions 2016 Administrative Boundaries FIRE County Fire Regions 2016 Administrative Boundaries LACITY City of LA Community Plan Areas Administrative Boundaries Library County Library 2000 Service Areas (not available for download) Administrative Boundaries LIBRARY County Library 2020 Service Areas (not available for download) Administrative Boundaries LIBRARY County Library clusters (not available for download) Administrative Boundaries Library County library planning areas (not available for download) Administrative Boundaries LIBRARY County Library regions (not available for download) Administrative Boundaries LIBRARY County Library service area characteristics (not available for download) Administrative Boundaries LIBRARY Library Fee Area Boundaries (not available for download) Administrative Boundaries LIBRARY Library Planning Areas (not available for download) Administrative Boundaries LIBRARY Library Service Areas (not available for download) Administrative Boundaries OEM Disaster Management Areas Administrative Boundaries PROBATION Adult Probation Districts Administrative Boundaries PROBATION Juvenile Probation Districts Administrative Boundaries REGIONAL_CENTERS Los Angeles County Regional Center Service Area Boundaries (2014) Administrative Boundaries SANITATION Sanitation Districts boundaries Administrative Boundaries SCAG SCAG Countywide Zoning Administrative Boundaries SCAG SCAG Existing Land Use (2001, 1993 and 1990, 2005) Administrative Boundaries SHERIFF Law Enforcement Public Safety Answering Points (PSAPs) Administrative Boundaries SHERIFF Law Enforcement Station Boundaries Administrative Boundaries SHERIFF Parks Bureau Reporting Districts Administrative Boundaries SHERIFF Reporting Districts: Sheriff and Police Administrative Boundaries SHERIFF Sheriff Court Service Branches Basemaps and Grids ACWM Pest Detection – Statewide Trapping Grid (STG) Grids Basemaps and Grids ACWM Pest Detection – Statewide Trapping Grid (STG) Quints Basemaps and Grids ASSR Assessor Book and Page Boundaries (not available for download) Basemaps and Grids CA CA Public Land Survey System (Township Range Section and Ranchos) Basemaps and Grids DPW DPW Index Map Grid Basemaps and Grids DPW House Numbering Map Grid Index Basemaps and Grids DPW Sewer Maintenance Operations Map Grid Basemaps and Grids DPW Sewer Maintenance Operations Map Sub Grid Basemaps and Grids DRP Township, Range, Section Lines Basemaps and Grids DRP Zoning Index Map Grid Basemaps and Grids DRP Zoning Map Grid Basemaps and Grids EGIS Township, Range, Section, Rancho grid boundaries Basemaps and Grids EGIS USNG – United States National Grid 10000m Basemaps and Grids EGIS USNG – United States National Grid 1000m Basemaps and Grids EGIS USNG – United States National Grid 100km Basemaps and Grids EGIS USNG – United States National Grid 100m Basemaps and Grids FIRE County Map Grid Basemaps and Grids FIRE Fire Index Map Grid Basemaps and Grids LA Los Angeles, CA Historic Map, 1849 – Raster Image Basemaps and Grids LA Los Angeles, CA Historic Map, 1908 – Raster Image Basemaps and Grids LARIAC1 LARIAC1 Collection Areas Basemaps and Grids LARIAC1 LARIAC1 Date Imagery Flown Polygon Basemaps and Grids LARIAC1 LARIAC1 Oblique imagery footprints – community and neighborhood Basemaps and Grids LARIAC1 LARIAC1 Pictometry (oblique) sector grid Basemaps and Grids LARIAC1 LARIAC1 Tile grid Basemaps and Grids LARIAC2 LARIAC2 Mosaic Imagery boundaries Basemaps and Grids LARIAC2 LARIAC2 4″ grid Basemaps and Grids LARIAC2 LARIAC2 Delivery Grid Basemaps and Grids LARIAC2 LARIAC2 Ortho Seamlines Basemaps and Grids LARIAC2 LARIAC2 Pictometry (oblique) sector grid Basemaps and Grids RRCC RRCC Index Map Grid Basemaps and Grids RRCC RRCC Precinct Map Grid Basemaps and Grids TB Thomas Brothers Page Boundary Basemaps and Grids TB Thomas Brothers Page Grid Basemaps and Grids USGS USGS Quad Sheet Grid (not available for download) Basemaps and Grids USGS USGS Quad Sheet Grid – United States Basemaps and Grids USGS USGS Quadrangle Maps (not available for download) Cadastral (Property) ASSR Parcel lines (right-of-way, cut/deed, easement, lot line, etc.) Cadastral (Property) ASSR Parcel Polygons – Current Cadastral (Property) ASSR Tax Rate Areas (TRA) Polygons (not available for download) Cadastral (Property) ASSR Vacant Parcels (not available for download) Cadastral (Property) AUDITOR Tax Rate Areas (TRA) Table – 2014 Cadastral (Property) AUDITOR Tax Rate Areas (TRA) Table – 2015 Cadastral (Property) BEACHES Marina del Rey Leased Parcels Cadastral (Property) CA Statewide Parcel data Cadastral (Property) SCAG SCAG Zoning (Countywide) Cadastral (Property) TTC Tax Defaulted Properties (2012) (not available for download) Cadastral (Property) TTC Tax Defaulted Properties (2013) (not available for download) Cadastral (Property) TTC Tax Defaulted Properties (2015) (not available for download) Demography BOS Redistricting Demographic Data Demography CDC CDC Low Mod CT10 (not available for download) Demography CENSUS Census (2010) Summary File 1 Table Demography CENSUS Census Block Groups (2000) Demography CENSUS Census Block Groups (2010) Demography CENSUS Census Block Groups (2010) – Original Demography CENSUS Census Blocks (2000) Demography CENSUS Census Blocks (2010) Demography CENSUS Census Blocks (2010) – Age Demography CENSUS Census Designated Places (1990) Demography CENSUS Census Designated Places (2000) Demography CENSUS Census Designated Places (2010) Demography CENSUS Census Tracts (1990) Demography CENSUS Census Tracts (2000) Demography CENSUS Census Tracts (2010) Demography CENSUS Census Tracts (2010) – Original Demography CENSUS Census Tracts (2010) Lookup Table (not available for download) Demography CENSUS Census Tracts (2010) Spatial View (not available for download) Demography CENSUS Census Tracts Medium HH Income (2010) Demography CENSUS Census Tracts Sex by Age (2010) Demography DMH Department of Mental Health Penetration rate (client served) (not available for download) Demography DMH Estimated Prevalence of Serious Mental Illness Demography EGIS Census Tracts/City Split Areas (2010) Demography EGIS Census Tracts/City Split Areas (2014) Demography EGIS Population Estimates Trends by Census Tract (2010) Demography EGIS Poverty Estimates by Census Tract (2010) Demography EGIS Poverty Estimates by Race by Census Tract (2013) Demography EGIS Poverty Estimates by Race by Census Tract/City Split (2010) Demography EGIS Poverty Estimates by Race by Census Tract/City Split (2011) Demography EGIS Poverty Estimates by Race by Census Tract/City Split (2012) Demography EGIS Poverty Estimates by Race by Census Tract/City Split (2013) Demography SHERIFF Crime Data – locations moved for anonymity (2005 – 2014) Elevation EGIS Dummy Elevation Points for Profile Tool (not available for download) Elevation LARIAC Contours – 1000ft interval Elevation LARIAC Contours – 10ft interval Elevation LARIAC Contours – 250ft interval Elevation LARIAC Contours – 2ft interval Elevation LARIAC Contours – 50ft interval Elevation LARIAC Height Model from LARIAC – 5 Foot Elevation LARIAC LAR-IAC Hillshade – 5 foot Elevation LARIAC LAR-IAC Hillshade/Landcover combination (not available for download) Elevation LARIAC LAR-IAC Hillshade/Landcover combination – clipped to County (not available for download) Elevation LARIAC1 LARIAC1 Digital Elevation Model (DEM) – 5 Foot Elevation LARIAC1 LARIAC1 Digital Surface Model (DSM) – 5 Foot Elevation USGS USGS Backscatter Elevation USGS USGS Bathymetry Contour Lines 50ft Elevation USGS USGS Bathymetry Data Elevation USGS USGS Bathymetry Hillshade Elevation USGS USGS Bathymetry Relief Environmental CIO Average Costs for Electricity by Utility (not available for download) Environmental DPW Active Wells Environmental DPW Soil Types Environmental DPW Watersheds (not available for download) Environmental DRP Sensitive Environmental Resource Areas (SERA) Environmental DRP Significant Ecological Areas (SEA) – Existing/Adopted Environmental LARIAC Normalized Difference Vegetation Index Environmental LARIAC Raster Model of Trees Environmental LARIAC Solar Global Insolation Model – (100 foot grid) Environmental LARIAC Solar Global Insolation Model – (5 foot grid) Environmental SOLAR Facility_Centroids (not available for download) Environmental SOLAR Solar Data summarized to 2010 Parcels Environmental SOLAR Solar Installation Summary by Zipcode (not available for download) Environmental SOLAR Solar Installations (not available for download) Hazards CA Seismic – Fault Trace Hazards CA Seismic – Fault Zone Hazards CA Seismic – Landslide Hazards CA Seismic – Liquefaction Hazards FEMA FIRM Floodplain (Q3 data) Hazards FEMA FIRM Panels Hazards FEMA Flood Zones Hazards FIRE Historic Burn area Hazards FIRE Very High Fire Hazard Severity Zones (VHFHSZ) Hazards OEM Dam Inundation (not available for download) Hazards OEM Dam Inundation Est. Time of Arrival Hazards OEM Tsunami Inundation Hazards OEM Tsunami Inundation Line Hydrology (Water) DPW Abandoned Drains Hydrology (Water) DPW Approved connections, privately built, flowing into County maintained drains Hydrology (Water) DPW Catch Basins (not available for download) Hydrology (Water) DPW Catch Basins – collect urban run off from gutters Hydrology (Water) DPW Culverts in LA County Hydrology (Water) DPW Dams Hydrology (Water) DPW Debris Basin Points Hydrology (Water) DPW Devices used to keep solid waste from entering catch basins – Not currently used Hydrology (Water) DPW DPW Storm Drain Network Hydrology (Water) DPW Force mains – carry water uphill from pump stations into gravity mains Hydrology (Water) DPW Gates – located at channel inlets and used to regulate drain flow Hydrology (Water) DPW GroundWater_Basins Hydrology (Water) DPW Inlet and outlet points along a culvert Hydrology (Water) DPW Inlets and Outlets along an open Channel Hydrology (Water) DPW Lateral lines – connect catch basins to underground gravity mains or channels Hydrology (Water) DPW Linework of Major Channels (not available for download) Hydrology (Water) DPW Locations of concrete collars used for connecting two pipes end to end. Hydrology (Water) DPW Locations where storm water enters the ocen from gravity mains Hydrology (Water) DPW Low flow points along a low flow line Hydrology (Water) DPW Maintenance locations where inspectors can enter gravity mains Hydrology (Water) DPW nuous Deflective Separation (CDS) units trap pollutants underground for later removal. Hydrology (Water) DPW Open Channels carry stormwater and are open to the surface Hydrology (Water) DPW Openings where water enters into or exits out of a channel Hydrology (Water) DPW Pipes or drains not part of the network Hydrology (Water) DPW Points on a lateral line Hydrology (Water) DPW Points on a pseudo line Hydrology (Water) DPW Points that cannot be added to other feature classes Hydrology (Water) DPW Polygons of major channels (for cartography) (not available for download) Hydrology (Water) DPW Pump Stations are used to pump stormwater through a force main into a gravity main Hydrology (Water) DPW Reservoirs, Debris Basins, etc Hydrology (Water) DPW Spreading Grounds operated by LADPW Hydrology (Water) DPW Storm drains that do not run to their full capacity at any time Hydrology (Water) DPW Streams and rivers that flow through natural creek beds Hydrology (Water) DPW Structures that route stormwater runoff through sewage treatment facilities during non-storm periods Hydrology (Water) DPW Temporarily used feature class to help edit other polyline layers Hydrology (Water) DPW Transitions between different types of gravity mains Hydrology (Water) DPW Underground pipes and channels carrying storm water Hydrology (Water) DPW used for the geometric network, typcially connecting a channel opening to the flow line in a channel Hydrology (Water) DPW Walls along channels that prevent water overflowing Hydrology (Water) DPW Where two or more pipe segments meet Hydrology (Water) EGIS Ocean and County Boundaries for Cartography Hydrology (Water) NHD Hydrologic Points Hydrology (Water) NHD Hydrologic Units (Level 10) Hydrology (Water) NHD Hydrologic Units (Level 12) Hydrology (Water) NHD Hydrologic Units (Level 8) Hydrology (Water) NHD Streams Hydrology (Water) NHD Water Bodies Imagery EMERGE Emerge 1 foot (2001) Imagery EMERGE Emerge 1 meter (2001) Imagery EMERGE Emerge 2 meter (2001) Imagery LARIAC LAR-IAC 1′ color orthos (2006) Imagery LARIAC LAR-IAC 1′ NF color orthos (2006) Imagery LARIAC LAR-IAC 4″ color orthos (2006) Imagery LARIAC1 LARIAC1″ CIR orthos (2006) Imagery LARIAC2 LARIAC2 4″ color orthos (2008) Imagery LARIAC2 LARIAC2 4″ color orthos (2008) – lean fixes Imagery LARIAC2 LARIAC2 6″ color orthos (2008) – Station Fire Burn Area Imagery LARIAC3 LARIAC3 1 Foot National Forest color orthos (2011) Imagery LARIAC3 LARIAC3 4″ color orthos (2011) Imagery LARIAC3 LARIAC3 4″ color orthos (2011) Imagery LARIAC3 LARIAC3 Date Flown Boundary Imagery LARIAC3 LARIAC3 Ground Control Points Imagery LARIAC3 LARIAC3 Oblique imagery footprints (2011) Imagery LARIAC3 LARIAC3 Ortho Seamlines Imagery LARIAC4 LARIAC4 – 2012 1 foot imagery (not available for download) Imagery LARIAC4 LARIAC4 – 2012 1 foot imagery footprint (not available for download) Imagery LARIAC4 LARIAC4 – 2012 Fixed imagery (not available for download) Imagery LARIAC4 LARIAC4 – 2013 1 foot imagery (not available for download) Imagery LARIAC4 LARIAC4 1 Foot National Forest color orthos (2014) Imagery LARIAC4 LARIAC4 4″ color orthos (2014) Imagery LARIAC4 LARIAC4 Ortho Date Flown Boundaries Imagery LARIAC4 LARIAC4 Ortho Imagery Tile Grid Imagery LARIAC4 LARIAC4 Ortho Seamlines Imagery USDA NAIP Imagery – 2014 Infrastructure ACWM LMS Data for ACWM (not available for download) Infrastructure BEACHES Marina del Rey Docks Infrastructure BEACHES Marina del Rey Slips Infrastructure CDC CDC Offices Infrastructure CDC Public Housing Sites (Points) Infrastructure CDC Public Housing Sites (Polygons) Infrastructure CEO_RED LA County Facilities Infrastructure CIO 3D Models of County-owned buildings Infrastructure CIO County Owned Buildings (2D) Infrastructure DHS LMS Data for DHS (not available for download) Infrastructure DMH LMS Data for DMH Infrastructure DPW Public Works Facilities Infrastructure DPW Sewer Manhole Infrastructure DPW Sewer Non SMD Manhole Infrastructure DPW Sewer Non SMD Pipe Infrastructure DPW Sewer Operation Map Grid Infrastructure DPW Sewer Pipe Infrastructure DPW Sewer Pump Station Infrastructure DPW Sewer WWTP Infrastructure DPW Street Lights Infrastructure EGIS Land Type Data (Variety of Polygons) Infrastructure EGIS Location Management System Data (LMS) – point features Infrastructure EGIS Polygons of Amusement Parks Infrastructure FIRE Fire Hydrants in County jurisdiction Infrastructure LARIAC Countywide Building Outlines (2008) Infrastructure LARIAC Countywide Building Outlines (2014) Infrastructure LARIAC Countywide Building Outlines (2014) – deleted from 2008 Infrastructure LARIAC Parking Lots (Commercial, Industrial – non-residential) Infrastructure LARIAC1 LARIAC1 Raster Building Footprints Infrastructure MANHATTAN BEACH Manhattan Beach Pier (not available for download) Infrastructure OEM NPMS Breakout Tanks Infrastructure OEM NPMS Pipelines Infrastructure PARKS LMS Data for Parks (not available for download) Infrastructure PARKS LMS Data for Parks – 1 POINT (not available for download) Infrastructure PARKS LMS Data for Parks – 1 POINT STATIC (not available for download) Infrastructure POLA Port of Los Angeles Berthlines Infrastructure POLA Port of Los Angeles Docks Infrastructure POLA Port of Los Angeles Slips Infrastructure POLA Port of Los Angeles Structures Infrastructure SANITATION Sanitation Districts Sewers Infrastructure USGS USGS Geographic Names Information Systems – Full Dataset Infrastructure USGS USGS Geographic Names Information Systems – Structure locations for labelling (not available for download) Political Boundaries BASA Split 2010 Block Group/City – BASA (building blocks for BASAs) Political Boundaries BOS Board Approved Statistical Areas Political Boundaries BOS Redistricting Communities Political Boundaries BOS Redistricting Units (2011) Political Boundaries DPW City Annexations Political Boundaries DPW City Boundaries Political Boundaries DPW City Boundaries Lines Political Boundaries DPW City Labels (not available for download) Political Boundaries DPW County Boundary Political Boundaries DPW Supervisorial District Boundaries (1971) Political Boundaries DPW Supervisorial District Boundaries (1981) Political Boundaries DPW Supervisorial District Boundaries (1991) Political Boundaries DPW Supervisorial District Boundaries (2001) Political Boundaries DPW Supervisorial District Boundaries (2011) Political Boundaries DRP Angeles National Forest Political Boundaries LACITY City of LA Community Boundaries Political Boundaries LACITY City of LA Council Districts (2002) Political Boundaries LACITY City of LA Council Districts (2012) Political Boundaries LACITY City of LA Neighborhood Council Boundaries Political Boundaries RRCC Precinct Boundaries Political Boundaries RRCC State Assembly Districts (2011) Political Boundaries RRCC State Board of Equalization Districts (2011) Political Boundaries RRCC State Senate Districts (2011) Political Boundaries RRCC US Congressional District Services BOS Capital Improvements (Line) (not available for download) Services BOS Capital Improvements (Point) (not available for download) Services BOS Capital Improvements (Polygon) (not available for download) Services DCFS DCFS Regional Offices Services DHS DHS – LA County Hospitals Services DHS DHS Community Partners Services DHS DHS Facilities Services DMH Mental Health Providers Services DMH Publicly Funded Mental Health Providers Services DPSS Childcare Locator_Public (CA016.2) Services DPSS DPSS Offices Services LIBRARY Bookmobile stops (not available for download) Services Probation Probation Group Homes Services Probation Probation Sites Services RRCC Polling Places Services USDATA Business Locations Transportation CAMS CAMS Freeway Shields Transportation CAMS CAMS Roads (Dissolved for Cartography) Transportation CAMS CAMS Roads (production) Transportation CHP Traffic Collision PARTIES (2003 – 2012) from SWITRS Transportation CHP Traffic Collision VICTIMS (2003 – 2012) from SWITRS Transportation DPR Department of Parks and Recreation Trail Access Points Transportation DPR Department of Parks and Recreation Trails Transportation DPR Department of Parks and Recreation Trails Staging Area Boundaries Transportation DPW Bikeways maintained by DPW Transportation DPW Public Works Bikeways Agreements Transportation DRP Airport Influence Areas Transportation DRP Airport Noise Contours Transportation DRP Airport Runway Protection Zones Transportation DRP Airport Supplmental Areas Transportation DRP Master Plan of Highways Transportation EGIS Airport Runways Transportation ESRI Road Network – for network analysis (not available for download) Transportation METROLINK Metrolink Mileposts Transportation METROLINK Metrolink Rail Lines Transportation METROLINK Metrolink Stations Transportation MTA Bus transit routes Transportation MTA Metro Lines (Gold, Blue, etc) Transportation MTA Metro Stations Transportation MTA MTA Bikeways Transportation MTA MTA Park & Ride Locations Transportation OEM Disaster Routes"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://egis3.lacounty.gov/dataportal/2010/02/05/2008-4-inch-color-orthophotography/")
                )
            )
        });
        return testConfigs;
    }

    public Egis3_LacountyTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
