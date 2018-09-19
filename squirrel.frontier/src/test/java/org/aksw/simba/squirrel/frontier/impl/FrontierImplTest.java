package org.aksw.simba.squirrel.frontier.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.queue.RDBQueue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;

public class FrontierImplTest {

    RethinkDB r;
    Connection connection;
    FrontierImpl frontier;
    RDBQueue queue;
    RDBKnownUriFilter filter;
    List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
    CrawleableUriFactory4Tests cuf = new CrawleableUriFactory4Tests();

    @Before
    public void setUp() throws Exception {
        String rethinkDockerExecCmd = "docker run --name squirrel-test-rethinkdb "
                + "-p 58015:28015 -p 58887:8080 -d rethinkdb:2.3.5";
        Process p = Runtime.getRuntime().exec(rethinkDockerExecCmd);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // read any errors from the attempted command
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        r = RethinkDB.r;
        int retryCount = 0;
        while (true) {
            try {
                connection = r.connection().hostname("localhost").port(58015).connect();
                break;
            } catch (ReqlDriverError error) {
                System.out.println("Could not connect, retrying");
                retryCount++;
                if (retryCount > 10)
                    break;
                Thread.sleep(5000);
            }
        }

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
        Map<CrawleableUri,List<CrawleableUri>> map = new HashMap<CrawleableUri,List<CrawleableUri>>();
        CrawleableUri uri_1 = cuf.create(new URI("http://dbpedia.org/resource/New_York"),
                InetAddress.getByName("127.0.0.1"), UriType.DEREFERENCEABLE);
        CrawleableUri uri_2 = cuf.create(new URI("http://dbpedia.org/resource/Moscow"),
                InetAddress.getByName("127.0.0.1"), UriType.DEREFERENCEABLE);

        frontier.crawlingDone(crawledUris);
        assertFalse("uri_1 has been already crawled", frontier.knownUriFilter.isUriGood(uri_1));
    }

    @Test
    public void getNumberOfPendingUris() throws Exception {
        frontier.addNewUris(uris);
        List<CrawleableUri> nextUris = frontier.getNextUris();
        int numberOfPendingUris = frontier.getNumberOfPendingUris();
        assertEquals(1, numberOfPendingUris);
        
        nextUris = frontier.getNextUris();
        numberOfPendingUris = frontier.getNumberOfPendingUris();
        assertEquals(2, numberOfPendingUris);
    }

    /*
     * see https://github.com/dice-group/Squirrel/issues/47
     */
    @Test
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
            if(uri.getUri().equals(uri_1.getUri())) {
                uri.addData(Constants.URI_PREFERRED_RECRAWL_ON, System.currentTimeMillis() - 1);
            }
        }

        frontier.crawlingDone(uris);

        nextUris = frontier.getNextUris();
        Assert.assertNotNull(nextUris);
        assertTrue("uri_1 has been expected but couldn't be found", nextUris.contains(uri_1));
        Assert.assertEquals(1, nextUris.size());
        assertFalse("uri_2 has been found but was not expected", nextUris.contains(uri_2));
    }

    @After
    public void tearDown() throws Exception {
        String rethinkDockerStopCommand = "docker stop squirrel-test-rethinkdb";
        Process p = Runtime.getRuntime().exec(rethinkDockerStopCommand);
        p.waitFor();
        String rethinkDockerRmCommand = "docker rm squirrel-test-rethinkdb";
        p = Runtime.getRuntime().exec(rethinkDockerRmCommand);
        p.waitFor();
    }
}