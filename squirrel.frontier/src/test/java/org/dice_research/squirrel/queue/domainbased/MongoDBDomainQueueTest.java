package org.dice_research.squirrel.queue.domainbased;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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
import org.dice_research.squirrel.data.uri.UriUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({ "deprecation" })
public class MongoDBDomainQueueTest extends MongoDBBasedTest {

    private List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
    private String expectedDomains[];
    private MongoDBDomainBasedQueue mongodbQueue;
    private QueryExecutionFactory queryExecFactory;

    @Before
    public void setUp() throws Exception {
        queryExecFactory = initQueryFactoryEngine();
        mongodbQueue = new MongoDBDomainBasedQueue("localhost", 58027,false,queryExecFactory);
        CrawleableUriFactory4Tests cuf = new CrawleableUriFactory4Tests();
        uris.add(cuf.create(new URI("http://localhost/sparql"), InetAddress.getByName("127.0.0.1"), UriType.SPARQL));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/New_York_City"), InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/Moscow"), InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/Berlin"), InetAddress.getByName("127.0.0.1"),
            UriType.DEREFERENCEABLE));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/Bangalore"), InetAddress.getByName("127.0.0.1"),
            UriType.DEREFERENCEABLE));
        // Added this to check https://github.com/AKSW/Squirrel/issues/17
        uris.add(cuf.create(new URI("http://danbri.org/foaf.rdf"),
                InetAddress.getByName((new URI("http://danbri.org/foaf.rdf")).toURL().getHost()), UriType.DUMP));

        expectedDomains = new String[] {"danbri.org", "dbpedia.org", "localhost"};
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

        for(int i = 0; i <= 5; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s1, p, o); // <http://dbpedia.org/resource/New_York_City>
        }
        for(int i = 0; i <= 10; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s2, p, o); // <http://dbpedia.org/resource/Berlin>
        }
        for(int i = 0; i <= 8; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s3, p, o); // <http://dbpedia.org/resource/Bangalore>
        }
        for(int i = 0; i <= 4; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s4, p, o); // <http://dbpedia.org/resource/Moscow>
        }


        return new QueryExecutionFactoryDataset(dataset);
    }

    @Test
    public void openClose() throws Exception {
        mongodbQueue.open();
        // assertTrue("squirrel database was created",
        // mongodbQueue.squirrelDatabaseExists());
        assertTrue("queue table was created", mongodbQueue.queueTableExists());
        mongodbQueue.close();
    }

    @Test
    public void openOpen() throws Exception {
        mongodbQueue.open();
        mongodbQueue.open();
        mongodbQueue.close();
    }

    @Test
    public void queueContainsDomain() throws Exception {
        mongodbQueue.open();
        mongodbQueue.purge();
        String domain = UriUtils.getDomainName(uris.get(0).getUri().toString());
        assertFalse(mongodbQueue.containsDomain(domain));
        mongodbQueue.addDomain(domain);
        assertTrue(mongodbQueue.containsDomain(domain));
        mongodbQueue.close();
    }

    @Test
    public void purgeQueue() throws Exception {
        mongodbQueue.open();
        mongodbQueue.purge();
        assertEquals(0, mongodbQueue.length());
        for (CrawleableUri uri : uris) {
            mongodbQueue.addUri(uri);
        }
        assertEquals(3, mongodbQueue.length());
        mongodbQueue.purge();
        assertEquals(0, mongodbQueue.length());
        mongodbQueue.close();
    }

    @Test
    public void addCrawleableUri() throws Exception {
        mongodbQueue.open();
        mongodbQueue.purge();
        mongodbQueue.addUri(uris.get(1));
        assertEquals(1, mongodbQueue.length());
        mongodbQueue.addUri(uris.get(2));
        assertEquals(1, mongodbQueue.length());
        mongodbQueue.close();
    }

    @Test
    public void addCrawleableUriWithScore() throws Exception {
        mongodbQueue.open();
        mongodbQueue.purge();
        for (CrawleableUri uri : uris) {
            mongodbQueue.addUri(uri);
        }
        assertEquals(3, mongodbQueue.length());
        List<CrawleableUri> listUris = mongodbQueue.getNextUris();
        assertEquals(1, listUris.size());
        List<CrawleableUri> listUris2 = mongodbQueue.getNextUris();
        assertEquals(4, listUris2.size());
        Assert.assertEquals("http://dbpedia.org/resource/New_York_City", listUris2.get(0).getUri().toString());
        Assert.assertEquals("http://dbpedia.org/resource/Moscow", listUris2.get(1).getUri().toString());
        Assert.assertEquals("http://dbpedia.org/resource/Berlin", listUris2.get(2).getUri().toString());
        Assert.assertEquals("http://dbpedia.org/resource/Bangalore", listUris2.get(3).getUri().toString());
        List<CrawleableUri> listUris3 = mongodbQueue.getNextUris();
        assertEquals(1, listUris3.size());
        List<CrawleableUri> listUris4 = mongodbQueue.getNextUris();
        assertEquals(null, listUris4);
        mongodbQueue.purge();
        mongodbQueue.close();
    }

    @Test
    public void getIterator() throws Exception {
        mongodbQueue.open();
        mongodbQueue.purge();
        for (CrawleableUri uri : uris) {
            mongodbQueue.addUri(uri);
        }
        Iterator<String> iter = mongodbQueue.getGroupIterator();
        List<String> domains = new ArrayList<String>();
        while (iter.hasNext()) {
            domains.add(iter.next());
        }
        mongodbQueue.close();
        Collections.sort(domains);
        assertArrayEquals(expectedDomains, domains.toArray(new String[domains.size()]));
    }

    @Test
    public void getUris() throws Exception {
        mongodbQueue.open();
        mongodbQueue.purge();
        for (CrawleableUri uri : uris) {
            mongodbQueue.addUri(uri);
        }

        List<CrawleableUri> listUris = mongodbQueue.getNextUris();
        Iterator<String> iter = mongodbQueue.getGroupIterator();
        int count = 0;
        while (iter.hasNext()) {
            String domain = iter.next();
            List<CrawleableUri> uriList = mongodbQueue.getUris(domain);
            for (CrawleableUri uri : uriList) {
                assertTrue(uris.contains(uri));
                ++count;
            }
        }

        assertEquals(uris.size(), count);
        mongodbQueue.close();
    }

    @Test
    public void deleteUris() throws Exception {
        mongodbQueue.open();
        mongodbQueue.purge();
        mongodbQueue.addUri(uris.get(1));
        List<CrawleableUri> retrievedUris = mongodbQueue.getNextUris();
        assertEquals(1, retrievedUris.size());
        assertTrue(uris.get(1).equals(retrievedUris.get(0)));
        mongodbQueue.addUri(uris.get(2));
        // The retrieval should fail
        assertNull(mongodbQueue.getNextUris());
        // Give back the first URI
        mongodbQueue.markUrisAsAccessible(retrievedUris);
        // Although we returned it, the queue shouldn't be empty!
        assertEquals(1, mongodbQueue.length());
        retrievedUris = mongodbQueue.getNextUris();
        assertEquals(1, retrievedUris.size());
        assertTrue(uris.get(2).equals(retrievedUris.get(0)));
        // Give back the second URI
        mongodbQueue.markUrisAsAccessible(retrievedUris);
        // Now, the queue should be empty
        assertEquals(0, mongodbQueue.length());
        mongodbQueue.close();
    }

    @Test
    public void testMakeURIsKeywise() throws URISyntaxException {
        List<CrawleableUri> uriList = new ArrayList<>();
        CrawleableUri uri1 = new CrawleableUri(new URI("http://dbpedia.org/resource/New_York_City"));
        CrawleableUri uri2 = new CrawleableUri(new URI("http://dbpedia.org/resource/Berlin"));
        CrawleableUri uri3 = new CrawleableUri(new URI("http://dbpedia.org/resource/Bangalore"));

        CrawleableUri uri4 = new CrawleableUri(new URI("https://en.wikipedia.org/wiki/New_York_City"));
        CrawleableUri uri5 = new CrawleableUri(new URI("https://en.wikipedia.org/wiki/Berlin"));

        CrawleableUri uri6 = new CrawleableUri(new URI("https://www.lonelyplanet.com/search?q=berlin"));
        uriList.add(uri1);
        uriList.add(uri2);
        uriList.add(uri3);
        uriList.add(uri4);
        uriList.add(uri5);
        uriList.add(uri6);
        Map<String, List<CrawleableUri>> keywiseUris = mongodbQueue.makeURIsKeywise(uriList);
        Assert.assertEquals(3, keywiseUris.get("dbpedia.org").size());
        Assert.assertEquals(2, keywiseUris.get("en.wikipedia.org").size());
        Assert.assertEquals(1, keywiseUris.get("www.lonelyplanet.com").size());
        Assert.assertTrue(keywiseUris.get("dbpedia.org").contains(uri1));
        Assert.assertTrue(keywiseUris.get("dbpedia.org").contains(uri2));
        Assert.assertTrue(keywiseUris.get("dbpedia.org").contains(uri3));
        Assert.assertTrue(keywiseUris.get("en.wikipedia.org").contains(uri4));
        Assert.assertTrue(keywiseUris.get("en.wikipedia.org").contains(uri5));
        Assert.assertTrue(keywiseUris.get("www.lonelyplanet.com").contains(uri6));
    }

    @Test
    public void testAddKeywiseUrisForFirstTime() throws URISyntaxException {
        mongodbQueue.open();
        mongodbQueue.purge();
        Map<String, List<CrawleableUri>> keyWiseUris = new HashMap<>();
        List<CrawleableUri> dbpediaUris = new ArrayList<>();
        CrawleableUri uri1 = new CrawleableUri(new URI("http://dbpedia.org/resource/Berlin"));
        CrawleableUri uri2 = new CrawleableUri(new URI("http://dbpedia.org/resource/Bangalore"));
        CrawleableUri uri3 = new CrawleableUri(new URI("http://dbpedia.org/resource/New_York_City"));
        dbpediaUris.add(uri1);
        dbpediaUris.add(uri2);
        dbpediaUris.add(uri3);
        keyWiseUris.put("dbpedia.org", dbpediaUris);
        List<CrawleableUri> urisNotAdded = mongodbQueue.addKeywiseUris(keyWiseUris, 2);
        Assert.assertEquals(0, urisNotAdded.size());
        mongodbQueue.purge();
        mongodbQueue.close();
    }

    @Test
    public void testAddKeywiseUrisWithBelowCriticalScores() throws URISyntaxException {
        mongodbQueue.open();
        mongodbQueue.purge();
        Map<String, List<CrawleableUri>> keyWiseUris = new HashMap<>();
        List<CrawleableUri> dbpediaUris = new ArrayList<>();
        CrawleableUri uri1 = new CrawleableUri(new URI("http://dbpedia.org/resource/Berlin"));
        CrawleableUri uri2 = new CrawleableUri(new URI("http://dbpedia.org/resource/Bangalore"));
        CrawleableUri uri3 = new CrawleableUri(new URI("http://dbpedia.org/resource/New_York_City"));
        mongodbQueue.addUri(uri1);
        mongodbQueue.addUri(uri2);
        mongodbQueue.addUri(uri3);
        dbpediaUris.add(uri1);
        dbpediaUris.add(uri2);
        dbpediaUris.add(uri3);
        keyWiseUris.put("dbpedia.org", dbpediaUris);
        List<CrawleableUri> urisNotAdded = mongodbQueue.addKeywiseUris(keyWiseUris, 2);
        Assert.assertEquals(3, urisNotAdded.size());
        mongodbQueue.purge();
        mongodbQueue.close();
    }
}
