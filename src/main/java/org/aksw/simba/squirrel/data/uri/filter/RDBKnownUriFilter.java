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

/**
 * Created by ivan on 8/18/16.
 */
public class RDBKnownUriFilter implements KnownUriFilter, Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBKnownUriFilter.class);

    private RDBConnector connector = null;
    private Integer recrawlEveryWeek = 60 * 60 * 24 * 7 * 1000; //in miiliseconds
    private RethinkDB r = RethinkDB.r;

    public RDBKnownUriFilter(String hostname, Integer port) {
        this.connector = new RDBConnector(hostname, port);
    }

    public void open() {
        this.connector.open();
        if(!connector.squirrelDatabaseExists())
            r.dbCreate("squirrel").run(this.connector.connection);
        if(!knownUriFilterTableExists()) {
            r.db("squirrel").tableCreate("knownurifilter").run(this.connector.connection);
            r.db("squirrel").table("knownurifilter").indexCreate("uri").run(this.connector.connection);
            r.db("squirrel").table("knownurifilter").indexWait("uri").run(this.connector.connection);
        }
    }

    public boolean knownUriFilterTableExists() {
        return this.connector.tableExists("squirrel", "knownurifilter");
    }

    public void close() {
        r.db("squirrel").tableDrop("knownurifilter").run(this.connector.connection);
        this.connector.close();
    }

    @Override
    public void add(CrawleableUri uri) {
        add(uri, System.currentTimeMillis());
    }

    @Override
    public void add(CrawleableUri uri, long timestamp) {
        r.db("squirrel")
                .table("knownurifilter")
                .insert(convertURITimestampToRDB(uri, timestamp))
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

    private MapObject convertURITimestampToRDB(CrawleableUri uri, long timestamp) {
        MapObject uriMap = convertURIToRDB(uri);
        return uriMap
                .with("timestamp", timestamp);
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        Cursor<Long> cursor = r.db("squirrel")
                .table("knownurifilter")
                .getAll(uri.getUri().toString())
                .optArg("index", "uri")
                .g("timestamp")
                .run(connector.connection);
        if(cursor.hasNext()) {
            LOGGER.debug("URI {} is not good", uri.toString());
            Long timestampRetrieved = cursor.next();
            cursor.close();
            if((System.currentTimeMillis() - timestampRetrieved) < recrawlEveryWeek) {
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
}
