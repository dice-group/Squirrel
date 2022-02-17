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
public class CatalogDataGovTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriDetailsPage = "https://catalog.data.gov/dataset/demographic-statistics-by-zip-code-acfc9";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/catalog_data_gov/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("National Student Loan Data System"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.description,
                    new LiteralImpl(NodeFactory.createLiteral("The National Student Loan Data System (NSLDS) is the national database of information about loans and grants awarded to students under Title IV of the Higher Education Act (HEA) of 1965. NSLDS provides a centralized, integrated view of Title IV loans and grants during their complete life cycle, from aid approval through disbursement, repayment, deferment, delinquency, and closure."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.publisher,
                    new LiteralImpl(NodeFactory.createLiteral("Office of Federal Student Aid"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.issued,
                    new LiteralImpl(NodeFactory.createLiteral("January 7, 2016"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://purl.org/dsnotify/vocab/eventset/sourceDataset"),
                    new ResourceImpl("https://catalog.data.gov/harvest/object/b764a3d1-d5cc-4456-8d05-a6a17e21cc7b")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.license,
                    new ResourceImpl("http://www.opendefinition.org/licenses/cc-zero")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.modified,
                    new LiteralImpl(NodeFactory.createLiteral("August 9, 2018"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://ifap.ed.gov/fedschcodelist/attachments/1617FedSchoolCodeList.xlsx")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/about/data-center/student/title-iv/sites/default/files/fsawg/datacenter/library/FL_Dashboard_AY2009_2010_Q1.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/about/data-center/student/title-iv/sites/default/files/fsawg/datacenter/library/FL_Dashboard_AY2009_2010_Q2.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/about/data-center/student/title-iv/sites/default/files/fsawg/datacenter/library/FL_Dashboard_AY2009_2010_Q3.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/about/data-center/student/title-iv/sites/default/files/fsawg/datacenter/library/FL_Dashboard_AY2009_2010_Q4.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/PortfolioSummary.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/PortfoliobyLoanType.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/LocationofFFELPLoans.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/PortfoliobyLoanStatus.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/DLbyDefermentType.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/DLbyForbearanceType.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/FFELbyDefermentType.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/DLPortfoliobyRepaymentPlan.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/DLPortfoliobyDelinquencyStatus.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sa/sites/default/files/fsawg/datacenter/library/ECFReport.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sites/default/files/fsawg/datacenter/library/DLEnteringDefaults.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://studentaid.ed.gov/sites/default/files/fsawg/datacenter/library/TLF.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("https://catalog.data.gov/harvest/object/b764a3d1-d5cc-4456-8d05-a6a17e21cc7b")
                )
            )
        });
        return testConfigs;
    }

    public CatalogDataGovTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
