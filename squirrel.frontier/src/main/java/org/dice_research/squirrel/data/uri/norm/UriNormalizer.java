package org.dice_research.squirrel.data.uri.norm;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * A class which can normalize a URI, i.e., transform a given URI into a normal
 * form.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 */
@FunctionalInterface
public interface UriNormalizer {
    /**
     * Normalizes the given {@link CrawleableUri} object and may create a new,
     * normalized instance.
     * 
     * @param uri
     *            the URI that should be normalized
     * @return If the URI object has been changed, a new instance could be returned.
     */
    public CrawleableUri normalize(CrawleableUri uri);
}
