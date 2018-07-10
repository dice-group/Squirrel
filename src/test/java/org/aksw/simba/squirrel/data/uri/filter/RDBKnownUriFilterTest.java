package org.aksw.simba.squirrel.data.uri.filter;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;


public class RDBKnownUriFilterTest {

    Connection connection;
    RDBConnector rdbConnector = null;
    RethinkDB r = RethinkDB.r;
    public static final String DB_HOST_NAME = "localhost";
    public static final int DB_PORT = 58015;
    RDBKnownUriFilter filter = new RDBKnownUriFilter("localhost", 58015);

    private CrawleableUri curi;



    @Test
    public void testIsUriGood() throws URISyntaxException, UnknownHostException {
        String uriToFetch = "http://example.org/uri_1";
        curi = new CrawleableUri(new URI(uriToFetch));
        curi.setIpAddress(InetAddress.getByName("127.0.0.1"));
        curi.setType(UriType.DEREFERENCEABLE);
        RDBKnownUriFilter filter = new RDBKnownUriFilter("localhost", 58015);
        boolean var = filter.isUriGood(curi);
        Assert.assertEquals(true, var);

    }

    @Test
    public void testIsUriNotGood() throws URISyntaxException, UnknownHostException {
        String uriToFetch = "/uri_1";
        curi = new CrawleableUri(new URI(uriToFetch));
        curi.setIpAddress(InetAddress.getByName("127.0.0.1"));
        curi.setType(UriType.DEREFERENCEABLE);
        boolean var = filter.isUriGood(curi);
        Assert.assertEquals(false, var);

    }


}
