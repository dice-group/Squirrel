package org.aksw.simba.squirrel.data.uri;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCrawleableUriFactory implements CrawleableUriFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCrawleableUriFactory.class);

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
            return new CrawleableUri(uri, ip);
        } catch (UnknownHostException e) {
            LOGGER.info("Couldn't get the IP address for \"" + uri + "\". Returning null.", e);
        }
        return null;
    }

}
