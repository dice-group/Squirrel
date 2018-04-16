package org.aksw.simba.squirrel.model;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ivan on 8/18/16.
 */
public class RDBConnectorTest {
    Connection connection;
    RDBConnector rdbConnector = null;
    RethinkDB r = RethinkDB.r;

    private CrawleableUriFactory4Tests factory = new CrawleableUriFactory4Tests();

    @Before
    public void init() throws IOException, InterruptedException {
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

        String RDBHost = "localhost";
        Integer RDBPort = 58015;

        rdbConnector = new RDBConnector(RDBHost, RDBPort);
        rdbConnector.open();

        try {
            r.dbCreate("squirrel").run(rdbConnector.connection);
            r.db("squirrel").tableCreate("knownurifilter").run(rdbConnector.connection);
            r.db("squirrel").table("knownurifilter").indexCreate("uri").run(rdbConnector.connection);
            r.db("squirrel").table("knownurifilter").indexWait("uri").run(rdbConnector.connection);

            r.db("squirrel").tableCreate("queue").run(rdbConnector.connection);
            r.db("squirrel").table("queue").indexCreate("ipAddressType",
                    row -> r.array(row.g("ipAddress"), row.g("type"))).run(rdbConnector.connection);
            r.db("squirrel").table("queue").indexWait("ipAddressType").run(rdbConnector.connection);
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    @After
    public void teardown() throws IOException, InterruptedException {
        r.dbDrop("squirrel").run(rdbConnector.connection);
        rdbConnector.close();
        String rethinkDockerStopCommand = "docker stop squirrel-test-rethinkdb";
        Process p = Runtime.getRuntime().exec(rethinkDockerStopCommand);
        p.waitFor();
        String rethinkDockerRmCommand = "docker rm squirrel-test-rethinkdb";
        p = Runtime.getRuntime().exec(rethinkDockerRmCommand);
        p.waitFor();
    }

    @Test
    public void testInsertKnownURIFilter() throws UnknownHostException {
        CrawleableUri uri = factory.create(URI.create("http://example.org/uri_1"), InetAddress.getByName("127.0.0.1"),
                UriType.UNKNOWN);
        long timestamp = System.currentTimeMillis();

        r.db("squirrel").table("knownurifilter").insert(convertURITimestampToRDB(uri, timestamp)).run(rdbConnector.connection);
    }

    @Test
    public void testHasKnownURI() throws UnknownHostException {
        CrawleableUri uri = factory.create(URI.create("http://example.org/uri_1"), InetAddress.getByName("127.0.0.1"),
                UriType.UNKNOWN);
        long timestamp = System.currentTimeMillis();

        r.db("squirrel").table("knownurifilter").insert(convertURITimestampToRDB(uri, timestamp)).run(rdbConnector.connection);

        Cursor cursor = r.db("squirrel").table("knownurifilter").getAll(uri.getUri().toString()).optArg("index", "uri").run(rdbConnector.connection);
        assert(cursor.hasNext());
        cursor.close();
    }

    @Test
    public void testHasOutdatedKnownURI() throws UnknownHostException {
        CrawleableUri uri = factory.create(URI.create("http://example.org/uri_1"), InetAddress.getByName("127.0.0.1"),
                UriType.UNKNOWN);
        long timestamp = 100000;
        long invalidationTime = 100000;

        r.db("squirrel").table("knownurifilter").insert(convertURITimestampToRDB(uri, timestamp)).run(rdbConnector.connection);

        Cursor cursor = r.db("squirrel")
                .table("knownurifilter")
                .getAll(uri.getUri().toString())
                .optArg("index", "uri")
                //.g("timestamp")
                .run(rdbConnector.connection);
        assert(cursor.hasNext());
        HashMap crawleableUri = (HashMap) cursor.next();
        long retrievedTimestamp = (long) crawleableUri.get("timestamp");
        assert ((System.currentTimeMillis() - retrievedTimestamp) > invalidationTime);
        cursor.close();
    }

    @Test
    public void testHasNoKnownURI() throws UnknownHostException {
        CrawleableUri uri = factory.create(URI.create("http://example.org/uri_1"), InetAddress.getByName("127.0.0.1"),
                UriType.UNKNOWN);
        long timestamp = System.currentTimeMillis();

        Cursor cursor = r.db("squirrel").table("knownurifilter").getAll(uri.getUri().toString()).optArg("index", "uri").run(rdbConnector.connection);
        assert(!cursor.hasNext());
    }

    @Test
    public void testAddUriToQueue() throws UnknownHostException {
        CrawleableUri uri_1 = factory.create(URI.create("http://example.org/uri_1"), InetAddress.getByName("127.0.0.1"),
                UriType.UNKNOWN);
        CrawleableUri uri_2 = factory.create(URI.create("http://example.org/uri_2"), InetAddress.getByName("127.0.0.1"),
                UriType.UNKNOWN);

        addToQueue(uri_1);
        addToQueue(uri_2);

        Cursor cursor = r.db("squirrel")
                .table("queue")
                .getAll(r.array(uri_1.getIpAddress().toString(), uri_1.getType().toString()))
                .optArg("index", "ipAddressType")
                .run(rdbConnector.connection);

        for (Object doc : cursor) {
            System.out.println(doc);
        }
    }

    @Test
    public void testConvertGetUrisFromQueue() throws UnknownHostException {
        CrawleableUri uri_1 = factory.create(URI.create("http://example.org/uri_1"), InetAddress.getByName("127.0.0.1"),
                UriType.DUMP);
        CrawleableUri uri_2 = factory.create(URI.create("http://example.org/uri_2"), InetAddress.getByName("127.0.0.1"),
                UriType.DEREFERENCEABLE);

        addToQueue(uri_1);
        addToQueue(uri_2);

        Cursor cursor = r.db("squirrel")
                .table("queue")
                .getAll(r.array(uri_1.getIpAddress().toString(), uri_1.getType().toString()))
                .optArg("index", "ipAddressType")
                .run(rdbConnector.connection);

        HashMap result = (HashMap) cursor.next();
        ArrayList uris = (ArrayList) result.get("uris");
        UriType type = UriType.valueOf(result.get("type").toString());
        List<CrawleableUri> crawleableUris = UriUtils.createCrawleableUriList(uris, type);
        System.out.println(crawleableUris);
    }

    private void addToQueue(CrawleableUri uri) {
        List ipAddressTypeKey = r.array(uri.getIpAddress().toString(), uri.getType().toString());
        Cursor cursor = r.db("squirrel")
                .table("queue")
                .getAll(ipAddressTypeKey)
                .optArg("index", "ipAddressType")
                .run(rdbConnector.connection);
        // if URI exists update the uriDatePairs list
        if(cursor.hasNext()) {
            r.db("squirrel")
                    .table("queue")
                    .getAll(ipAddressTypeKey)
                    .optArg("index", "ipAddressType")
                    .update(queueItem -> r.hashMap("uris", queueItem.g("uris").append(uri.getUri().toString())))
                            .run(rdbConnector.connection);
        } else {
            r.db("squirrel")
                    .table("queue")
                    .insert(convertURItoRDBQueue(uri))
                    .run(rdbConnector.connection);
        }
    }

    private MapObject convertURItoRDBQueue(CrawleableUri uri) {
        InetAddress ipAddress = uri.getIpAddress();
        URI uriPath = uri.getUri();
        UriType uriType = uri.getType();
        return r.hashMap("uris", r.array(uriPath.toString()))
                .with("ipAddress", ipAddress.toString())
                .with("type", uriType.toString());
    }

    private MapObject convertURIToRDB(CrawleableUri uri) {
        InetAddress ipAddress = uri.getIpAddress();
        URI uriPath = uri.getUri();
        UriType uriType = uri.getType();
        return r.hashMap("uri", uriPath.toString())
                .with("ipAddress", ipAddress.toString())
                .with("type", uriType.toString());
    }

    private MapObject convertURITimestampToRDB(CrawleableUri uri, long timestamp) {
        MapObject uriMap = convertURIToRDB(uri);
        return uriMap
                .with("timestamp", timestamp);
    }
}
