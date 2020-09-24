package org.dice_research.squirrel.frontier.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.dice_research.squirrel.frontier.recrawling.FrontierQueryGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecrawlingTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecrawlingTest.class);

    @Test
    public void recrawling(){
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel().read("test.ttl"));
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        Calendar date = Calendar.getInstance();
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.HOUR, 7);
        date.set(Calendar.AM_PM, Calendar.AM);
        date.set(Calendar.MONTH, Calendar.JANUARY);
        date.set(Calendar.DAY_OF_MONTH, 3);
        date.set(Calendar.YEAR, 2020);
        Query getOutdatedUrisQuery = FrontierQueryGenerator.getOutdatedUrisQuery(date);
        QueryExecution qe = queryExecFactory.createQueryExecution(getOutdatedUrisQuery);
        ResultSet rs = qe.execSelect();
        assertTrue("There should be at least one result", rs.hasNext());
        QuerySolution solu = rs.nextSolution();
        LOGGER.info("Solution: {}", solu);
           RDFNode outdatedUri = solu.get("url");
            assertEquals("Expected URI", "http://dbpedia.org/ontology/language", outdatedUri.asResource().getURI());
            assertFalse("Not expecting any URI", rs.hasNext());

        qe.close();
        }
}

