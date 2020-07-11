package org.dice_research.squirrel.queue.ipbased;

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
import org.dice_research.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.dice_research.squirrel.data.uri.UriType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("deprecation")
public class MongoDBIpScoreBasedQueueTest extends MongoDBBasedTest{

    private List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
    private Set<InetAddress> expectedIps;
    private MongoDBIpScoreBasedQueue mongodbQueue;
    private QueryExecutionFactory queryExecFactory;

    @Before
    public void setUp() throws Exception {
        queryExecFactory = initQueryFactoryEngine();
    	mongodbQueue = new MongoDBIpScoreBasedQueue("localhost", 58027,false,queryExecFactory);

//    	mongodbQueue = new MongoDBQueue("localhost", 27017);

        CrawleableUriFactory4Tests cuf = new CrawleableUriFactory4Tests();
        uris.add(cuf.create(new URI("http://localhost/sparql"), InetAddress.getByName("127.0.0.1"), UriType.SPARQL));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/New_York_City"), InetAddress.getByName("dbpedia.org"),
                UriType.DEREFERENCEABLE));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/Moscow"), InetAddress.getByName("dbpedia.org"),
                UriType.DEREFERENCEABLE));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/Berlin"), InetAddress.getByName("dbpedia.org"),
            UriType.DEREFERENCEABLE));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/Bangalore"), InetAddress.getByName("dbpedia.org"),
            UriType.DEREFERENCEABLE));
        // Added this to check https://github.com/AKSW/Squirrel/issues/17
        uris.add(cuf.create(new URI("http://danbri.org/foaf.rdf"),
                InetAddress.getByName((new URI("http://danbri.org/foaf.rdf")).toURL().getHost()), UriType.DUMP));

        expectedIps = new HashSet<InetAddress>();
        expectedIps.add(InetAddress.getByName("127.0.0.1"));
        expectedIps.add(InetAddress.getByName("dbpedia.org"));
        expectedIps.add(InetAddress.getByName((new URI("http://danbri.org/foaf.rdf")).toURL().getHost()));
    }

    private QueryExecutionFactory initQueryFactoryEngine() {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        Node g = NodeFactory.createURI(Constants.DEFAULT_META_DATA_GRAPH_URI.toString());
//        DatasetGraph graph = createDataSetGraph(dataset, g);

        Node s1 = NodeFactory.createURI("http://dbpedia.org/resource/New_York_City");
        Node s2 = NodeFactory.createURI("http://dbpedia.org/resource/Berlin");
        Node s3 = NodeFactory.createURI("http://dbpedia.org/resource/Bangalore");
        Node s4 = NodeFactory.createURI("http://dbpedia.org/resource/Moscow");
        String otherUri = "http://dbpedia.org/doesntMatter";
        Node o = NodeFactory.createURI("http://dbpedia.org/doesntMatter");
        DatasetGraph graph = dataset.asDatasetGraph();

        for(int i = 0; i < 5; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s1, p, o); // <http://dbpedia.org/resource/New_York_City>
        }
        for(int i = 0; i < 10; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s2, p, o); // <http://dbpedia.org/resource/Berlin>
        }
        for(int i = 0; i < 8; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s3, p, o); // <http://dbpedia.org/resource/Bangalore>
        }
        for(int i = 0; i < 4; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s4, p, o); // <http://dbpedia.org/resource/Moscow>
        }
        return new QueryExecutionFactoryDataset(dataset);
    }

    @Test
    public void getUris() throws Exception {
        mongodbQueue.open();
        mongodbQueue.purge();
        mongodbQueue.addUris(uris);
        Iterator<InetAddress> iter = mongodbQueue.getGroupIterator();
        int count = 0;
        while (iter.hasNext()) {
            InetAddress ip = iter.next();
            List<CrawleableUri> uriList = mongodbQueue.getUris(ip);
            for (CrawleableUri uri : uriList) {
                assertTrue(uris.contains(uri));
                ++count;
            }
        }
        assertEquals(6, count);
        mongodbQueue.close();
    }

    @Test
    public void testAddUris() throws URISyntaxException, UnknownHostException {
        mongodbQueue.open();
        mongodbQueue.purge();
        Map<InetAddress, List<CrawleableUri>> keyWiseUris = new HashMap<>();
        List<CrawleableUri> dbpediaUris = new ArrayList<>();
        CrawleableUri uri1 = new CrawleableUri(new URI("http://dbpedia.org/resource/Paderborn"), InetAddress.getByName("dbpedia.org"));
        CrawleableUri uri2 = new CrawleableUri(new URI("http://dbpedia.org/resource/Sirsi"), InetAddress.getByName("dbpedia.org"));
        dbpediaUris.add(uri1);
        dbpediaUris.add(uri2);
        keyWiseUris.put(InetAddress.getByName("dbpedia.org"), dbpediaUris);
        mongodbQueue.addUris(keyWiseUris);
        List<CrawleableUri> mongoDBUris = mongodbQueue.getUris(InetAddress.getByName("dbpedia.org"));
        Assert.assertTrue(mongoDBUris.contains(uri1));
        Assert.assertTrue(mongoDBUris.contains(uri2));
        mongodbQueue.purge();
        mongodbQueue.close();
    }
}
