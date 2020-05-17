package org.aksw.simba.squirrel.queue;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.queue.URIGraphSizeBasedQueue;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class URIGraphSizeBasedQueueTest {

    @Test
    public void testPriorityQueue() throws URISyntaxException {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        Node g = NodeFactory.createURI(Constants.DEFAULT_META_DATA_GRAPH_URI.toString());
//        DatasetGraph graph = createDataSetGraph(dataset, g);

        Node s1 = NodeFactory.createURI("http://dbpedia.org/resource/New_York_City");
        Node s2 = NodeFactory.createURI("http://dbpedia.org/resource/Berlin");
        String otherUri = "http://dbpedia.org/doesntMatter";
        Node o = NodeFactory.createURI("http://dbpedia.org/doesntMatter");
        DatasetGraph graph = dataset.asDatasetGraph();

        for(int i = 0; i <= 5; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s1, p, o);
        }
        for(int i = 0; i <= 10; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s2, p, o);
        }

        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        URIGraphSizeBasedQueue uRIGraphSizeBasedQueue = new URIGraphSizeBasedQueue(queryExecFactory);

        uRIGraphSizeBasedQueue.addUri(new CrawleableUri(new URI("http://dbpedia.org/resource/New_York_City")));
        uRIGraphSizeBasedQueue.addUri(new CrawleableUri(new URI("http://dbpedia.org/resource/Berlin")));

        List<CrawleableUri> uris = uRIGraphSizeBasedQueue.getNextUris();
        Assert.assertEquals(2, uris.size());
        Assert.assertTrue(uris.get(0).getUri().toString().equals("http://dbpedia.org/resource/Berlin"));
        Assert.assertTrue(uris.get(1).getUri().toString().equals("http://dbpedia.org/resource/New_York_City"));
    }
}
