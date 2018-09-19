package org.aksw.simba.squirrel.collect;

import java.net.URI;
import java.util.Iterator;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.SinkBase;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

/**
 * A URI collector stores the URIs that have been found by a worker while
 * crawling/processing a certain URI. After the crawling, the URI collector can
 * be asked for these URIs using the {@link #getUris(CrawleableUri)} method.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface UriCollector extends SinkBase {

    /**
     * Adds the given triple to the list of URIs collected from the given URI.
     * <b>Note</b> that it is suggested to use the
     * {@link #addNewUri(CrawleableUri, CrawleableUri)} method instead since this
     * enables the addition of meta data to the collected URI.
     * 
     * @param uri
     *            The URI from which the given triple has been collected.
     * @param triple
     *            The triple that has been collected.
     */
    public default void addTriple(CrawleableUri uri, Triple triple) {
        addNewUri(uri, triple.getSubject());
        addNewUri(uri, triple.getPredicate());
        addNewUri(uri, triple.getObject());
    }

    /**
     * Adds the given new URI to the list of URIs collected for the given URI.
     * 
     * @param uri
     *            The URI from which the given new URI has been collected.
     * @param newUri
     *            The new URI that has been collected.
     */
    public void addNewUri(CrawleableUri uri, CrawleableUri newUri);

    /**
     * Adds the given new URI to the list of URIs collected for the given URI.
     * 
     * @param uri
     *            The URI from which the given new URI has been collected.
     * @param newUri
     *            The new URI that has been collected.
     */
    public default void addNewUri(CrawleableUri uri, Node newUri) {
        if (newUri.isURI()) {
            addNewUri(uri, newUri.getURI());
        }
    }

    /**
     * Adds the given new URI to the list of URIs collected for the given URI.
     * 
     * @param uri
     *            The URI from which the given new URI has been collected.
     * @param newUri
     *            The new URI that has been collected.
     */
    public default void addNewUri(CrawleableUri uri, String newUri) {
        try {
            addNewUri(uri, new CrawleableUri(URI.create(newUri)));
        } catch (IllegalArgumentException e) {
            // will be ignored
        }
    }

    /**
     * Returns a list of serialized {@link CrawleableUri} instances that have been
     * collected for the given URI.
     * 
     * @param uri
     *            The URI from which the returned serialized URIs have been
     *            collected.
     * @return An {@link Iterator} that iterates over the already serialized URIs
     *         that have been collected for the given URI.
     */
    public Iterator<byte[]> getUris(CrawleableUri uri);
    
    
    /**
     * 
     * Returns the total of uris that have been collected
     * 
     * @param uri
     *            The URI from which the returned serialized URIs have been
     *            collected.
     * @return A long number of total uris collected
     */
    public long getSize(CrawleableUri uri);

}
