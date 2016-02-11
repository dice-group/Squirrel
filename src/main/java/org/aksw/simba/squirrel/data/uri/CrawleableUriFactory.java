package org.aksw.simba.squirrel.data.uri;

import java.net.URI;

/**
 * This factory generates {@link CrawleableUri} instances. Note that the URIs
 * used for the generation of these instances might have to fulfill certain
 * requirements imposed by the different implementations of this interface.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface CrawleableUriFactory {

    /**
     * Creates a {@link CrawleableUri} from the given URI String.
     * 
     * @param uri
     *            the URI that should be crawleable.
     * @return A {@link CrawleableUri} instance based on the given URI string or
     *         null, if the given URI did not fulfill the requirements of a
     *         crawleable URI.
     */
    public CrawleableUri create(String uri);

    /**
     * Creates a {@link CrawleableUri} from the given {@link URI} instance.
     * 
     * @param uri
     *            the URI that should be crawleable.
     * @return A {@link CrawleableUri} instance based on the given {@link URI}
     *         instance or null, if the given URI did not fulfill the
     *         requirements of a crawleable URI.
     */
    public CrawleableUri create(URI uri);

    /**
     * Creates a {@link CrawleableUri} from the given {@link URI} instance and
     * the given {@link UriType}
     * 
     * @param uri
     *            the URI that should be crawleable.
     * @param type
     * @return A {@link CrawleableUri} instance based on the given {@link URI}
     *         instance and the given {@link UriType} or null, if the given
     *         {@link URI} did not fulfill the requirements of a crawleable URI.
     */
    public CrawleableUri create(URI uri, UriType type);
}
