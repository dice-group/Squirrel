package org.aksw.simba.squirrel.data.uri.filter;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ivan on 8/18/16.
 */
public class RDBKnownUriFilter implements KnownUriFilter, Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBKnownUriFilter.class);

    private RDBConnector connector = null;
    private Integer recrawlEveryWeek = 60 * 60 * 24 * 7 * 1000; //in miiliseconds
    private RethinkDB r;

    private static final String COLUMN_TIMESTAMP_NEXT_CRAWL = "timestampNextCrawl";

    public RDBKnownUriFilter(String hostname, Integer port) {
        this.connector = new RDBConnector(hostname, port);
        r = RethinkDB.r;
    }

    public RDBKnownUriFilter(RDBConnector connector, RethinkDB r) {
        this.connector = connector;
        this.r = r;
    }

    public void open() {
        this.connector.open();
        if (!connector.squirrelDatabaseExists())
            r.dbCreate("squirrel").run(this.connector.connection);
        if (!knownUriFilterTableExists()) {
            r.db("squirrel").tableCreate("knownurifilter").run(this.connector.connection);
            r.db("squirrel").table("knownurifilter").indexCreate("uri").run(this.connector.connection);
            r.db("squirrel").table("knownurifilter").indexWait("uri").run(this.connector.connection);
        }
    }

    @Override
    public List<CrawleableUri> getOutdatedUris() {

        Cursor<HashMap> cursor = r.db("squirrel")
            .table("knownurifilter")
            .filter(doc -> doc.getField("timestampNextCrawl").le(System.currentTimeMillis()))
            .run(connector.connection);

        List<CrawleableUri> urisToRecrawl = new ArrayList<>();
        while (cursor.hasNext()) {
            try {
                HashMap row = cursor.next();
                String s = (String) row.get("ipAddress");
                s = s.split("/")[1];
                urisToRecrawl.add(new CrawleableUri(new URI((String) row.get("uri")), InetAddress.getByName(s)));
            } catch (URISyntaxException | UnknownHostException e) {
                LOGGER.warn(e.toString());
            }
        }
        cursor.close();
        return urisToRecrawl;
    }

    public boolean knownUriFilterTableExists() {
        return this.connector.tableExists("squirrel", "knownurifilter");
    }

    public void close() {
        r.db("squirrel").tableDrop("knownurifilter").run(this.connector.connection);
        this.connector.close();
    }

    @Override
    public void add(CrawleableUri uri, long nextCrawlTimestamp) {
        add(uri, System.currentTimeMillis(), nextCrawlTimestamp);
    }

    @Override
    public void add(CrawleableUri uri, long lastCrawlTimestamp, long nextCrawlTimestamp) {
        r.db("squirrel")
            .table("knownurifilter")
            .insert(convertURITimestampToRDB(uri, lastCrawlTimestamp, nextCrawlTimestamp))
            .run(connector.connection);
        LOGGER.debug("Adding URI {} to the known uri filter list", uri.toString());
    }

    private MapObject convertURIToRDB(CrawleableUri uri) {
        InetAddress ipAddress = uri.getIpAddress();
        URI uriPath = uri.getUri();
        UriType uriType = uri.getType();
        return r.hashMap("uri", uriPath.toString())
            .with("ipAddress", ipAddress.toString())
            .with("type", uriType.toString());
    }

    private MapObject convertURITimestampToRDB(CrawleableUri uri, long timestamp, long nextCrawlTimestamp) {
        MapObject uriMap = convertURIToRDB(uri);
        return uriMap
            .with("timestamp", timestamp).with(COLUMN_TIMESTAMP_NEXT_CRAWL, nextCrawlTimestamp);
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        Cursor<Long> cursor = r.db("squirrel")
            .table("knownurifilter")
            .getAll(uri.getUri().toString())
            .optArg("index", "uri")
            .g("timestamp")
            .run(connector.connection);
        if (cursor.hasNext()) {
            LOGGER.debug("URI {} is not good", uri.toString());
            Long timestampRetrieved = cursor.next();
            cursor.close();
            if ((System.currentTimeMillis() - timestampRetrieved) < recrawlEveryWeek) {
                return false;
            } else {
                return true;
            }
        } else {
            LOGGER.debug("URI {} is good", uri.toString());
            cursor.close();
            return true;
        }
    }

    public void purge() {
        r.db("squirrel").table("knownurifilter").delete().run(connector.connection);
    }

    public class Tuple {
        long x, y;

        Tuple(long x, long y) {
            this.x = x;
            this.y = y;
        }
    }
}
