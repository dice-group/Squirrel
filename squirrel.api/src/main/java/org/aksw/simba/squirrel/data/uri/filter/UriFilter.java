package org.aksw.simba.squirrel.data.uri.filter;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

/**
 * A simple filter that can decide whether a given {@link CrawleableUri} object
 * imposes a certain requirement or not.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface UriFilter {

    /**
     * Returns true if the given {@link CrawleableUri} object fulfills the
     * requirements imposed by this filter.
     * 
     * @param uri
     *            the {@link CrawleableUri} object that is checked
     * @return true if the given {@link CrawleableUri} object fulfills the
     *         requirements imposed by this filter. Otherwise false is returned.
     */
    public boolean isUriGood(CrawleableUri uri);
}
