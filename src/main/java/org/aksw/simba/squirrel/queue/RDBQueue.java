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
import java.net.UnknownHostException;
import java.util.*;

public class RDBQueue extends AbstractIpAddressBasedQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBQueue.class);

    protected RDBConnector connector;
    private RethinkDB r = RethinkDB.r;

    public void open() {
        this.connector.open();
        try {
            r.dbCreate("squirrel").run(this.connector.connection);
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
            LOGGER.debug("TypeKey is in the queue already");
            insertExistingUriTypePair(ipAddressTypeKey, uri);
        } else {
            LOGGER.debug("TypeKey is not in the queue, creating a new one");
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
        LOGGER.debug("Inserted existing UriTypePair");
    }

    private void insertNewUriTypePair(CrawleableUri uri) {
        r.db("squirrel")
                .table("queue")
                .insert(convertURItoRDBQueue(uri))
                .run(connector.connection);
        LOGGER.debug("Inserted new UriTypePair");
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
        Iterator<IpUriTypePair> ipUriTypePairIterator = new Iterator<IpUriTypePair>() {
            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            @Override
            public IpUriTypePair next() {
                HashMap map = (HashMap) cursor.next();
                try {
                    InetAddress ipAddress = InetAddress.getByName(map.get("ipAddress").toString().substring(1));
                    UriType uriType = UriType.valueOf(map.get("type").toString());
                    IpUriTypePair pair = new IpUriTypePair(ipAddress, uriType);
                    return pair;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        return ipUriTypePairIterator;
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
            LOGGER.debug("query result {}",result.toString());
            UriType type = UriType.valueOf(result.get("type").toString());
            uris = UriUtils.createCrawleableUriList(uriStringList, type);
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
