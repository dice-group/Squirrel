package org.aksw.simba.squirrel.data.uri;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.aksw.simba.squirrel.data.uri.filter.UriFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple implementation of a {@link CrawleableUriFactory} that is expandable
 * by UriFilter instances.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class CrawleableUriFactoryImpl implements CrawleableUriFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawleableUriFactoryImpl.class);

    /**
     * URI filters applied to the URIs during the generation.
     */
    protected UriFilter filters[];

    /**
     * The default constructor that imposes no additional requirements.
     */
    public CrawleableUriFactoryImpl() {
        this(new UriFilter[0]);
    }

    /**
     * Constructor taking additional filters that are used to check URIs during
     * the creation.
     * 
     * @param filters
     */
    public CrawleableUriFactoryImpl(UriFilter... filters) {
        this.filters = filters;
    }

    @Override
    public CrawleableUri create(String uri) {
        try {
            return create(new URI(uri));
        } catch (Exception e) {
            LOGGER.info("The given URI \"" + uri + "\" couldn't be parsed. Returning null.", e);
            return null;
        }
    }

    @Override
    public CrawleableUri create(URI uri) {
        return create(uri, UriType.UNKNOWN);
    }

    @Override
    public CrawleableUri create(URI uri, UriType type) {
        try {
            InetAddress ip = InetAddress.getByName(uri.getHost());
            return filter(new CrawleableUri(uri, ip, type));
        } catch (UnknownHostException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Couldn't get the IP address for \"" + uri + "\". Returning null.", e);
            } else {
                LOGGER.info("Couldn't get the IP address for \"" + uri + "\". Returning null.");
            }
        }
        return null;
    }

    /**
     * Returns the given {@link CrawleableUri} instance if all local
     * {@link #filters} marked it as a good URI. Otherwise null is returned.
     * 
     * @param createdUri
     *            the URI that should be checked using the local filters.
     * @return the given {@link CrawleableUri} instance if it is a good URI.
     *         Otherwise null is returned.
     */
    protected CrawleableUri filter(CrawleableUri createdUri) {
        for (int i = 0; i < filters.length; ++i) {
            if (!filters[i].isUriGood(createdUri)) {
                return null;
            }
        }
        return createdUri;
    }

}
