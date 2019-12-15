package org.dice_research.squirrel.frontier.impl;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactoryDataset;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.frontier.recrawling.FrontierQueryGenerator;
import org.dice_research.squirrel.frontier.recrawling.SparqlhostConnector;
import org.dice_research.squirrel.vocab.PROV_O;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RecrawlingTest {

    @Test
    public void RecrawlingTest() throws Exception {
        List<CrawleableUri> urisToRecrawl = new ArrayList<>();
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        UpdateExecutionFactory updateExecFactory = new UpdateExecutionFactoryDataset(dataset);

        CrawleableUri uri = new CrawleableUri(new URI("http://example.org/dataset"));
        uri.addData(String.valueOf(PROV_O.endedAtTime), "2019-12-15T21:40:11.173Z");
        CrawleableUri uri1 = new CrawleableUri(new URI("http://example.org/resource"));
        uri1.addData(String.valueOf(PROV_O.endedAtTime), "2019-11-17T21:40:11.173Z");
        
        try (SparqlhostConnector sink = new SparqlhostConnector(queryExecFactory, updateExecFactory)) {
            Query getOutdatedUrisQuery = FrontierQueryGenerator.getOutdatedUrisQuery();
            QueryExecution qe = queryExecFactory.createQueryExecution(getOutdatedUrisQuery);
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution sol = rs.nextSolution();
                RDFNode outdatedUri = sol.get("uri");
                try {
                    urisToRecrawl.add(new CrawleableUri(new URI((outdatedUri.toString()))));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            qe.close();


        }
    }

}
