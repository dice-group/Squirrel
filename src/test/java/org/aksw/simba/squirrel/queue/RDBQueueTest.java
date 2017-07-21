package org.aksw.simba.squirrel.queue;

import com.rethinkdb.model.MapObject;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class RDBQueueTest {
    RDBQueue rdbQueue;
    List<CrawleableUri> uris = new ArrayList<CrawleableUri>();

    @Before
    public void setUp() throws Exception {
        //to start up the server run
        //docker run -p 8080:8080 -p 29015:29015 -p 28015:28015 --name rethinkdb -v "$PWD:/data" -it --rm rethinkdb:2.3.5
        rdbQueue = new RDBQueue("localhost", 28015);

        CrawleableUriFactory4Tests cuf = new CrawleableUriFactory4Tests();
        uris.add(
            cuf.create(
                new URI("http://danbri.org/foaf.rdf"),
                InetAddress.getByName("127.0.0.1"),
                UriType.DUMP
            ));
        uris.add(
            cuf.create(
                new URI("http://dbpedia.org/resource/New_York"),
                InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE
            ));
        uris.add(
            cuf.create(
                new URI("http://dbpedia.org/resource/Moscow"),
                InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE
            ));
    }

    @Test
    public void openClose() throws Exception {
        rdbQueue.open();
        List<String> dbList = rdbQueue.getDatabaseList();
        assertTrue("squirrel database was createad", dbList.contains("squirrel"));
        List<String> tableList = rdbQueue.getTableList();
        assertTrue("queue table was created", tableList.contains("queue"));
        rdbQueue.close();
    }

    @Test
    public void packTuple() throws Exception {
        List rArray = rdbQueue.packTuple("/192.168.1.1", "http://localhost");
        assertTrue(rArray.contains("/192.168.1.1"));
        assertTrue(rArray.contains("http://localhost"));
        rArray = rdbQueue.packTuple("/192.168.1.1", "DUMP");
        assertTrue(rArray.contains("/192.168.1.1"));
        assertTrue(rArray.contains("DUMP"));
    }

    @Test
    public void getIpAddressTypeKey() throws Exception {
        List rArray = rdbQueue.getIpAddressTypeKey(uris.get(0));
        assertTrue(rArray.contains("/127.0.0.1"));
        assertFalse(rArray.contains("http://danbri.org/foaf.rdf"));
        assertTrue(rArray.contains("DUMP"));
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
        rdbQueue.addToQueue(uris.get(0));
        assertTrue(rdbQueue.length() == 1);
        rdbQueue.purgeQueue();
        assertTrue(rdbQueue.length() == 0);
        rdbQueue.close();
    }

    @Test
    public void addCrawleableUri() throws Exception {
        rdbQueue.open();
        rdbQueue.purgeQueue();
        rdbQueue.addCrawleableUri(uris.get(1));
        assertTrue(rdbQueue.length() == 1);
        List iatKey = rdbQueue.getIpAddressTypeKey(uris.get(2));
        rdbQueue.addCrawleableUri(uris.get(2), iatKey);
        assertTrue(rdbQueue.length() == 1);
        rdbQueue.close();
    }

    @Test
    public void addToQueue() throws Exception {
        rdbQueue.open();
        for(CrawleableUri uri : uris) {
            rdbQueue.addToQueue(uri);
        }
        assertTrue(rdbQueue.length() == 2);
        rdbQueue.close();
    }

    @Test
    public void crawleableUriToRDBHashMap() throws Exception {
        MapObject rHashMap = rdbQueue.crawleableUriToRDBHashMap(uris.get(0));
        assertTrue(rHashMap.containsKey("uris"));
        assertTrue(rHashMap.containsKey("ipAddress"));
        assertTrue(rHashMap.containsKey("type"));
        assertTrue(rHashMap.get("ipAddress").equals("/127.0.0.1"));
        assertTrue(rHashMap.get("type").equals("DUMP"));
    }

    @Test
    public void getIterator() throws Exception {
        rdbQueue.open();
        for(CrawleableUri uri : uris) {
            rdbQueue.addToQueue(uri);
        }
        Iterator<IpUriTypePair> iter = rdbQueue.getIterator();
        while(iter.hasNext()) {
            IpUriTypePair pair = iter.next();
            System.out.println(pair.toString());
        }
        rdbQueue.close();
    }

    @Test
    public void getUris() throws Exception {
        rdbQueue.open();
        for(CrawleableUri uri : uris) {
            rdbQueue.addToQueue(uri);
        }
        Iterator<IpUriTypePair> iter = rdbQueue.getIterator();
        while(iter.hasNext()) {
            IpUriTypePair pair = iter.next();
            List<CrawleableUri> uriList = rdbQueue.getUris(pair);
            for(CrawleableUri uri : uriList) {
                assertTrue(uris.contains(uri));
            }
        }
        rdbQueue.close();
    }

}
