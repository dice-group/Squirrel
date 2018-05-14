package org.aksw.simba.squirrel.data.uri.filter;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
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
    private RethinkDB r;

    /**
     * Indicates whether the {@link org.aksw.simba.squirrel.frontier.Frontier} using this filter does recrawling.
     */
    private boolean frontierDoesRecrawling;

    /*
    Some constants for the rethinkdb
     */
    public static final String DATABASE_NAME = "squirrel";
    public static final String TABLE_NAME = "knownurifilter";
    public static final String COLUMN_TIMESTAMP_LAST_CRAWL = "timestampLastCrawl";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_CRAWLING_IN_PROCESS = "crawlingInProcess";
    private static final String COLUMN_TIMESTAMP_NEXT_CRAWL = "timestampNextCrawl";
    private static final String COLUMN_IP = "ipAddress";
    private static final String COLUMN_TYPE = "type";

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
        Cursor<HashMap> cursor = r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).run(connector.connection);
        if (cursor.hasNext()) {
            r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).update(r.hashMap(COLUMN_CRAWLING_IN_PROCESS, false));
            r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).update(r.hashMap(COLUMN_TIMESTAMP_LAST_CRAWL, lastCrawlTimestamp));
            r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).update(r.hashMap((COLUMN_TIMESTAMP_NEXT_CRAWL), nextCrawlTimestamp)).run(connector.connection);
        } else {
            r.db(DATABASE_NAME)
                .table(TABLE_NAME)
                .insert(convertURITimestampToRDB(uri, lastCrawlTimestamp, nextCrawlTimestamp, false))
                .run(connector.connection);
        }
        LOGGER.debug("Adding URI {} to the known uri filter list", uri.toString());
    }

    private MapObject convertURIToRDB(CrawleableUri uri) {
        InetAddress ipAddress = uri.getIpAddress();
        URI uriPath = uri.getUri();
        UriType uriType = uri.getType();
        return r.hashMap(COLUMN_URI, uriPath.toString())
            .with(COLUMN_IP, ipAddress.toString())
            .with(COLUMN_TYPE, uriType.toString());
    }

    private MapObject convertURITimestampToRDB(CrawleableUri uri, long timestamp, long nextCrawlTimestamp, boolean crawlingInProcess) {
        MapObject uriMap = convertURIToRDB(uri);
        return uriMap
            .with(COLUMN_TIMESTAMP_LAST_CRAWL, timestamp).with(COLUMN_TIMESTAMP_NEXT_CRAWL, nextCrawlTimestamp)
            .with(COLUMN_CRAWLING_IN_PROCESS, crawlingInProcess);
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
                LOGGER.debug("URI {} is not good", uri.toString());
                return false;
            }
            Long timestampNextCrawl = cursor.next();
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
