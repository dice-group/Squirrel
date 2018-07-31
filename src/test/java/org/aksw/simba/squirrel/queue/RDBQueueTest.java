package org.aksw.simba.squirrel.queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aksw.simba.squirrel.RethinkDBBasedTest;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.junit.Before;
import org.junit.Test;

import com.rethinkdb.model.MapObject;

@SuppressWarnings("rawtypes")
public class RDBQueueTest extends RethinkDBBasedTest {

    private List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
    private RDBQueue rdbQueue;

    @Before
    public void setUp() throws Exception {
        rdbQueue = new RDBQueue("localhost", 58015);

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
    public void openClose() throws Exception {
        rdbQueue.open();
        assertTrue("squirrel database was created", rdbQueue.squirrelDatabaseExists());
        assertTrue("queue table was created", rdbQueue.queueTableExists());
        rdbQueue.close();
    }

    @Test
    public void openOpen() throws Exception {
        rdbQueue.open();
        rdbQueue.open();
        rdbQueue.close();
    }

    @Test
    public void packTuple() throws Exception {
        List rArray = rdbQueue.packTuple("192.168.1.1", "http://localhost");
        assertTrue(rArray.contains("192.168.1.1"));
        assertTrue(rArray.contains("http://localhost"));
        rArray = rdbQueue.packTuple("192.168.1.1", "DUMP");
        assertTrue(rArray.contains("192.168.1.1"));
        assertTrue(rArray.contains("DUMP"));
    }

    @Test
    public void getIpAddressTypeKey() throws Exception {
        List rArray = rdbQueue.getIpAddressTypeKey(uris.get(0));
        String arrayString = rArray.toString();
        assertTrue(arrayString, rArray.contains("127.0.0.1"));
        assertFalse(arrayString, rArray.contains("http://danbri.org/foaf.rdf"));
        assertTrue(arrayString, rArray.contains("SPARQL"));
    }

    @Test
    public void queueContainsIpAddressTypeKey() throws Exception {
        rdbQueue.open();
        List iatKey = rdbQueue.getIpAddressTypeKey(uris.get(0));
        assertFalse(rdbQueue.queueContainsIpAddressTypeKey(iatKey));
        rdbQueue.addToQueue(uris.get(0));
        assertTrue(rdbQueue.queueContainsIpAddressTypeKey(iatKey));
        rdbQueue.close();
    }

    @Test
    public void purgeQueue() throws Exception {
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
    public void addCrawleableUri() throws Exception {
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
    public void addToQueue() throws Exception {
        rdbQueue.open();
        for (CrawleableUri uri : uris) {
            rdbQueue.addToQueue(uri);
        }
        assertEquals(3, rdbQueue.length());
        rdbQueue.close();
    }

    @Test
    public void crawleableUriToRDBHashMap() throws Exception {
        MapObject rHashMap = rdbQueue.crawleableUriToRDBHashMap(uris.get(0));
        assertTrue(rHashMap.containsKey("uris"));
        assertTrue(rHashMap.containsKey("ipAddress"));
        assertTrue(rHashMap.containsKey("type"));
        assertEquals("127.0.0.1", rHashMap.get("ipAddress"));
        assertEquals("SPARQL", rHashMap.get("type"));
    }

    @Test
    public void getIterator() throws Exception {
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
    public void getUris() throws Exception {
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
}