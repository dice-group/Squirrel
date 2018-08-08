package org.aksw.simba.squirrel.queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aksw.simba.squirrel.MongoDBBasedTest;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class MongoDBQueueTest  extends MongoDBBasedTest{

    private List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
    private MongoDBQueue mongodbQueue;

    @Before
    public void setUp() throws Exception {
    	mongodbQueue = new MongoDBQueue("localhost", 58027);
    	
//    	mongodbQueue = new MongoDBQueue("localhost", 27017);

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
        mongodbQueue.open();
//        assertTrue("squirrel database was created", mongodbQueue.squirrelDatabaseExists());
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
    public void packTuple() throws Exception {
        List rArray = mongodbQueue.packTuple("192.168.1.1", "http://localhost");
        assertTrue(rArray.contains("192.168.1.1"));
        assertTrue(rArray.contains("http://localhost"));
        rArray = mongodbQueue.packTuple("192.168.1.1", "DUMP");
        assertTrue(rArray.contains("192.168.1.1"));
        assertTrue(rArray.contains("DUMP"));
    }

    @Test
    public void getIpAddressTypeKey() throws Exception {
        List rArray = mongodbQueue.getIpAddressTypeKey(uris.get(0));
        String arrayString = rArray.toString();
        assertTrue(arrayString, rArray.contains("127.0.0.1"));
        assertFalse(arrayString, rArray.contains("http://danbri.org/foaf.rdf"));
        assertTrue(arrayString, rArray.contains("SPARQL"));
    }

    @Test
    public void queueContainsIpAddressTypeKey() throws Exception {
        mongodbQueue.open();
        List iatKey = mongodbQueue.getIpAddressTypeKey(uris.get(0));
        assertFalse(mongodbQueue.queueContainsIpAddressTypeKey(null,iatKey));
        mongodbQueue.addToQueue(uris.get(0));
        assertTrue(mongodbQueue.queueContainsIpAddressTypeKey(null,iatKey));
        mongodbQueue.close();
    }

    @Test
    public void purgeQueue() throws Exception {
        mongodbQueue.open();
        mongodbQueue.purge();
        assertEquals(0, mongodbQueue.length());
        for (CrawleableUri uri : uris) {
            mongodbQueue.addToQueue(uri);
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
        mongodbQueue.addCrawleableUri(uris.get(1));
        assertEquals(1, mongodbQueue.length());
        List iatKey = mongodbQueue.getIpAddressTypeKey(uris.get(2));
        mongodbQueue.addCrawleableUri(uris.get(2), iatKey);
        assertEquals(1, mongodbQueue.length());
        mongodbQueue.close();
    }

    @Test
    public void addToQueue() throws Exception {
        mongodbQueue.open();
        for (CrawleableUri uri : uris) {
            mongodbQueue.addToQueue(uri);
        }
        assertEquals(3, mongodbQueue.length());
        mongodbQueue.close();
    }


    @Test
    public void getIterator() throws Exception {
        mongodbQueue.open();
        for (CrawleableUri uri : uris) {
            mongodbQueue.addToQueue(uri);
        }
        Iterator<IpUriTypePair> iter = mongodbQueue.getIterator();
        while (iter.hasNext()) {
            IpUriTypePair pair = iter.next();
            System.out.println(pair.toString());
        }
        mongodbQueue.close();
    }

    @Test
    public void getUris() throws Exception {
        mongodbQueue.open();
        for (CrawleableUri uri : uris) {
            mongodbQueue.addToQueue(uri);
        }
        Iterator<IpUriTypePair> iter = mongodbQueue.getIterator();
        while (iter.hasNext()) {
            IpUriTypePair pair = iter.next();
            List<CrawleableUri> uriList = mongodbQueue.getUris(pair);
            for (CrawleableUri uri : uriList) {
                assertTrue(uris.contains(uri));
            }
        }
        mongodbQueue.close();
    }
}
