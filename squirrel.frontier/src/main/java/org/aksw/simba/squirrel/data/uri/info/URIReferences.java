package org.aksw.simba.squirrel.data.uri.info;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;

import java.net.URI;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;

/**
 * An interface for storing the references: crawled URI --> URIs, that were found by crawling this URI
 *
 * @author Pilipp Heinisch
 */
public interface URIReferences {
    /**
     * destroy the database.
     */
    void close();

    /**
     * init the database.
     */
    void open();

    /**
     * Extends the database with the new information
     *
     * @param uri       the {@link URI} (secondary index)
     * @param urisFound list of {@link URI}s, that were found while crawling through the content of the uri
     */
    void add(CrawleableUri uri, List<CrawleableUri> urisFound);

    /**
     * Get a iterator. With that iterator, you can walk through the crawled graph.
     *
     * @param offset          skip the first entries. A negative number means to avoid skipping anything.
     * @param latest          if {@code true}, the iterator starts from the last entry - offset
     * @param onlyCrawledUris if {@code true}, uris, that are not in the {@link KnownUriFilter} will be discarded from the result
     * @return the iterator, e.g. an {@link org.aksw.simba.squirrel.data.uri.info.URIReferencesIterator}
     */
    Iterator<AbstractMap.SimpleEntry<String, List<String>>> walkThroughCrawledGraph(int offset, boolean latest, boolean onlyCrawledUris);
}
