package org.dice_research.squirrel.frontier.impl;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.resultset.ResultSetCompare;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.frontier.recrawling.FrontierQueryGenerator;
import org.dice_research.squirrel.frontier.recrawling.SparqlhostConnector;
import org.dice_research.squirrel.vocab.PROV_O;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

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
        while(rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode outdatedUri = sol.get("uri");
            LOGGER.info(String.valueOf(outdatedUri));
            assertEquals("Expected URI", outdatedUri.asResource().getURI(), "http://d-nb.info/gnd/4042012-7");
            assertEquals("Expected URI", outdatedUri.asResource().getURI(), "http://eu.dbpedia.org/resource/New_York_(estatua)");
            assertFalse("Not expecting any URI", rs.hasNext());
        }
        qe.close();
        }


    }

