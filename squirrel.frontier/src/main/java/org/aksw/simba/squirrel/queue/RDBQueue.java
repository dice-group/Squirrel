package org.aksw.simba.squirrel.queue;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.SnappyJavaUriSerializer;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings("rawtypes")
public class RDBQueue extends AbstractIpAddressBasedQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBQueue.class);

    protected RDBConnector connector;
    private RethinkDB r = RethinkDB.r;
    private Serializer serializer;

    public RDBQueue(String hostname, Integer port) {
        this.serializer = new SnappyJavaUriSerializer();
        connector = new RDBConnector(hostname, port);
    }

    public RDBQueue(String hostname, Integer port, Serializer serializer) {
        this.serializer = serializer;
        connector = new RDBConnector(hostname, port);
    }

    public void open() {
        this.connector.open();
        if(!squirrelDatabaseExists()) {
            r.dbCreate("squirrel").run(this.connector.connection);
        }
        if(!queueTableExists()) {
            r.db("squirrel").tableCreate("queue").run(this.connector.connection);
            r.db("squirrel").table("queue").indexCreate("ipAddressType",
                row -> r.array(row.g("ipAddress"), row.g("type"))).run(this.connector.connection);
            r.db("squirrel").table("queue").indexWait("ipAddressType").run(this.connector.connection);
        }
    }

    public void close() {
        r.db("squirrel").tableDrop("queue").run(this.connector.connection);
        this.connector.close();
    }

    public boolean squirrelDatabaseExists() {
        return this.connector.squirrelDatabaseExists();
    }

    public boolean queueTableExists() {
        return this.connector.tableExists("squirrel", "queue");
    }


    public void purge() {
        r.db("squirrel")
            .table("queue")
            .delete()
            .run(connector.connection);
    }

    public long length() {
        return r.db("squirrel")
            .table("queue")
            .count()
            .run(connector.connection);
    }

    @Override
    protected void addToQueue(CrawleableUri uri) {
        List ipAddressTypeKey = getIpAddressTypeKey(uri);
        // if URI exists update the uris list
        if(queueContainsIpAddressTypeKey(ipAddressTypeKey)) {
            LOGGER.debug("TypeKey is in the queue already");
            addCrawleableUri(uri, ipAddressTypeKey);
        } else {
            LOGGER.debug("TypeKey is not in the queue, creating a new one");
            addCrawleableUri(uri);
        }
    }

    public boolean queueContainsIpAddressTypeKey(List ipAddressTypeKey) {
        return !((boolean) r.db("squirrel")
            .table("queue")
            .getAll(ipAddressTypeKey)
            .optArg("index", "ipAddressType")
            .isEmpty()
            .run(connector.connection));
    }

    @SuppressWarnings("unchecked")
    public List<String> getIpAddressTypeKey(CrawleableUri uri) {
        return packTuple(uri.getIpAddress().getHostAddress(), uri.getType().toString());
    }

    public List packTuple(String str_1, String str_2) {
        return r.array(str_1, str_2);
    }

//    private String parseBytesToString(CrawleableUri uri) {
//    	byte[] suri = null;
//    	try {
//			suri = serializer.serialize(uri);
//			StringBuilder s = new StringBuilder();
//
//			for (int i = 0; i < suri.length; i++) {
//				s.append(suri[i]);
//                if (i < suri.length - 1)
//					s.append(",");
//			}
//			return s.toString();
//		} catch (IOException e) {
//			LOGGER.error("Error while adding uri to RDBQueue",e);
//			return null;
//		}
//    }
//
//    private CrawleableUri parseStringToCuri(String uri) {
//    	String[] suri = uri.split(",");
//    	byte[] buri = new byte[suri.length];
//
//    	for (int i = 0; i < buri.length; i++) {
//    		buri[i] = Byte.parseByte(suri[i]);
//		}
//
//    	try {
//			return serializer.deserialize(buri);
//		} catch (IOException e) {
//			return null;
//		}
//    }

    public void addCrawleableUri(CrawleableUri uri, List ipAddressTypeKey) {

        try {
            byte[] suri = serializer.serialize(uri);
            r.db("squirrel")
                .table("queue")
                .getAll(ipAddressTypeKey)
                .optArg("index", "ipAddressType")
                .update(queueItem -> r.hashMap("uris", queueItem.g("uris").append(r.binary((suri)))))
                .run(connector.connection);
            LOGGER.debug("Inserted existing UriTypePair");
        } catch (Exception e) {
            LOGGER.error("Error while adding uri to RDBQueue", e);
        }
    }

    public void addCrawleableUri(CrawleableUri uri) {
        r.db("squirrel")
            .table("queue")
            .insert(crawleableUriToRDBHashMap(uri))
            .run(connector.connection);
        LOGGER.debug("Inserted new UriTypePair");
    }

    public MapObject crawleableUriToRDBHashMap(CrawleableUri uri) {
        byte[] suri = null;
        try {
            suri = serializer.serialize(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InetAddress ipAddress = uri.getIpAddress();
        UriType uriType = uri.getType();
        return r.hashMap("uris", r.array(r.binary((suri))))
            .with("ipAddress", ipAddress.getHostAddress())
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
                    InetAddress ipAddress = InetAddress.getByName(map.get("ipAddress").toString());
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

        List ipAddressTypeKey = packTuple(pair.ip.getHostAddress(), pair.type.toString());
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
            uris = createCrawleableUriList(uriStringList);
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

    private List<CrawleableUri> createCrawleableUriList(ArrayList uris) {
        List<CrawleableUri> resultUris = new ArrayList<>();

        for (Object uriString : uris) {
            try {
                resultUris.add(serializer.deserialize((byte[]) uriString));
            } catch (Exception e) {
                LOGGER.error("Couldn't deserialize uri", e);
            }
        }

        return resultUris;
    }

//    @Override
    public Iterator<AbstractMap.SimpleEntry<InetAddress, List<CrawleableUri>>> getIPURIIterator() {
        return new Iterator<AbstractMap.SimpleEntry<InetAddress, List<CrawleableUri>>>() {
            private Cursor cursor = r.db("squirrel").table("queue").orderBy().optArg("index", "ipAddressType").run(connector.connection);

            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            @Override
            public AbstractMap.SimpleEntry<InetAddress, List<CrawleableUri>> next() {
                HashMap row = (HashMap) cursor.next();
                LOGGER.trace("Go through the result. Next entry contains " + row.size() + " elements: " + row);
                try {
                    String baseURI = row.get("ipAddress").toString();
                    InetAddress key = null;
                    try {
                        key = InetAddress.getByName(baseURI);
                    } catch (Exception e) {
                        LOGGER.error("Can't parse the address " + baseURI, e);
                    }
                    key = (key == null) ? InetAddress.getLocalHost() : key;
                    Object uriField = row.get("uris");
                    List<CrawleableUri> value;
                    CrawleableUriFactoryImpl factory = new CrawleableUriFactoryImpl();
                    if (uriField instanceof List) {
                        value = createCrawleableUriList((ArrayList) uriField);
                    } else if (uriField instanceof String) {
                        value = Collections.singletonList(factory.create((String) uriField));
                    } else {
                        LOGGER.error("Was not able to read the field from the RDBQueue \"uris\"");
                        value = Collections.EMPTY_LIST;
                    }

                    return new AbstractMap.SimpleEntry<>(key, value);
                } catch (UnknownHostException e) {
                    LOGGER.error("Error while parsing the data from the RDBQueue into an HashMap", e);
                    return null;
                }
            }
        };
    }

}