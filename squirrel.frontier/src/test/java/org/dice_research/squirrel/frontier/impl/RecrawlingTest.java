package org.dice_research.squirrel.frontier.impl;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.dice_research.squirrel.frontier.recrawling.FrontierQueryGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class RecrawlingTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecrawlingTest.class);

    @Test
    public void Recrawling() throws Exception {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel().read("test.ttl"));
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);

        Query getOutdatedUrisQuery = FrontierQueryGenerator.getOutdatedUrisQuery();
        QueryExecution qe = queryExecFactory.createQueryExecution(getOutdatedUrisQuery);
        ResultSet rs = qe.execSelect();
        assertTrue("There should be at least one result", rs.hasNext());
        QuerySolution solu = rs.nextSolution();
           RDFNode outdatedUri = solu.get("uri");
            LOGGER.info(String.valueOf(outdatedUri));
            assertEquals("Expected URI", outdatedUri.asResource().getURI(), "http://d-nb.info/gnd/4042012-7");
            assertFalse("Not expecting any URI", rs.hasNext());

        qe.close();
        }
}

