package org.dice_research.squirrel.data.uri.norm;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * An interface to generate different variants of a given URI
 */
public interface UriGenerator {
    /**
     * Generate new variant of the input uri
     * @param uri
     * @return new variant uri
     */
    CrawleableUri getUriVariant(CrawleableUri uri);
}
