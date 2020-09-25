package org.dice_research.squirrel.data.uri.filter;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

import org.dice_research.squirrel.MongoDBBasedTest;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;


/**
 * 
 * Test Commented only for release
 * 
 * @author gsjunior
 *
 */
@Ignore
public class MongoDBKnownUriFilterTest {

    private MongoDBKnowUriFilter filter;

    /**
     * For functionality regarding the starting of rethinkdb container.
     */
    private MongoClient client;

    @Before
    public void setUp() throws Exception {

        
        filter = new MongoDBKnowUriFilter(MongoDBBasedTest.DB_HOST_NAME,27017);
        filter.open();
        MongoDBBasedTest.tearDownMDB();
        MongoDBBasedTest.setUpMDB();
    }

    @Test
    public void testGetOutdatedUris() throws URISyntaxException, UnknownHostException {
        client = new MongoClient(MongoDBBasedTest.DB_HOST_NAME,MongoDBBasedTest.DB_PORT);
        
        MongoDatabase db = client.getDatabase(MongoDBKnowUriFilter.DB_NAME);
        
        
        
//        db.createCollection(MongoDBKnowUriFilter.COLLECTION_NAME);
        db.getCollection(MongoDBKnowUriFilter.COLLECTION_NAME).createIndex(Indexes.compoundIndex(Indexes.ascending("uri")));
        
//        r.dbCreate(RDBKnownUriFilter.DATABASE_NAME).run(this.connector.connection);
//        r.db(RDBKnownUriFilter.DATABASE_NAME).tableCreate(RDBKnownUriFilter.TABLE_NAME).run(this.connector.connection);
//        r.db(RDBKnownUriFilter.DATABASE_NAME).table(RDBKnownUriFilter.TABLE_NAME).indexCreate(RDBKnownUriFilter.COLUMN_URI).run(this.connector.connection);
//        r.db(RDBKnownUriFilter.DATABASE_NAME).table(RDBKnownUriFilter.TABLE_NAME).indexWait(RDBKnownUriFilter.COLUMN_URI).run(this.connector.connection);

        CrawleableUri uri1 = new CrawleableUri(new URI("http://www.google.de"), InetAddress.getByName("192.168.100.1"));
        CrawleableUri uri2 = new CrawleableUri(new URI("http://www.upb.de"), InetAddress.getByName("192.168.100.1"));
        filter.add(uri1, System.currentTimeMillis() - 10);
        filter.add(uri2, System.currentTimeMillis() + 50000);

        // filter must return uri1 as it is outdated
        List<CrawleableUri> uris = filter.getOutdatedUris();
        Assert.assertEquals(1, uris.size());
        Assert.assertEquals(uri1, uris.get(0));

        // set crawlingInProcess to true for uri1
//        Cursor<Boolean> cursor = r.db(RDBKnownUriFilter.DATABASE_NAME).table(RDBKnownUriFilter.TABLE_NAME).
//            filter(doc -> doc.getField(RDBKnownUriFilter.COLUMN_URI).eq(uri1.getUri().toString())).
//            getField(RDBKnownUriFilter.COLUMN_CRAWLING_IN_PROCESS).run(connector.connection);
//
//        // check if flag is true for uri1
//        Assert.assertTrue(cursor.next());
//
//        cursor = r.db(RDBKnownUriFilter.DATABASE_NAME).table(RDBKnownUriFilter.TABLE_NAME).
//            filter(doc -> doc.getField(RDBKnownUriFilter.COLUMN_URI).eq(uri2.getUri().toString())).
//            getField(RDBKnownUriFilter.COLUMN_CRAWLING_IN_PROCESS).run(connector.connection);
//
//        // check if flag is still false for uri2
//        Assert.assertFalse(cursor.next());
//
//
//        // filter must return nothing now
//        uris = filter.getOutdatedUris();
//        Assert.assertTrue(uris.isEmpty());
//
//        // manipulate lastCrawlTimestamp so that uri will be returned by filter
//        r.db(RDBKnownUriFilter.DATABASE_NAME).table(RDBKnownUriFilter.TABLE_NAME).
//            filter(doc -> doc.getField(RDBKnownUriFilter.COLUMN_URI).eq(uri1.getUri().toString())).
//            update(r.hashMap(RDBKnownUriFilter.COLUMN_TIMESTAMP_LAST_CRAWL,
//                System.currentTimeMillis() - 10 * FrontierImpl.DEFAULT_GENERAL_RECRAWL_TIME)).run(connector.connection);


        // filter must return uri1 now again
        uris = filter.getOutdatedUris();
        Assert.assertEquals(1, uris.size());
        Assert.assertEquals(uri1, uris.get(0));

//        cursor.close();
    }
//
//    @After
//    public void tearDown() throws Exception {
//        rethinkDBMockTest.tearDownRDB();
//    }

}
