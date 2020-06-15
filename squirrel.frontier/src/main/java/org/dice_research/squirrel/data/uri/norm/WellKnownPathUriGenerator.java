package org.dice_research.squirrel.data.uri.norm;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate well-known/VoID variant of a given URI
 */
public class WellKnownPathUriGenerator implements UriGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(NormalizerImpl.class);
    @Override
    public CrawleableUri getUriVariant(CrawleableUri uri) {
        URI uriObject = uri.getUri();
        String path = "/.well-known/void";
        URIBuilder builder = new URIBuilder(uriObject);
        builder.setPath(path);
        builder.setPort(-1);
        builder.setFragment(null);
        builder.setCustomQuery(null);
        CrawleableUri uriVariant = null;
        try {
            uriVariant = new CrawleableUri(builder.build());

        } catch (URISyntaxException e) {
            LOGGER.error("Exception while generating variant URIs", e);
        }
        return uriVariant;
    }
}
