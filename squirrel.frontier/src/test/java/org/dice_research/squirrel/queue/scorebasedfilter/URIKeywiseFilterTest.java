package org.dice_research.squirrel.queue.scorebasedfilter;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.MongoDBBasedTest;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URIKeywiseFilterTest extends MongoDBBasedTest {

    private URIKeywiseFilter uriKeywiseFilter;

    @Before
    public void setUp() throws Exception {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        Node g = NodeFactory.createURI(Constants.DEFAULT_META_DATA_GRAPH_URI.toString());
        Node s1 = NodeFactory.createURI("http://dbpedia.org/resource/New_York_City");
        Node s2 = NodeFactory.createURI("http://dbpedia.org/resource/Berlin");
        Node s3 = NodeFactory.createURI("http://dbpedia.org/resource/Bangalore");
        Node s4 = NodeFactory.createURI("http://dbpedia.org/resource/Moscow");
        Node s5 = NodeFactory.createURI("https://www.lonelyplanet.com/germany/paderborn");
        Node s6 = NodeFactory.createURI("https://www.lonelyplanet.com/germany/north-rhine-westphalia/dortmund");
        Node s7 = NodeFactory.createURI("https://www.lonelyplanet.com/england/london");
        Node s8 = NodeFactory.createURI("https://www.lonelyplanet.com/france/paris");

        String otherUri = "http://dbpedia.org/doesntMatter";
        Node o = NodeFactory.createURI("http://dbpedia.org/doesntMatter");
        DatasetGraph graph = dataset.asDatasetGraph();

        for (int i = 0; i < 5; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s1, p, o); // <http://dbpedia.org/resource/New_York_City>
        }
        for (int i = 0; i < 10; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s2, p, o); // <http://dbpedia.org/resource/Berlin>
        }
        for (int i = 0; i < 8; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s3, p, o); // <http://dbpedia.org/resource/Bangalore>
        }
        for (int i = 0; i < 4; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s4, p, o); // <http://dbpedia.org/resource/Moscow>
        }
        for (int i = 0; i < 4; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s5, p, o); // <https://www.lonelyplanet.com/germany/paderborn>
        }
        for (int i = 0; i < 5; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s6, p, o); // <https://www.lonelyplanet.com/germany/north-rhine-westphalia/dortmund>
        }
        for (int i = 0; i < 4; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s7, p, o); // <https://www.lonelyplanet.com/england/london>
        }
        for (int i = 0; i < 7; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s8, p, o); // <https://www.lonelyplanet.com/france/paris>
        }
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        uriKeywiseFilter = new URIKeywiseFilter(queryExecFactory);
    }

    @Test
    public void testURIScore() throws URISyntaxException {
        float score1 = uriKeywiseFilter.getURIScore(new CrawleableUri(new URI("http://dbpedia.org/resource/Berlin")));
        float score2 = uriKeywiseFilter.getURIScore(new CrawleableUri(new URI("http://dbpedia.org/resource/Bangalore")));
        Assert.assertEquals(.1f, score1, .0001);
        Assert.assertEquals(.125f, score2, .0001);
    }

    @Test
    public void testFilterUrisKeywise() throws URISyntaxException {
        Map<String, List<CrawleableUri>> keyWiseUris = new HashMap<>();
        List<CrawleableUri> dbpediaUris = new ArrayList<>();
        CrawleableUri uri1 = new CrawleableUri(new URI("http://dbpedia.org/resource/Berlin"));
        CrawleableUri uri2 = new CrawleableUri(new URI("http://dbpedia.org/resource/Bangalore"));
        CrawleableUri uri3 = new CrawleableUri(new URI("http://dbpedia.org/resource/New_York_City"));
        CrawleableUri uri4 = new CrawleableUri(new URI("http://dbpedia.org/resource/Moscow"));
        dbpediaUris.add(uri1);
        dbpediaUris.add(uri2);
        dbpediaUris.add(uri3);
        dbpediaUris.add(uri4);
        keyWiseUris.put("dbpedia.org", dbpediaUris);

        Assert.assertEquals(4, uriKeywiseFilter.filterUrisKeywise(keyWiseUris, 2, .001f).size());
        Assert.assertEquals(0, uriKeywiseFilter.filterUrisKeywise(keyWiseUris, 2, .8f).size());

        CrawleableUri uri5 = new CrawleableUri(new URI("https://www.lonelyplanet.com/germany/paderborn"));
        CrawleableUri uri6 = new CrawleableUri(new URI("https://www.lonelyplanet.com/germany/north-rhine-westphalia/dortmund"));
        CrawleableUri uri7 = new CrawleableUri(new URI("https://www.lonelyplanet.com/england/london"));
        CrawleableUri uri8 = new CrawleableUri(new URI("https://www.lonelyplanet.com/france/paris"));
        List<CrawleableUri> lonelyPlanetUris = new ArrayList<>();
        lonelyPlanetUris.add(uri5);
        lonelyPlanetUris.add(uri6);
        lonelyPlanetUris.add(uri7);
        lonelyPlanetUris.add(uri8);
        keyWiseUris.put("www.lonelyplanet.com", lonelyPlanetUris);
        Map<CrawleableUri, Float> filteredUris = uriKeywiseFilter.filterUrisKeywise(keyWiseUris, 2, .2f);

        Assert.assertEquals(4, filteredUris.size());
        Assert.assertTrue(filteredUris.containsKey(uri5));
        Assert.assertTrue(filteredUris.containsKey(uri6));
        Assert.assertTrue(filteredUris.containsKey(uri7));
        Assert.assertTrue(filteredUris.containsKey(uri8));
    }
}
