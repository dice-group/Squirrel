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
import java.util.*;

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

    /**
     * Indicates weather the filter should save in addition a list of referred (found) URIs for each URI in the list (know URI)
     */
    private final boolean saveReferenceList;

    /*
    Some constants for the rethinkdb
     */
    public static final String DATABASE_NAME = "squirrel";
    public static final String TABLE_NAME = "knownurifilter";
    public static final String COLUMN_TIMESTAMP_LAST_CRAWL = "timestampLastCrawl";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_CRAWLING_IN_PROCESS = "crawlingInProcess";
    public static final String COLUMN_FOUNDURIS = "foundUris";
    private static final String COLUMN_TIMESTAMP_NEXT_CRAWL = "timestampNextCrawl";
    private static final String COLUMN_IP = "ipAddress";
    private static final String COLUMN_TYPE = "type";

    /**
     * Constructor.
     *
     * @param hostname               The hostname for database.
     * @param port                   The port for the database.
     * @param frontierDoesRecrawling Value for {@link #frontierDoesRecrawling}.
     * @param saveReferenceList      Value for {@link #saveReferenceList}.
     */
    public RDBKnownUriFilter(String hostname, Integer port, boolean frontierDoesRecrawling, boolean saveReferenceList) {
        this.connector = new RDBConnector(hostname, port);
        r = RethinkDB.r;
        this.frontierDoesRecrawling = frontierDoesRecrawling;
        this.saveReferenceList = saveReferenceList;
    }

    /**
     * Constructor.
     *
     * @param hostname               The hostname for database.
     * @param port                   The port for the database.
     * @param frontierDoesRecrawling Value for {@link #frontierDoesRecrawling}.
     */
    public RDBKnownUriFilter(String hostname, Integer port, boolean frontierDoesRecrawling) {
        this(hostname, port, frontierDoesRecrawling, false);
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
     * @param saveReferenceList      Value for {@link #saveReferenceList}.
     */
    public RDBKnownUriFilter(RDBConnector connector, RethinkDB r, boolean frontierDoesRecrawling, boolean saveReferenceList) {
        this.connector = connector;
        this.r = r;
        this.frontierDoesRecrawling = frontierDoesRecrawling;
        this.saveReferenceList = saveReferenceList;
    }

    /**
     * Constructor.
     *
     * @param connector              Value for {@link #connector}.
     * @param r                      Value for {@link #r}.
     * @param frontierDoesRecrawling Value for {@link #frontierDoesRecrawling}.
     */
    public RDBKnownUriFilter(RDBConnector connector, RethinkDB r, boolean frontierDoesRecrawling) {
        this(connector, r, frontierDoesRecrawling, false);
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
        add(uri, Collections.EMPTY_LIST, lastCrawlTimestamp, nextCrawlTimestamp);
    }

    @Override
    public void add(CrawleableUri uri, List<CrawleableUri> urisFound, long lastCrawlTimestamp, long nextCrawlTimestamp) {
        Cursor<HashMap> cursor = r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).run(connector.connection);
        if (cursor.hasNext()) {
            r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).update(r.hashMap(COLUMN_CRAWLING_IN_PROCESS, false));
            r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).update(r.hashMap(COLUMN_TIMESTAMP_LAST_CRAWL, lastCrawlTimestamp));
            if(saveReferenceList) {
                r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).update(r.hashMap(COLUMN_FOUNDURIS, urisFound));
            }
            r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).update(r.hashMap((COLUMN_TIMESTAMP_NEXT_CRAWL), nextCrawlTimestamp)).run(connector.connection);
        } else {
            r.db(DATABASE_NAME)
                .table(TABLE_NAME)
                .insert(convertURITimestampToRDB(uri, urisFound, lastCrawlTimestamp, nextCrawlTimestamp, false))
                .run(connector.connection);
        }
        LOGGER.debug("Adding URI {} to the known uri filter list", uri.toString());
    }

    public void add(CrawleableUri uri, List<CrawleableUri> urisFound, long nextCrawlTimestamp) {
        add(uri, urisFound, System.currentTimeMillis(),  nextCrawlTimestamp);
    }

    protected MapObject convertURIToRDB(CrawleableUri uri) {
        InetAddress ipAddress = uri.getIpAddress();
        URI uriPath = uri.getUri();
        UriType uriType = uri.getType();
        return r.hashMap(COLUMN_URI, uriPath.toString())
            .with(COLUMN_IP, ipAddress.toString())
            .with(COLUMN_TYPE, uriType.toString());
    }

    private MapObject convertURITimestampToRDB(CrawleableUri uri, List<CrawleableUri> urisFound, long timestamp, long nextCrawlTimestamp, boolean crawlingInProcess) {
        MapObject uriMap = convertURIToRDB(uri);
        uriMap
            .with(COLUMN_TIMESTAMP_LAST_CRAWL, timestamp).with(COLUMN_TIMESTAMP_NEXT_CRAWL, nextCrawlTimestamp)
            .with(COLUMN_CRAWLING_IN_PROCESS, crawlingInProcess);
        if (saveReferenceList) {
            uriMap.with(COLUMN_FOUNDURIS, urisFound);
            LOGGER.trace("Appended " + urisFound.size() + " founded uris to the list.");
        }

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

    /**
     * A reference list is a list for eacch crawled (known) URIs, that contains URIs (or namespaces of URIs or something else), that were found while crawling the certain URI
     *
     * @return {@code true} iff the object stores the reference list
     */
    @Override
    public boolean savesReferenceList() {
        return saveReferenceList;
    }

    /**
     * Get a iterator. With that iterator, you can walk through the crawled graph.
     * If {@link #saveReferenceList} is {@code false}, you'll get an empty Iterator!
     *
     * @param offset          skip the first entries. A negative number means to avoid skipping anything.
     * @param latest          if {@code true}, the iterator starts from the last entry - offset
     * @param onlyCrawledUris if {@code true}, uris, that are not in the {@link KnownUriFilter} will be discarded from the result
     * @return the iterator {@link KnownUriReferenceIterator}
     */
    public Iterator<AbstractMap.SimpleEntry<String, List<String>>> walkThroughCrawledGraph(int offset, boolean latest, boolean onlyCrawledUris) {
        if (!saveReferenceList) {
            return Collections.emptyIterator();
        }

        long entryCount = r.db(DATABASE_NAME).table(TABLE_NAME).count().run(connector.connection);
        if (entryCount < offset) {
            if (latest) {
                LOGGER.debug("Your offset (" + offset + ") is higher than the number of entries (" + entryCount + "), so we'll return an iterator over the whole graph from the beginning!");
            } else {
                LOGGER.warn("Your offset (" + offset + ") is higher than the number of entries (" + entryCount + ")! Return an empty iterator...");
                return Collections.emptyIterator();
            }
        }
        long finalOffset = (offset < 0) ? -1 : ((latest) ? entryCount - offset : offset);

        return new KnownUriReferenceIterator(connector, finalOffset, onlyCrawledUris);
    }
}

class KnownUriReferenceIterator implements Iterator<AbstractMap.SimpleEntry<String, List<String>>> {
    private final Logger LOGGER = LoggerFactory.getLogger(KnownUriReferenceIterator.class);

    private final boolean onlyCrawledUris;

    private final RethinkDB r = RethinkDB.r;
    private RDBConnector connector;

    private Cursor cursor;

    KnownUriReferenceIterator(RDBConnector connector, long offset, boolean onlyCrawledUris) {
        if (offset <= 0) {
            cursor = r.db(RDBKnownUriFilter.DATABASE_NAME).table(RDBKnownUriFilter.TABLE_NAME).orderBy().optArg("index", r.asc("id")).run(connector.connection);
        } else {
            cursor = r.db(RDBKnownUriFilter.DATABASE_NAME).table(RDBKnownUriFilter.TABLE_NAME).orderBy().optArg("index", r.asc("id")).skip(offset).run(connector.connection);
        }
        this.connector = connector;
        this.onlyCrawledUris = onlyCrawledUris;
    }

    private boolean containURI(String uri) {
        return !((boolean) r.db(RDBKnownUriFilter.DATABASE_NAME)
            .table(RDBKnownUriFilter.TABLE_NAME)
            .getAll(uri)
            .optArg("index", "uri")
            .isEmpty()
            .run(connector.connection));
    }

    @Override
    public boolean hasNext() {
        //LOGGER.info("Asked for existing of next results of " + cursor);
        return cursor.hasNext();
    }

    @Override
    public AbstractMap.SimpleEntry<String, List<String>> next() {
        HashMap row = (HashMap) cursor.next();
        LOGGER.trace("Go through the result. Next entry contains " + row.size() + " elements: " + row);
        AbstractMap.SimpleEntry<String, List<String>> ret;
        try {
            List<String> references = (ArrayList<String>) row.get(RDBKnownUriFilter.COLUMN_FOUNDURIS);
            int referencesSize = references.size();
            if (onlyCrawledUris) {
                references.removeIf(s -> !containURI(s));
                LOGGER.debug("Because you enabled the \"onlyCrawledUris\"-option, " + (referencesSize - references.size()) + " URIs were removed from the foundedURI list!");
            }
            ret = new AbstractMap.SimpleEntry<>(row.get("uri").toString() + ((row.containsKey("ipAddress")) ? "|" + row.get("ipAddress") : ""), (ArrayList<String>) row.get(RDBKnownUriFilter.COLUMN_FOUNDURIS));
        } catch (NullPointerException e) {
            ret = new AbstractMap.SimpleEntry<>("FAIL [" + e.hashCode() + "]", Collections.singletonList((e.getMessage() == null) ? "unknown error" : e.getMessage()));
        }
        if (!cursor.hasNext())
            cursor.close();

        return ret;
    }
}
