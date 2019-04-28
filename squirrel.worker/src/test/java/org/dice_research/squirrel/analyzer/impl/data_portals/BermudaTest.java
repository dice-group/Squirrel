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
public class BermudaTest extends AbstractDataPortalTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
/*        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("bermuda.io")),
            new File("src/test/resources/html_scraper_analyzer/html_scraper_analyzer/bermuda/bermuda_index.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("bermuda.io"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new PropertyImpl("http://bermuda.io/dataset")
                )
            )
        });*/
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("http://bermuda.io/dataset/budget-book-estimates-of-revenue-and-expenditure-for-the-year")),
            new File("src/test/resources/html_scraper_analyzer/bermuda/bermuda_detail.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset/budget-book-estimates-of-revenue-and-expenditure-for-the-year"),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Budget Book (Estimates of Revenue and Expenditure for the Year)"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset/budget-book-estimates-of-revenue-and-expenditure-for-the-year"),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Estimates of Revenue and Expenditure for the Year"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset/budget-book-estimates-of-revenue-and-expenditure-for-the-year"),
                    new PropertyImpl(DCTerms.creator.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Financial Secretary"),null)
                )
            )
        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("http://bermuda.io/dataset")),
            new File("src/test/resources/html_scraper_analyzer/bermuda/bermuda_search.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/budget-book-estimates-of-revenue-and-expenditure-for-the-year")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/financial-instructions"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/pati-information-statements")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/corporation-of-hamilton-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/bermuda-college-audited-financials")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/consolidated-fund-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/historical-forward-planning-documents"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/board-of-trustees-of-the-golf-courses-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/government-employees-health-insurance-gehi-fund-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/reports-on-education"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/berkeley-institute-capitation-grant-account-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/cedarbridge-academy-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/national-drug-commission-ndc-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/bermuda-housing-corporation-bhc-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/public-service-superannuation-fund-pssf-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/ministers-and-members-of-the-legislature-pensions-fund-mmlpf-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/contributory-pension-fund-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/bermuda-hospitals-board-bhb-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/bermuda-land-development-corporation-bldc-audited-financials"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://bermuda.io/dataset/bermuda-digest-of-statistics"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://bermuda.io/dataset?page=1"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("http://bermuda.io/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("http://bermuda.io/dataset?page=2"))
            )
        });
        return testConfigs;
    }

    public BermudaTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
