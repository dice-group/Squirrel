package org.aksw.simba.squirrel.queue;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.URI;
import java.util.*;

public class RDBQueue extends AbstractIpAddressBasedQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBQueue.class);

    protected RDBConnector connector;
    private RethinkDB r = RethinkDB.r;

    public void open() {
        this.connector.open();
        try {
            r.dbCreate("squirrel");
        } catch (Exception e) {
            LOGGER.debug(e.toString());
        }
        r.db("squirrel").tableCreate("queue").run(this.connector.connection);
        r.db("squirrel").table("queue").indexCreate("ipAddressType",
                row -> r.array(row.g("ipAddress"), row.g("type"))).run(this.connector.connection);
        r.db("squirrel").table("queue").indexWait("ipAddressType").run(this.connector.connection);
    }

    public void close() {
        r.db("squirrel").tableDrop("queue").run(this.connector.connection);
        this.connector.close();
    }

    public RDBQueue(String hostname, Integer port) {
        connector = new RDBConnector(hostname, port);
    }

    @Override
    protected void addToQueue(CrawleableUri uri) {
        List ipAddressTypeKey = convertCrawleableUriToIpAddressTypeKey(uri);
        Boolean queueContainsKey = containsKey(ipAddressTypeKey);
        // if URI exists update the uris list
        if(queueContainsKey) {
            insertExistingUriTypePair(ipAddressTypeKey, uri);
        } else {
            insertNewUriTypePair(uri);
        }
    }

    private void insertExistingUriTypePair(List ipAddressTypeKey, CrawleableUri uri) {
        r.db("squirrel")
                .table("queue")
                .getAll(ipAddressTypeKey)
                .optArg("index", "ipAddressType")
                .update(queueItem -> r.hashMap("uris", queueItem.g("uris").append(uri.getUri().toString())))
                .run(connector.connection);
    }

    private void insertNewUriTypePair(CrawleableUri uri) {
        r.db("squirrel")
                .table("queue")
                .insert(convertURItoRDBQueue(uri))
                .run(connector.connection);
    }

    private MapObject convertURItoRDBQueue(CrawleableUri uri) {
        InetAddress ipAddress = uri.getIpAddress();
        URI uriPath = uri.getUri();
        UriType uriType = uri.getType();
        return r.hashMap("uris", r.array(uriPath.toString()))
                .with("ipAddress", ipAddress.toString())
                .with("type", uriType.toString());
    }

    @Override
    protected Iterator<IpUriTypePair> getIterator() {
        Cursor cursor = r.db("squirrel")
                .table("queue")
                .orderBy()
                .optArg("index", "ipAddressType")
                .run(connector.connection);
        return cursor;
    }

    @Override
    protected List<CrawleableUri> getUris(IpUriTypePair pair) {
        List<CrawleableUri> uris = null;

        List ipAddressTypeKey = convertIpAddressTypeToList(pair.ip.toString(), pair.type.toString());
        Cursor cursor = r.db("squirrel")
                .table("queue")
                .getAll(ipAddressTypeKey)
                .optArg("index", "ipAddressType")
                .run(connector.connection);

        if (cursor.hasNext()) {
            //remove all URIs for the pair
            HashMap result = (HashMap) cursor.next();
            ArrayList uriStringList = (ArrayList) result.get("uris");
            uris = UriUtils.createCrawleableUriList(uriStringList);
            //remove from the queue
            r.db("squirrel")
                    .table("queue")
                    .getAll(ipAddressTypeKey)
                    .optArg("index", "ipAddressType")
                    .delete()
                    .run(connector.connection);
        }
        // return the URIs
        return uris;
    }

    private Boolean containsKey(List ipAddressTypeKey) {
        Cursor cursor = r.db("squirrel")
                .table("queue")
                .getAll(ipAddressTypeKey)
                .optArg("index", "ipAddressType")
                .run(connector.connection);
        if(cursor.hasNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    private List convertCrawleableUriToIpAddressTypeKey(CrawleableUri uri) {
        return convertIpAddressTypeToList(uri.getIpAddress().toString(), uri.getType().toString());
    }

    private List convertIpAddressTypeToList(String ipAddress, String uri) {
        return r.array(ipAddress, uri);
    }
}
