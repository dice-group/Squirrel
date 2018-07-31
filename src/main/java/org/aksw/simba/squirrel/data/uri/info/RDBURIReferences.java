package org.aksw.simba.squirrel.data.uri.info;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlError;
import com.rethinkdb.gen.exc.ReqlOpFailedError;
import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.URI;
import java.util.*;

/**
 * A class, that stores the references: crawled URI --> URIs, that were found by crawling this URI
 *
 * @author Philipp Heinisch
 */
public class RDBURIReferences implements URIReferences, Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBURIReferences.class);

    private RDBConnector connector;
    private RethinkDB r;


    private URIReferencesUtils utils;

    // Some constants for the rethinkDB
    static final String DATABASE_NAME = "squirrel";
    static final String TABLE_NAME = "uriReferences";
    static final String COLUMN_URI = "uri";
    static final String COLUMN_FOUNDURIS = "foundUris";

    /**
     * Constructor.
     *
     * @param hostname The hostname for database.
     * @param port     The port for the database.
     */
    public RDBURIReferences(String hostname, Integer port) {
        this.connector = new RDBConnector(hostname, port);
        r = RethinkDB.r;
        utils = new URIReferencesUtils();
    }

    /**
     * Constructor.
     *
     * @param hostname The hostname for database.
     * @param port     The port for the database.
     * @param mode     The mode for cutting the inserted URIs. See {@link URIShortcutMode}
     */
    public RDBURIReferences(String hostname, Integer port, URIShortcutMode mode) {
        this.connector = new RDBConnector(hostname, port);
        r = RethinkDB.r;
        utils = new URIReferencesUtils(mode);
    }

    /**
     * Initialize the database
     */
    public void open() {
        try {
            this.connector.open();
            if (!connector.squirrelDatabaseExists())
                r.dbCreate(DATABASE_NAME).run(this.connector.connection);
            if (!connector.tableExists(DATABASE_NAME, TABLE_NAME)) {
                r.db(DATABASE_NAME).tableCreate(TABLE_NAME).run(this.connector.connection);
                r.db(DATABASE_NAME).table(TABLE_NAME).indexCreate(COLUMN_URI).run(this.connector.connection);
                r.db(DATABASE_NAME).table(TABLE_NAME).indexWait(COLUMN_URI).run(this.connector.connection);
            }
        } catch (ReqlOpFailedError e) {
            if (connector.tableExists(DATABASE_NAME, TABLE_NAME))
                LOGGER.debug("The database already exists", e);
            else
                throw e;
        }
    }

    /**
     * Extends the database with the new information
     *
     * @param uri       the {@link URI} (secondary index)
     * @param urisFound list of {@link URI}s, that were found while crawling through the content of the uri
     */
    public synchronized void add(CrawleableUri uri, List<CrawleableUri> urisFound) {
        if (!connector.tableExists(DATABASE_NAME, TABLE_NAME)) {
            LOGGER.warn("Table \"" + TABLE_NAME + "\" doesn't maybe exist until yet. Create it now...");
            open();
        }

        Cursor<List<String>> cursor = r.db(DATABASE_NAME)
            .table(TABLE_NAME)
            .getAll(utils.convertURI(uri))
            .optArg("index", COLUMN_URI)
            .g(COLUMN_FOUNDURIS)
            .run(connector.connection);

        if (cursor.hasNext()) {
            // entry found
            String convertedURI = utils.convertURI(uri);
            try {
                List<String> updateList = utils.mergeLists(cursor.next(), urisFound);
                r.db(DATABASE_NAME).table(TABLE_NAME).getAll(convertedURI).optArg("index", COLUMN_URI).update(r.hashMap(COLUMN_FOUNDURIS, r.expr(updateList.toArray()))).run(connector.connection);
                LOGGER.info("There was already a row in the database table \"" + TABLE_NAME + "\" for " + convertedURI + ". We updated the value regarding that to a size of " + updateList.size());
            } catch (OutOfMemoryError e) {
                LOGGER.error("List of references URIs was getting too long! Insert a new line (maybe leads to inconsistencies)", e);
                insertDBLine(uri, urisFound);
            } catch (Exception e) {
                LOGGER.error("Failed to extend/update the database " + DATABASE_NAME + "->" + TABLE_NAME + " with the URI " + convertedURI, e);
            }
        } else {
            //no entry found
            insertDBLine(uri, urisFound);
        }

        try {
            cursor.close();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
        }
    }

    /**
     * Inserts a new row into the database
     *
     * @param uri       the {@link URI} (secondary index)
     * @param urisFound list of {@link URI}s, that were found while crawling through the content of the uri
     */
    private void insertDBLine(CrawleableUri uri, List<CrawleableUri> urisFound) {
        try {
            r.db(DATABASE_NAME).table(TABLE_NAME).insert(
                r.hashMap(COLUMN_URI, utils.convertURI(uri))
                    .with(COLUMN_FOUNDURIS, r.expr(utils.mergeLists(null, urisFound).toArray()))
            ).run(connector.connection);
            LOGGER.debug("Inserted  the URI " + uri.toString() + " with " + urisFound.size() + " references");
        } catch (ReqlError e) {
            LOGGER.error("Failed to extend the database " + DATABASE_NAME + "->" + TABLE_NAME + " with the URI " + utils.convertURI(uri) + " | Insert fail: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     */
    @Override
    public void close() {
        r.db(DATABASE_NAME).tableDrop(TABLE_NAME).run(this.connector.connection);
        this.connector.close();
    }

    /**
     * Get a iterator. With that iterator, you can walk through the crawled graph.
     *
     * @param offset          skip the first entries. A negative number means to avoid skipping anything.
     * @param latest          if {@code true}, the iterator starts from the last entry - offset
     * @param onlyCrawledUris if {@code true}, uris, that are not in the {@link KnownUriFilter} will be discarded from the result
     * @return the iterator {@link org.aksw.simba.squirrel.data.uri.info.URIReferencesIterator}
     */
    public Iterator<AbstractMap.SimpleEntry<String, List<String>>> walkThroughCrawledGraph(int offset, boolean latest, boolean onlyCrawledUris) {
        long entryCount = r.db(DATABASE_NAME).table(TABLE_NAME).count().run(connector.connection);
        if (entryCount <= offset) {
            if (latest) {
                LOGGER.debug("Your offset (" + offset + ") is higher or equal than the number of entries (" + entryCount + "), so we'll return an iterator over the whole graph from the beginning!");
            } else {
                LOGGER.warn("Your offset (" + offset + ") is higher or equal than the number of entries (" + entryCount + ")! Return an empty iterator...");
                return Collections.emptyIterator();
            }
        }
        long finalOffset = (offset < 0) ? -1 : ((latest) ? entryCount - offset : offset);

        return new org.aksw.simba.squirrel.data.uri.info.URIReferencesIterator(connector, finalOffset, onlyCrawledUris);
    }
}

/**
 * The Iterator regarding {@link RDBURIReferences}.walkThroughCrawledGraph
 *
 * @author Pilipp Heinisch
 */
class URIReferencesIterator implements Iterator<AbstractMap.SimpleEntry<String, List<String>>> {
    private final Logger LOGGER = LoggerFactory.getLogger(org.aksw.simba.squirrel.data.uri.info.URIReferencesIterator.class);

    private final boolean onlyCrawledUris;

    private final RethinkDB r = RethinkDB.r;
    private RDBConnector connector;

    private Cursor cursor;

    /**
     * Creates an iterator
     *
     * @param connector       the connector to the RethinkDB (because the iterator will crawl over the data table {@link RDBURIReferences}.TABLE_NAME
     * @param offset          the number of data table lines, that should be skipped (crawled URIs). If the offset is 0 or below, no lines wil be skipped
     * @param onlyCrawledUris for each crawled URI there is a list with URIs, that were found by crawling that URI. If that parameter is {@code true}, only the already crawled URIs in these lists will be returned
     */
    URIReferencesIterator(RDBConnector connector, long offset, boolean onlyCrawledUris) {
        if (offset <= 0) {
            cursor = r.db(RDBURIReferences.DATABASE_NAME).table(RDBURIReferences.TABLE_NAME).orderBy().optArg("index", r.asc("id")).run(connector.connection);
        } else {
            cursor = r.db(RDBURIReferences.DATABASE_NAME).table(RDBURIReferences.TABLE_NAME).orderBy().optArg("index", r.asc("id")).skip(offset).run(connector.connection);
        }
        this.connector = connector;
        this.onlyCrawledUris = onlyCrawledUris;
    }

    private boolean containURI(String uri) {
        return !((boolean) r.db(RDBURIReferences.DATABASE_NAME)
            .table(RDBURIReferences.TABLE_NAME)
            .getAll(uri)
            .optArg("index", RDBURIReferences.COLUMN_URI)
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
            List<String> references = (ArrayList<String>) row.get(RDBURIReferences.COLUMN_FOUNDURIS);
            int referencesSize = references.size();
            if (onlyCrawledUris) {
                references.removeIf(s -> !containURI(s));
                LOGGER.debug("Because you enabled the \"onlyCrawledUris\"-option, " + (referencesSize - references.size()) + " URIs were removed from the foundedURI list!");
            }
            ret = new AbstractMap.SimpleEntry<>(row.get(RDBURIReferences.COLUMN_URI).toString(), references);
        } catch (Exception e) {
            ret = new AbstractMap.SimpleEntry<>("FAIL [" + e.hashCode() + "]", Collections.singletonList((e.getMessage() == null) ? "unknown error" : e.getMessage()));
        }
        if (!cursor.hasNext())
            cursor.close();

        return ret;
    }
}

