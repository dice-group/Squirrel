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
import java.util.List;

/**
 * Created by ivan on 8/18/16.
 */
public class RDBKnownUriFilterWithoutReferences implements KnownUriFilter, Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBKnownUriFilterWithoutReferences.class);
    static final String RDBTABLENAME = "knownurifilter";
    static final String RDBDATABASENAME = "squirrel";

    protected RDBConnector connector;
    private Integer recrawlEveryWeek = 60 * 60 * 24 * 7 * 1000; //in milliseconds
    protected RethinkDB r = RethinkDB.r;

    public RDBKnownUriFilterWithoutReferences(String hostname, Integer port) {
        this.connector = new RDBConnector(hostname, port);
    }

    public void open() {
        this.connector.open();
        if(!connector.squirrelDatabaseExists())
            r.dbCreate(RDBTABLENAME).run(this.connector.connection);
        if(!knownUriFilterTableExists()) {
            r.db(RDBDATABASENAME).tableCreate(RDBTABLENAME).run(this.connector.connection);
            r.db(RDBDATABASENAME).table(RDBTABLENAME).indexCreate("uri").run(this.connector.connection);
            r.db(RDBDATABASENAME).table(RDBTABLENAME).indexWait("uri").run(this.connector.connection);
        }
    }

    public boolean knownUriFilterTableExists() {
        return this.connector.tableExists(RDBDATABASENAME, RDBTABLENAME);
    }

    public void close() {
        r.db(RDBDATABASENAME).tableDrop(RDBTABLENAME).run(this.connector.connection);
        this.connector.close();
    }

    @Override
    public void add(CrawleableUri uri) {
        add(uri, System.currentTimeMillis());
    }

    @Override
    public void add(CrawleableUri uri, long timestamp) {
        r.db(RDBDATABASENAME)
            .table(RDBTABLENAME)
                .insert(convertURITimestampToRDB(uri, timestamp))
                .run(connector.connection);
        LOGGER.debug("Adding URI {} to the known uri filter list", uri.toString());
    }

    @Override
    public void add(CrawleableUri uri, List<CrawleableUri> urisFound, long timestamp) {
        add(uri, timestamp);
    }

    protected MapObject convertURIToRDB(CrawleableUri uri) {
        InetAddress ipAddress = uri.getIpAddress();
        URI uriPath = uri.getUri();
        UriType uriType = uri.getType();
        return r.hashMap("uri", uriPath.toString())
                .with("ipAddress", ipAddress.toString())
                .with("type", uriType.toString());
    }

    protected MapObject convertURITimestampToRDB(CrawleableUri uri, long timestamp) {
        MapObject uriMap = convertURIToRDB(uri);
        return uriMap
                .with("timestamp", timestamp);
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        Cursor<Long> cursor = r.db(RDBDATABASENAME)
            .table(RDBTABLENAME)
                .getAll(uri.getUri().toString())
                .optArg("index", "uri")
                .g("timestamp")
                .run(connector.connection);
        if(cursor.hasNext()) {
            LOGGER.debug("URI {} is not good", uri.toString());
            Long timestampRetrieved = cursor.next();
            cursor.close();
            return (System.currentTimeMillis() - timestampRetrieved) >= recrawlEveryWeek;
        } else {
            LOGGER.debug("URI {} is good", uri.toString());
            cursor.close();
            return true;
        }
    }

    public void purge() {
        r.db(RDBDATABASENAME).table(RDBTABLENAME).delete().run(connector.connection);
    }

    @Override
    public long count() {
        return r.db(RDBDATABASENAME).table(RDBTABLENAME).count().run(connector.connection);
    }
}
