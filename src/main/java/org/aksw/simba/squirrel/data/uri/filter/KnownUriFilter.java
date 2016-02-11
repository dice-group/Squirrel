package org.aksw.simba.squirrel.data.uri.filter;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

/**
 * A {@link UriFilter} that works like a blacklist filter and contains only thos
 * URIs on its blacklist that the crawler already has seen before.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface KnownUriFilter extends UriFilter {

    /**
     * Adds the given URI to the list of already known URIs.
     * 
     * @param uri
     */
    public void add(CrawleableUri uri);
}
