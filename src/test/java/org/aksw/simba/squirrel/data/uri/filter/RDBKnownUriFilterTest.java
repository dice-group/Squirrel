package org.aksw.simba.squirrel.data.uri.filter;

import com.rethinkdb.RethinkDB;
import junit.framework.TestCase;
import org.aksw.simba.squirrel.RethinkDBMockTest;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;


public class RDBKnownUriFilterTest {
    private RethinkDB r;
    private RDBConnector connector;
    private RDBKnownUriFilter filter;

    /**
     * For functionality regarding the starting of rethinkdb container
     */
    private RethinkDBMockTest rethinkDBMockTest;

    @Before
    public void setUp() throws IOException, InterruptedException {
        r = RethinkDB.r;
        connector = new RDBConnector(RethinkDBMockTest.DB_HOST_NAME, RethinkDBMockTest.DB_PORT);
        filter = new RDBKnownUriFilter(connector, r);

        // to get rethinkdb container running
        rethinkDBMockTest = new RethinkDBMockTest();
        rethinkDBMockTest.setUp();
    }

    @Test
    public void testGetOutdatedUris() throws URISyntaxException, UnknownHostException {
        this.connector.open();

        r.dbCreate(RDBKnownUriFilter.DATABASE_NAME).run(this.connector.connection);
        r.db(RDBKnownUriFilter.DATABASE_NAME).tableCreate(RDBKnownUriFilter.TABLE_NAME).run(this.connector.connection);
        r.db(RDBKnownUriFilter.DATABASE_NAME).table(RDBKnownUriFilter.TABLE_NAME).indexCreate(RDBKnownUriFilter.COLUMN_URI).run(this.connector.connection);
        r.db(RDBKnownUriFilter.DATABASE_NAME).table(RDBKnownUriFilter.TABLE_NAME).indexWait(RDBKnownUriFilter.COLUMN_URI).run(this.connector.connection);

        CrawleableUri uri1 = new CrawleableUri(new URI("http://www.google.de"), InetAddress.getByName("192.168.100.1"));
        CrawleableUri uri2 = new CrawleableUri(new URI("http://www.upb.de"), InetAddress.getByName("192.168.100.1"));
        filter.add(uri1, System.currentTimeMillis() - 10);
        filter.add(uri2, System.currentTimeMillis() + 50000);

        List<CrawleableUri> uris = filter.getOutdatedUris();

        Assert.assertEquals(1, uris.size());
        Assert.assertEquals(uri1, uris.get(0));
    }

    @After
    public void tearDown() throws IOException {
        rethinkDBMockTest.tearDown();
    }
}
