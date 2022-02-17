package org.dice_research.squirrel.queue.domainbased;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dice_research.squirrel.MongoDBBasedTest;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.dice_research.squirrel.data.uri.UriType;
import org.dice_research.squirrel.data.uri.UriUtils;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({ "deprecation" })
public class MongoDBDomainQueueTest extends MongoDBBasedTest {

    private List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
    private String expectedDomains[];
    private MongoDBDomainBasedQueue mongodbQueue;

    @Before
    public void setUp() throws Exception {
        mongodbQueue = new MongoDBDomainBasedQueue("localhost", 58027,false);

        CrawleableUriFactory4Tests cuf = new CrawleableUriFactory4Tests();
        uris.add(cuf.create(new URI("http://localhost/sparql"), InetAddress.getByName("127.0.0.1"), UriType.SPARQL));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/New_York"), InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE));
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/Moscow"), InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE));
        // Added this to check https://github.com/AKSW/Squirrel/issues/17
        uris.add(cuf.create(new URI("http://danbri.org/foaf.rdf"),
                InetAddress.getByName((new URI("http://danbri.org/foaf.rdf")).toURL().getHost()), UriType.DUMP));
        
        expectedDomains = new String[] {"danbri.org", "dbpedia.org", "localhost"};
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
}
