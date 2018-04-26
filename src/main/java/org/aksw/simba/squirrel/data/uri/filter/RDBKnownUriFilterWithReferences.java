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
    public void add(CrawleableUri uri, List<CrawleableUri> urisFound, long timestamp) {
        r.db(RDBDATABASENAME)
            .table(RDBTABLENAME)
            .insert(convertURITimestampToRDB(uri, timestamp).with(ADDITIONALCOLUMN, urisFound))
            .run(connector.connection);
        LOGGER.debug("Adding URI {} to the known uri filter list with " + urisFound.size() + " founded uris", uri.toString());
    }


    /**
     * Get a iterator. With that iterator, you can walk through the crawled graph.
     *
     * @param offset          skip the first entries. A negative number means to avois skipping anything.
     * @param latest          if {@code true}, the iterator starts from the last entry - offset
     * @param onlyCrawledUris if {@code true}, uris, that are not in the {@link KnownUriFilter} will be discarded from the result
     * @return the iterator {@link KnownUriReferenceIterator}
     */
    public Iterator<AbstractMap.SimpleEntry<String, List<String>>> walkThroughCrawledGraph(int offset, boolean latest, boolean onlyCrawledUris) {
        long entryCount = r.db(RDBDATABASENAME).table(RDBTABLENAME).count().run(connector.connection);
        if (entryCount < offset) {
            LOGGER.warn("Your offset (" + offset + ") is higher than the number of entries (" + entryCount + ")!");
            return Collections.emptyIterator();
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
        if (offset == -1) {
            cursor = r.db(RDBKnownUriFilterWithReferences.RDBDATABASENAME).table(RDBKnownUriFilterWithoutReferences.RDBTABLENAME).run(connector.connection);
        } else {
            cursor = r.db(RDBKnownUriFilterWithReferences.RDBDATABASENAME).table(RDBKnownUriFilterWithoutReferences.RDBTABLENAME).skip(offset).run(connector.connection);
        }
        this.connector = connector;
        this.onlyCrawledUris = onlyCrawledUris;
    }

    private boolean containURI(String uri) {
        return r.db(RDBKnownUriFilterWithReferences.RDBDATABASENAME)
            .table(RDBKnownUriFilterWithReferences.RDBTABLENAME)
            .getAll(uri)
            .optArg("index", "uri")
            .isEmpty()
            .run(connector.connection);
    }

    @Override
    public boolean hasNext() {
        return cursor.hasNext();
    }

    @Override
    public AbstractMap.SimpleEntry<String, List<String>> next() {
        HashMap row = (HashMap) cursor.next();
        LOGGER.trace("Go through the result. Next entry contains " + row.size() + " elements: " + row);
        List<String> references = (ArrayList<String>) row.get(RDBKnownUriFilterWithReferences.ADDITIONALCOLUMN);
        int referencesSize = references.size();
        if (onlyCrawledUris) {
            references.removeIf(s -> !containURI(s));
            LOGGER.debug("Because you enabled the \"referencesSize\"-option, " + (referencesSize - references.size()) + " URIs were removed!");
        }
        return new AbstractMap.SimpleEntry<>(row.get("uri").toString(), (ArrayList<String>) row.get(RDBKnownUriFilterWithReferences.ADDITIONALCOLUMN));
    }
}
