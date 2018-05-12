package org.aksw.simba.squirrel.queue;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("rawtypes")
public class RDBQueueTest {
    Connection connection;
    RDBQueue rdbQueue;
    RethinkDB r = RethinkDB.r;
    List<CrawleableUri> uris = new ArrayList<CrawleableUri>();

    @Before
    public void setUp() throws Exception {
        String rethinkDockerExecCmd = "docker run --name squirrel-test-rethinkdb " +
            "-p 58015:28015 -p 58887:8080 -d rethinkdb:2.3.5";
        Process p = Runtime.getRuntime().exec(rethinkDockerExecCmd);
        BufferedReader stdInput = new BufferedReader(new
            InputStreamReader(p.getInputStream()));
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
                if (retryCount > 10) break;
                Thread.sleep(5000);
            }
        }

        // to start up the server run
        // docker run -p 8080:8080 -p 29015:29015 -p 28015:28015 --name rethinkdb -v "$PWD:/data" -it --rm rethinkdb:2.3.5
        rdbQueue = new RDBQueue("localhost", 58015);
        //rdbQueue = new RDBQueue("192.168.99.100", 28015);

        CrawleableUriFactory4Tests cuf = new CrawleableUriFactory4Tests();
        uris.add(cuf.create(new URI("http://localhost/sparql"), InetAddress.getByName("127.0.0.1"), UriType.SPARQL));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/New_York"), InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/Moscow"), InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE));
        // Added this to check https://github.com/AKSW/Squirrel/issues/17
        uris.add(cuf.create(new URI("http://danbri.org/foaf.rdf"),
                InetAddress.getByName((new URI("http://danbri.org/foaf.rdf")).toURL().getHost()), UriType.DUMP));
    }

    @Test
    public void openClose() {
        rdbQueue.open();
        assertTrue("squirrel database was created", rdbQueue.squirrelDatabaseExists());
        assertTrue("queue table was created", rdbQueue.queueTableExists());
        rdbQueue.close();
    }

    @Test
    public void openOpen() {
        rdbQueue.open();
        rdbQueue.open();
        rdbQueue.close();
    }

    @Test
    public void packTuple() {
        List rArray = rdbQueue.packTuple("192.168.1.1", "http://localhost");
        assertTrue(rArray.contains("192.168.1.1"));
        assertTrue(rArray.contains("http://localhost"));
        rArray = rdbQueue.packTuple("192.168.1.1", "DUMP");
        assertTrue(rArray.contains("192.168.1.1"));
        assertTrue(rArray.contains("DUMP"));
    }

    @Test
    public void getIpAddressTypeKey() {
        List rArray = rdbQueue.getIpAddressTypeKey(uris.get(0));
        String arrayString = rArray.toString();
        assertTrue(arrayString, rArray.contains("127.0.0.1"));
        assertFalse(arrayString, rArray.contains("http://danbri.org/foaf.rdf"));
        assertTrue(arrayString, rArray.contains("SPARQL"));
    }

    @Test
    public void queueContainsIpAddressTypeKey() {
        rdbQueue.open();
        List iatKey = rdbQueue.getIpAddressTypeKey(uris.get(0));
        assertFalse(rdbQueue.queueContainsIpAddressTypeKey(iatKey));
        rdbQueue.addToQueue(uris.get(0));
        assertTrue(rdbQueue.queueContainsIpAddressTypeKey(iatKey));
        rdbQueue.close();
    }

    @Test
    public void purgeQueue() {
        rdbQueue.open();
        rdbQueue.purge();
        assertEquals(0, rdbQueue.length());
        for (CrawleableUri uri : uris) {
            rdbQueue.addToQueue(uri);
        }
        assertEquals(3, rdbQueue.length());
        rdbQueue.purge();
        assertEquals(0, rdbQueue.length());
        rdbQueue.close();
    }

    @Test
    public void addCrawleableUri() {
        rdbQueue.open();
        rdbQueue.purge();
        rdbQueue.addCrawleableUri(uris.get(1));
        assertEquals(1, rdbQueue.length());
        List iatKey = rdbQueue.getIpAddressTypeKey(uris.get(2));
        rdbQueue.addCrawleableUri(uris.get(2), iatKey);
        assertEquals(1, rdbQueue.length());
        rdbQueue.close();
    }

    @Test
    public void addToQueue() {
        rdbQueue.open();
        for (CrawleableUri uri : uris) {
            rdbQueue.addToQueue(uri);
        }
        assertEquals(3, rdbQueue.length());
        rdbQueue.close();
    }

    @Test
    public void crawleableUriToRDBHashMap() {
        MapObject rHashMap = rdbQueue.crawleableUriToRDBHashMap(uris.get(0));
        assertTrue(rHashMap.containsKey("uris"));
        assertTrue(rHashMap.containsKey("ipAddress"));
        assertTrue(rHashMap.containsKey("type"));
        assertEquals("127.0.0.1", rHashMap.get("ipAddress"));
        assertEquals("SPARQL", rHashMap.get("type"));
    }

    @Test
    public void getIterator() {
        rdbQueue.open();
        for (CrawleableUri uri : uris) {
            rdbQueue.addToQueue(uri);
        }
        Iterator<IpUriTypePair> iter = rdbQueue.getIterator();
        while (iter.hasNext()) {
            IpUriTypePair pair = iter.next();
            System.out.println(pair.toString());
        }
        rdbQueue.close();
    }

    @Test
    public void getUris() {
        rdbQueue.open();
        for (CrawleableUri uri : uris) {
            rdbQueue.addToQueue(uri);
        }
        Iterator<IpUriTypePair> iter = rdbQueue.getIterator();
        while (iter.hasNext()) {
            IpUriTypePair pair = iter.next();
            List<CrawleableUri> uriList = rdbQueue.getUris(pair);
            for (CrawleableUri uri : uriList) {
                assertTrue(uris.contains(uri));
            }
        }
        rdbQueue.close();
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
