package org.dice_research.squirrel.frontier.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactoryDataset;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.MongoDBBasedTest;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.dice_research.squirrel.data.uri.UriType;
import org.dice_research.squirrel.data.uri.filter.MongoDBKnowUriFilter;
import org.dice_research.squirrel.data.uri.norm.NormalizerImpl;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.queue.ipbased.MongoDBIpBasedQueue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class FrontierImplTest {



    private static FrontierImpl frontier;
    private static MongoDBIpBasedQueue queue;
    private static MongoDBKnowUriFilter filter;
    private static List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
    private static CrawleableUriFactory4Tests cuf = new CrawleableUriFactory4Tests();

    @Before
    public void setUp() throws Exception {
        MongoDBBasedTest.setUpMDB();
        filter = new MongoDBKnowUriFilter("localhost", 58027);
        queue = new MongoDBIpBasedQueue("localhost", 58027);
        filter.open();
        queue.open();
        frontier = new FrontierImpl(new NormalizerImpl(), filter, queue, true, 18000, 18000, null,null);
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/New_York"), InetAddress.getByName("127.0.0.1"),
            UriType.DEREFERENCEABLE));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/Moscow"), InetAddress.getByName("127.0.0.1"),
            UriType.DEREFERENCEABLE));
    }

    @Test
    public void getNextUris() throws Exception {

        queue.addUri(uris.get(1));

      //  queue.addCrawleableUri(uris.get(1));
        List<CrawleableUri> nextUris = frontier.getNextUris();
        List<CrawleableUri> assertion = new ArrayList<CrawleableUri>();
        assertion.add(uris.get(1));
        assertEquals("Should be dbr:New_York", assertion, nextUris);
    }

    @Test
    public void addNewUris() throws Exception {
        queue.purge();
        filter.purge();
        frontier.addNewUris(uris);
        List<CrawleableUri> nextUris = frontier.getNextUris();
        List<CrawleableUri> assertion = new ArrayList<CrawleableUri>();
        assertion.add(cuf.create(new URI("http://dbpedia.org/resource/New_York"),
            InetAddress.getByName("194.109.129.58"), UriType.DEREFERENCEABLE));
        assertion.add(cuf.create(new URI("http://dbpedia.org/resource/Moscow"), InetAddress.getByName("194.109.129.58"),
            UriType.DEREFERENCEABLE));
        assertEquals("Should be the same as uris array", assertion, nextUris);
    }

    @Test
    public void addNewUri() throws Exception {
        CrawleableUri uri_1 = cuf.create(new URI("http://dbpedia.org/resource/Tom_Lazarus"), null, UriType.UNKNOWN);
        frontier.addNewUri(uri_1);
        List<CrawleableUri> nextUris = frontier.getNextUris();
        List<CrawleableUri> assertion = new ArrayList<>();
        assertion.add(cuf.create(new URI("http://dbpedia.org/resource/Tom_Lazarus"),
            InetAddress.getByName("194.109.129.58"), UriType.DEREFERENCEABLE));
        assertEquals(assertion, nextUris);
    }

    @Test
    public void crawlingDone() throws Exception {
        List<CrawleableUri> crawledUris = new ArrayList<>();
        CrawleableUri uri_1 = cuf.create(new URI("http://dbpedia.org/resource/New_York"),
            InetAddress.getByName("127.0.0.1"), UriType.DEREFERENCEABLE);
        CrawleableUri uri_2 = cuf.create(new URI("http://dbpedia.org/resource/Moscow"),
            InetAddress.getByName("127.0.0.1"), UriType.DEREFERENCEABLE);
        crawledUris.add(uri_1);
        crawledUris.add(uri_2);

        //        frontier.addNewUris(crawledUris);
        //        filter.add(uri_1, 100);

        frontier.crawlingDone(crawledUris);
        assertFalse("uri_1 has been already crawled", frontier.knownUriFilter.isUriGood(uri_1));
    }

    @Test
    public void getNumberOfPendingUris() throws Exception {
        frontier.addNewUris(uris);
        List<CrawleableUri> nextUris = frontier.getNextUris();
        int numberOfPendingUris = frontier.getNumberOfPendingUris();
        assertEquals(1, numberOfPendingUris);
        numberOfPendingUris = frontier.getNumberOfPendingUris();
        assertEquals(2, nextUris.size());
    }

    /*
     * see https://github.com/dice-group/Squirrel/issues/47
     */
    //@Test
    public void simlpeRecrawling() throws Exception {
        // Add the URIs to the frontier
        List<CrawleableUri> uris = new ArrayList<>();
        CrawleableUri uri_1 = cuf.create(new URI("http://dbpedia.org/resource/uriThatShouldBeRecrawled"),
            InetAddress.getByName("127.0.0.1"), UriType.DEREFERENCEABLE);
        CrawleableUri uri_2 = cuf.create(new URI("http://dbpedia.org/resource/normalUri"),
            InetAddress.getByName("127.0.0.1"), UriType.DEREFERENCEABLE);
        uris.add(uri_1);
        uris.add(uri_2);
        frontier.addNewUris(uris);
        List<CrawleableUri> nextUris = frontier.getNextUris();
        for (CrawleableUri uri : nextUris) {
            Assert.assertTrue(uris.contains(uri));
        }
        for (CrawleableUri uri : uris) {
            Assert.assertTrue(nextUris.contains(uri));
        }
        // Set the first URI as recrawlable
        for (CrawleableUri uri : nextUris) {
            if (uri.getUri().equals(uri_1.getUri())) {
                uri.addData(Constants.URI_PREFERRED_RECRAWL_ON, System.currentTimeMillis() - 1);
            }
        }
        frontier.crawlingDone(uris);
        uris.add(uri_1);
        uris.add(uri_2);
        nextUris = frontier.getNextUris();
        Assert.assertNotNull(nextUris);
        assertTrue("uri_1 has been expected but couldn't be found", nextUris.contains(uri_1));
        Assert.assertEquals(1, nextUris.size());
        assertFalse("uri_2 has been found but was not expected", nextUris.contains(uri_2));
    }

    @Test
    public void RecrawlingTest() throws Exception {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        QueryExecutionFactory queryExecFactory = new QueryExecutionFactoryDataset(dataset);
        UpdateExecutionFactory updateExecFactory = new UpdateExecutionFactoryDataset(dataset);

        CrawleableUri uri = new CrawleableUri(new URI("http://example.org/dataset"));
        uri.addData(Constants.UUID_KEY, "123");
        CrawlingActivity activity = new CrawlingActivity(uri, "http://example.org/testRecrawling");
        uri.addData(Constants.URI_CRAWLING_ACTIVITY, activity);
      /*  List<CrawleableUri> uris = new ArrayList<>();
        CrawleableUri uri_1 = cuf.create(new URI("http://dbpedia.org/resource/uriThatShouldBeRecrawled"));
        uri_1.addData("endedAtTime", "2019-07-06T17:04:02.864Z");
        CrawleableUri uri_2 = cuf.create(new URI("http://dbpedia.org/resource/normalUri"));
        uri_2.addData("endedAtTime", "2019-07-06T19:38:02.864Z");
        uris.add(uri_1);Build);
        }

        // Set the first URI as recrawlable
        for (CrawleableUri uri : nextUris) {
            if (uri.getUri().equals(uri_1.getUri())) {
                uri.addData(Constants.URI_PREFERRED_RECRAWL_ON, System.currentTimeMillis() - 1);
            }
        }
        Assert.assertNotNull(nextUris);
        assertTrue("uri_1 has been expected but couldn't be found", nextUris.contains(uri_1));
        Assert.assertEquals(2, nextUris.size());

        */
    }

    @After
    public void tearDown() throws Exception {
        filter.purge();
        queue.purge();
        String rethinkDockerStopCommand = "docker stop squirrel-test-frontierimpl";
        Process p = Runtime.getRuntime().exec(rethinkDockerStopCommand);
        p.waitFor();
        String rethinkDockerRmCommand = "docker rm squirrel-test-frontierimpl";
        p = Runtime.getRuntime().exec(rethinkDockerRmCommand);
        p.waitFor();
    }
}


