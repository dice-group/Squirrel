package org.aksw.simba.squirrel.data.uri.filter;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Cursor;
import junit.framework.TestCase;
import org.aksw.simba.squirrel.RethinkDBMockTest;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.junit.Assert;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;


public class RDBKnownUriFilterTest extends TestCase {
    private RethinkDB r;
    private RDBConnector connector;
    private RDBKnownUriFilter filter;

    /**
     * For functionality regarding the starting of rethinkdb container
     */
    private RethinkDBMockTest rethinkDBMockTest;

    public void setUp() throws IOException, InterruptedException {
        r = RethinkDB.r;
        connector = new RDBConnector(RethinkDBMockTest.DB_HOST_NAME, RethinkDBMockTest.DB_PORT);
        filter = new RDBKnownUriFilter(connector, r);

        // to get rethinkdb container running
        rethinkDBMockTest = new RethinkDBMockTest();
        rethinkDBMockTest.setUp();
    }

    public void testGetOutdatedUris() throws URISyntaxException, UnknownHostException, InterruptedException {
        this.connector.open();

        r.dbCreate("squirrel").run(this.connector.connection);
        r.db("squirrel").tableCreate("knownurifilter").run(this.connector.connection);
        r.db("squirrel").table("knownurifilter").indexCreate("uri").run(this.connector.connection);
        r.db("squirrel").table("knownurifilter").indexWait("uri").run(this.connector.connection);

        CrawleableUri uri1 = new CrawleableUri(new URI("http://www.google.de"), InetAddress.getByName("192.168.100.1"));
        CrawleableUri uri2 = new CrawleableUri(new URI("http://www.upb.de"), InetAddress.getByName("192.168.100.1"));
        filter.add(uri1, System.currentTimeMillis() - 10);
        filter.add(uri2, System.currentTimeMillis() + 50000);

        Cursor<String> c = r.db("squirrel")
            .table("knownurifilter").getAll().g("uri").run(connector.connection);

        while (c.hasNext()) {
            System.out.println(c.next());
        }

        List<CrawleableUri> uris = filter.getOutdatedUris();

        Assert.assertEquals(1, uris.size());
        Assert.assertEquals(uri1, uris.get(0));
    }

    public void tearDown() throws IOException {
        rethinkDBMockTest.tearDown();
    }
}
