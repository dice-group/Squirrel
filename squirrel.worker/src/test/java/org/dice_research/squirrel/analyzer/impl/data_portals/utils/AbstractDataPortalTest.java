package org.dice_research.squirrel.analyzer.impl.data_portals.utils;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.dice_research.squirrel.analyzer.impl.html.scraper.HtmlScraper;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.hobbit.utils.test.ModelComparisonHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractDataPortalTest {

    private CrawleableUri uriToCrawl;
    private File fileToScrape;
    private List<Triple> expectedTriples;
    private HtmlScraper htmlScraper;

    @Before
    public void prepareGeneral() {
        File configurationFile = new File("src/test/resources/html_scraper_analyzer/yaml");
        this.htmlScraper = new HtmlScraper(configurationFile);
    }

    public AbstractDataPortalTest(CrawleableUri uri, File fileToScrape, List<Triple> expectedTriples) {
        super();
        this.uriToCrawl = uri;
        this.fileToScrape = fileToScrape;
        this.expectedTriples = expectedTriples;
    }

    @Test
    public void test() throws Exception{
        List<Triple> listTriples = new ArrayList<Triple>();
        listTriples.addAll(htmlScraper.scrape(uriToCrawl, fileToScrape));
        ModelCom expectedModel = (ModelCom) ModelFactory.createDefaultModel();
        for (Triple curTriple: expectedTriples){
            Statement tempStmt = StatementImpl.toStatement(curTriple, expectedModel);
            expectedModel.add(tempStmt);
        }

        ModelCom actualModel = (ModelCom) ModelFactory.createDefaultModel();
        for (Triple curTriple: listTriples){
            Statement tempStmt = StatementImpl.toStatement(curTriple, actualModel);
            actualModel.add(tempStmt);
        }

        Set<Statement> missingStatements = ModelComparisonHelper.getMissingStatements(expectedModel, actualModel);
        Set<Statement> unexpectedStatements = ModelComparisonHelper.getMissingStatements(actualModel, expectedModel);

        StringBuilder builder = new StringBuilder();
        if (missingStatements.size() != 0) {
            builder.append("The result does not contain the expected statements:\n\n"
                + missingStatements.stream().map(Object::toString).collect(Collectors.joining("\n"))
                + "\n\nExpected model:\n\n" + expectedModel.toString() + "\n\nResult model:\n\n" + actualModel.toString()
                + "\n");
        }

        if (unexpectedStatements.size() != 0) {
            builder.append("The result contains the unexpected statements:\n\n"
                + unexpectedStatements.stream().map(Object::toString).collect(Collectors.joining("\n"))
                + "\n\nExpected model:\n\n" + expectedModel.toString() + "\nResult model:\n\n" + actualModel.toString()
                + "\n");
        }

        Assert.assertTrue(builder.toString(), missingStatements.size() == 0 &&
            unexpectedStatements.size() == 0);
    }
}
