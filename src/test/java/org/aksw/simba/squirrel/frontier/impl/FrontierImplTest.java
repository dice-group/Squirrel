package org.aksw.simba.squirrel.frontier.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.RethinkDBBasedTest;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.queue.RDBQueue;
import org.junit.*;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;

public class FrontierImplTest extends RethinkDBBasedTest {

    private FrontierImpl frontier;
    private RDBQueue queue;
    private RDBKnownUriFilter filter;
    private List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
    private CrawleableUriFactory4Tests cuf = new CrawleableUriFactory4Tests();

    @Before
    public void setUp() throws Exception {
        filter = new RDBKnownUriFilter("localhost", 58015);
        queue = new RDBQueue("localhost", 58015);
        // filter.purge();
        // queue.purge();
        frontier = new FrontierImpl(filter, queue);

        uris.add(cuf.create(new URI("http://dbpedia.org/resource/New_York"), InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/Moscow"), InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE));
    }

    @Test
    public void getNextUris() throws Exception {
        queue.addCrawleableUri(uris.get(1));

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

        List<CrawleableUri> newUris = new ArrayList<>();
        CrawleableUri uri_3 = cuf.create(new URI("http://dbpedia.org/resource/Tom_Lazarus"), null, UriType.UNKNOWN);
        newUris.add(uri_3);
        frontier.crawlingDone(crawledUris, newUris);
        assertTrue("uri_3 has just been added", frontier.knownUriFilter.isUriGood(uri_3));
        assertFalse("uri_1 has been already crawled", frontier.knownUriFilter.isUriGood(uri_1));
    }

    @Test
    public void getNumberOfPendingUris() throws Exception {
        frontier.addNewUris(uris);
        List<CrawleableUri> nextUris = frontier.getNextUris();
        int numberOfPendingUris = frontier.getNumberOfPendingUris();

        assertEquals("Number of pending URIs should be 1", 1, numberOfPendingUris);
    }

    /*
     * see https://github.com/dice-group/Squirrel/issues/47
     */
    @Test
    public void simpleRecrawling() throws Exception {
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
            if(uri.getUri().equals(uri_1.getUri())) {
                uri.addData(Constants.URI_PREFERRED_RECRAWL_ON, System.currentTimeMillis() - 1);
            }
        }

        frontier.crawlingDone(nextUris, new ArrayList<>());

        nextUris = frontier.getNextUris();
        Assert.assertNotNull(nextUris);
        assertTrue("uri_1 has been expected but couldn't be found", nextUris.contains(uri_1));
        Assert.assertEquals(1, nextUris.size());
        assertFalse("uri_2 has been found but was not expected", nextUris.contains(uri_2));
    }
}
