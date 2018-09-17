package org.aksw.simba.squirrel.data.uri.filter;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.ReqlExpr;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.deduplication.hashing.HashValue;
import org.aksw.simba.squirrel.deduplication.hashing.UriHashCustodian;
import org.aksw.simba.squirrel.deduplication.hashing.impl.ArrayHashValue;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by ivan on 8/18/16.
 */
public class RDBKnownUriFilter implements KnownUriFilter, Closeable, UriHashCustodian {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBKnownUriFilter.class);

    private RDBConnector connector = null;
    private RethinkDB r;

    /**
     * Used for converting Strings to {@link HashValue}s.
     */
    private HashValue hashValueForDecoding = new ArrayHashValue();

    /**
     * Indicates whether the {@link org.aksw.simba.squirrel.frontier.Frontier} using this filter does recrawling.
     */
    private boolean frontierDoesRecrawling;

    /*
    Some constants for the rethinkDB
     */
    public static final String DATABASE_NAME = "squirrel";
    public static final String TABLE_NAME = "knownurifilter";
    public static final String COLUMN_TIMESTAMP_LAST_CRAWL = "timestampLastCrawl";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_CRAWLING_IN_PROCESS = "crawlingInProcess";
    public static final String COLUMN_TIMESTAMP_NEXT_CRAWL = "timestampNextCrawl";
    public static final String COLUMN_IP = "ipAddress";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_HASH_VALUE = "hashValue";

    /**
     * Used as a default hash value for URIS, will be replaced by real hash value as soon as it has been computed.
     */
    private static final String DUMMY_HASH_VALUE = "dummyValue";


    /**
     * Constructor.
     *
     * @param hostname               The hostname for database.
     * @param port                   The port for the database.
     * @param frontierDoesRecrawling Value for {@link #frontierDoesRecrawling}.
     */
    public RDBKnownUriFilter(String hostname, Integer port, boolean frontierDoesRecrawling) {
        this.connector = new RDBConnector(hostname, port);
        r = RethinkDB.r;
        this.frontierDoesRecrawling = frontierDoesRecrawling;
    }

    /**
     * Constructor.
     *
     * @param hostname The hostname for database.
     * @param port     The port for the database.
     */
    public RDBKnownUriFilter(String hostname, Integer port) {
        this(hostname, port, false);
    }

    /**
     * Constructor.
     *
     * @param connector              Value for {@link #connector}.
     * @param r                      Value for {@link #r}.
     * @param frontierDoesRecrawling Value for {@link #frontierDoesRecrawling}.
     */
    @SuppressWarnings("unused")
    public RDBKnownUriFilter(RDBConnector connector, RethinkDB r, boolean frontierDoesRecrawling) {
        this.connector = connector;
        this.r = r;
        this.frontierDoesRecrawling = frontierDoesRecrawling;
    }

    public void open() {
        this.connector.open();
        if (!connector.squirrelDatabaseExists())
            r.dbCreate(DATABASE_NAME).run(this.connector.connection);
        if (!knownUriFilterTableExists()) {
            r.db(DATABASE_NAME).tableCreate(TABLE_NAME).run(this.connector.connection);
            r.db(DATABASE_NAME).table(TABLE_NAME).indexCreate(COLUMN_URI).run(this.connector.connection);
            r.db(DATABASE_NAME).table(TABLE_NAME).indexWait(COLUMN_URI).run(this.connector.connection);
        }
    }

    public void openConnector() {
        if (this.connector.connection == null) {
            this.connector.open();
        }
    }

    @Override
    public List<CrawleableUri> getOutdatedUris() {

        // get all uris with the following property:
        // (nextCrawlTimestamp has passed) AND (crawlingInProcess==false OR lastCrawlTimestamp is 3 times older than generalRecrawlTime)

        long generalRecrawlTime = Math.max(FrontierImpl.DEFAULT_GENERAL_RECRAWL_TIME, FrontierImpl.getGeneralRecrawlTime());

        Cursor<HashMap> cursor = r.db(DATABASE_NAME)
            .table(TABLE_NAME)
            .filter(doc -> doc.getField(COLUMN_TIMESTAMP_NEXT_CRAWL).le(System.currentTimeMillis()).and(
                (doc.getField(COLUMN_CRAWLING_IN_PROCESS).eq(false))
                    .or(doc.getField(COLUMN_TIMESTAMP_LAST_CRAWL).le(System.currentTimeMillis() - generalRecrawlTime * 3))))
            .run(connector.connection);

        List<CrawleableUri> urisToRecrawl = new ArrayList<>();
        while (cursor.hasNext()) {
            try {
                HashMap row = cursor.next();
                String ipString = (String) row.get(COLUMN_IP);
                if (ipString.contains("/")) {
                    ipString = ipString.split("/")[1];
                }
                urisToRecrawl.add(new CrawleableUri(new URI((String) row.get(COLUMN_URI)), InetAddress.getByName(ipString)));
            } catch (URISyntaxException | UnknownHostException e) {
                LOGGER.warn(e.toString());
            }
        }

        // mark that the uris are in process now
        for (CrawleableUri uri : urisToRecrawl) {
            r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).
                update(r.hashMap(COLUMN_CRAWLING_IN_PROCESS, true)).run(connector.connection);
        }

        cursor.close();
        return urisToRecrawl;
    }

    public boolean knownUriFilterTableExists() {
        return this.connector.tableExists(DATABASE_NAME, TABLE_NAME);
    }

    public void close() {
        r.db(DATABASE_NAME).tableDrop(TABLE_NAME).run(this.connector.connection);
        this.connector.close();
    }

    @Override
    public void add(CrawleableUri uri, long nextCrawlTimestamp) {
        add(uri, System.currentTimeMillis(), nextCrawlTimestamp);
    }

    @Override
    public void add(CrawleableUri uri, long lastCrawlTimestamp, long nextCrawlTimestamp) {
        try {
            // FIXME Fix this implementation
//            if (r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).isEmpty().run(connector.connection)) {
                r.db(DATABASE_NAME)
                    .table(TABLE_NAME)
                    .insert(convertURITimestampToRDB(uri, lastCrawlTimestamp, nextCrawlTimestamp, false, DUMMY_HASH_VALUE))
                    .run(connector.connection);
//            } else {
//                ReqlExpr row = r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString()));
//                row.update(r.hashMap(COLUMN_CRAWLING_IN_PROCESS, false));
//                row.update(r.hashMap(COLUMN_TIMESTAMP_LAST_CRAWL, lastCrawlTimestamp));
//                row.update(r.hashMap(COLUMN_HASH_VALUE, DUMMY_HASH_VALUE));
//                row.update(r.hashMap((COLUMN_TIMESTAMP_NEXT_CRAWL), nextCrawlTimestamp)).run(connector.connection);
//            }
            LOGGER.debug("Adding URI {} to the known uri filter list", uri.toString());
        } catch (Exception e) {
            LOGGER.error("Failed to add the URI \"" + uri.toString() + "\" to the known uri filter list", e);
        }
    }

    @Override
    public Set<CrawleableUri> getUrisWithSameHashValues(Set<HashValue> hashValuesForComparison) {

        Set<String> stringHashValues = new HashSet<>();
        for (HashValue value : hashValuesForComparison) {
            stringHashValues.add(value.encodeToString());
        }

        Cursor<HashMap> cursor = r.db(DATABASE_NAME).table(TABLE_NAME).filter
            (doc -> stringHashValues.contains(doc.getField(COLUMN_HASH_VALUE))).run(connector.connection);

        Set<CrawleableUri> urisToReturn = new HashSet<>();

        while (cursor.hasNext()) {
            HashMap<String, Object> nextRow = cursor.next();
            CrawleableUri newUri = null;
            HashValue hashValue = null;
            for (String key : nextRow.keySet()) {
                if (key.equals(COLUMN_HASH_VALUE)) {
                    String hashAsString = (String) nextRow.get(key);
                    hashValue = hashValueForDecoding.decodeFromString(hashAsString);
                } else if (key.equals(COLUMN_URI)) {
                    try {
                        newUri = new CrawleableUri(new URI((String) nextRow.get(key)));
                    } catch (URISyntaxException e) {
                        LOGGER.error("Error while constructing an uri: " + nextRow.get(key));
                    }
                }
            }
            newUri.putData(Constants.URI_HASH_KEY, hashValue);
            urisToReturn.add(newUri);
        }
        cursor.close();
        return urisToReturn;
    }

    @Override
    public void addHashValuesForUris(List<CrawleableUri> uris) {
        for (CrawleableUri uri : uris) {
            r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).
                update(r.hashMap(COLUMN_HASH_VALUE, ((HashValue) uri.getData(Constants.URI_HASH_KEY)).encodeToString())).run(connector.connection);
        }
    }

    private MapObject convertURIToRDB(CrawleableUri uri) {
        InetAddress ipAddress = uri.getIpAddress();
        URI uriPath = uri.getUri();
        UriType uriType = uri.getType();
        return r.hashMap(COLUMN_URI, uriPath.toString())
            .with(COLUMN_IP, ipAddress.toString())
            .with(COLUMN_TYPE, uriType.toString());
    }

    private MapObject convertURITimestampToRDB(CrawleableUri uri, long timestamp, long nextCrawlTimestamp, boolean crawlingInProcess, String hashValue) {
        MapObject uriMap = convertURIToRDB(uri);
        uriMap
            .with(COLUMN_TIMESTAMP_LAST_CRAWL, timestamp).with(COLUMN_TIMESTAMP_NEXT_CRAWL, nextCrawlTimestamp)
            .with(COLUMN_CRAWLING_IN_PROCESS, crawlingInProcess)
            .with(COLUMN_HASH_VALUE, hashValue);
        return uriMap;
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        Cursor<Long> cursor = r.db(DATABASE_NAME)
            .table(TABLE_NAME)
            .getAll(uri.getUri().toString())
            .optArg("index", COLUMN_URI)
            .g(COLUMN_TIMESTAMP_NEXT_CRAWL)
            .run(connector.connection);
        if (cursor.hasNext()) {
            if (!frontierDoesRecrawling) {
                LOGGER.debug("URI {} is not good, because it was already crawled and the frontier does not recrawl anything!", uri.toString());
                cursor.close();
                return false;
            }
            Long timestampNextCrawl = cursor.next();
            LOGGER.debug("URI {} was already crawled and will be next crawled at " + timestampNextCrawl + ". Current time stamp is " + System.currentTimeMillis(), uri.toString());
            cursor.close();
            return System.currentTimeMillis() > timestampNextCrawl;
        } else {
            LOGGER.debug("URI {} is good", uri.toString());
            cursor.close();
            return true;
        }
    }

    public void purge() {
        r.db(DATABASE_NAME).table(TABLE_NAME).delete().run(connector.connection);
    }

    @Override
    public long count() {
        return r.db(DATABASE_NAME).table(TABLE_NAME).count().run(connector.connection);
    }
}