package org.aksw.simba.squirrel.data.uri.filter;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * like {@link RDBKnownUriFilterWithoutReferences}, but with the additional feature od saving and iterating through the crawled graph.
 *
 * @author Philipp Heinisch
 */
public class RDBKnownUriFilterWithReferences extends RDBKnownUriFilterWithoutReferences {
    private Logger LOGGER = LoggerFactory.getLogger(RDBKnownUriFilterWithReferences.class);

    final static String ADDITIONALCOLUMN = "foundUris";

    public RDBKnownUriFilterWithReferences(String hostname, Integer port) {
        super(hostname, port);
    }

    @Override
    public void add(CrawleableUri uri) {
        add(uri, System.currentTimeMillis());
    }

    @Override
    public void add(CrawleableUri uri, long timestamp) {
        add(uri, Collections.EMPTY_LIST, timestamp);
    }

    @Override
    public void add(CrawleableUri uri, List<CrawleableUri> urisFound, long timestamp) {
        List<String> urisFoundInput = r.array();
        urisFound.forEach(u -> urisFoundInput.add(u.getUri().toString()));
        r.db(RDBDATABASENAME)
            .table(RDBTABLENAME)
            .insert(convertURITimestampToRDB(uri, timestamp)
                .with(ADDITIONALCOLUMN, urisFoundInput)
            )
            .optArg("conflict", "replace")
            .run(connector.connection);
        LOGGER.debug("Adding URI {} to the known uri filter list with " + urisFound.size() + " founded uris", uri.toString());
    }


    /**
     * Get a iterator. With that iterator, you can walk through the crawled graph.
     *
     * @param offset          skip the first entries. A negative number means to avoid skipping anything.
     * @param latest          if {@code true}, the iterator starts from the last entry - offset
     * @param onlyCrawledUris if {@code true}, uris, that are not in the {@link KnownUriFilter} will be discarded from the result
     * @return the iterator {@link KnownUriReferenceIterator}
     */
    public Iterator<AbstractMap.SimpleEntry<String, List<String>>> walkThroughCrawledGraph(int offset, boolean latest, boolean onlyCrawledUris) {
        long entryCount = r.db(RDBDATABASENAME).table(RDBTABLENAME).count().run(connector.connection);
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
            cursor = r.db(RDBKnownUriFilterWithReferences.RDBDATABASENAME).table(RDBKnownUriFilterWithoutReferences.RDBTABLENAME).orderBy().optArg("index", r.asc("id")).run(connector.connection);
        } else {
            cursor = r.db(RDBKnownUriFilterWithReferences.RDBDATABASENAME).table(RDBKnownUriFilterWithoutReferences.RDBTABLENAME).orderBy().optArg("index", r.asc("id")).skip(offset).run(connector.connection);
        }
        this.connector = connector;
        this.onlyCrawledUris = onlyCrawledUris;
    }

    private boolean containURI(String uri) {
        return !((boolean) r.db(RDBKnownUriFilterWithReferences.RDBDATABASENAME)
            .table(RDBKnownUriFilterWithReferences.RDBTABLENAME)
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
            List<String> references = (ArrayList<String>) row.get(RDBKnownUriFilterWithReferences.ADDITIONALCOLUMN);
            int referencesSize = references.size();
            if (onlyCrawledUris) {
                references.removeIf(s -> !containURI(s));
                LOGGER.debug("Because you enabled the \"onlyCrawledUris\"-option, " + (referencesSize - references.size()) + " URIs were removed from the foundedURI list!");
            }
            ret = new AbstractMap.SimpleEntry<>(row.get("uri").toString() + ((row.containsKey("ipAddress")) ? "|" + row.get("ipAddress") : ""), (ArrayList<String>) row.get(RDBKnownUriFilterWithReferences.ADDITIONALCOLUMN));
        } catch (NullPointerException e) {
            ret = new AbstractMap.SimpleEntry<>("FAIL [" + e.hashCode() + "]", Collections.singletonList((e.getMessage() == null) ? "unknown error" : e.getMessage()));
        }
        if (!cursor.hasNext())
            cursor.close();

        return ret;
    }
}
