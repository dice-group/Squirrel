package org.dice_research.squirrel.data.uri.norm;

import org.apache.http.client.utils.URIBuilder;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class UriGeneratorImpl implements UriGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(NormalizerImpl.class);

    @Override
    public List<CrawleableUri> getUriVariants(CrawleableUri uri) {
        List<CrawleableUri> variantUris = new ArrayList<>();
        URI newUri = null;
        URI uriObject = uri.getUri();

        // converting the uri to it's domain
        String domain = uriObject.getHost();
        try {
            newUri = new URI(domain);
            CrawleableUri uriVariant = new CrawleableUri(newUri);
            variantUris.add(uriVariant);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception while generating variant URIs", e);
        }

        // creating .wellknown/void variant
        String path = "/.well-known/void";
        URIBuilder builder = new URIBuilder(uriObject);
        builder.setPath(path);
        builder.setPort(-1);
        builder.setFragment(null);
        builder.setCustomQuery(null);
        try {
            CrawleableUri crawlUri = new CrawleableUri(builder.build());
            variantUris.add(crawlUri);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception while generating variant URIs", e);
        }
        return variantUris;
    }
}
