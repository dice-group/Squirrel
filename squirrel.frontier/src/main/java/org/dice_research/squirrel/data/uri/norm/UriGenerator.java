package org.dice_research.squirrel.data.uri.norm;

import org.dice_research.squirrel.data.uri.CrawleableUri;

import java.util.List;

/**
 * An interface to generate different variants of a given URI
 */
public interface UriGenerator {
    /**
     * Generate a list of uris that are variants of the input uri
     * @param uri
     * @return list of variant uris
     */
    List<CrawleableUri> getUriVariants(CrawleableUri uri);
}
