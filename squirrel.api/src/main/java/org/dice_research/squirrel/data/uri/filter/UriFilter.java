package org.dice_research.squirrel.data.uri.filter;

import org.dice_research.squirrel.data.uri.CrawleableUri;

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
    
    
    /**
     * Adds the given URI to the list of already known URIs. Works like calling {@link #add(CrawleableUri, long)} with the current system time.
     *
     * @param uri the URI that should be added to the list.
     * 
     */    
    public void add(CrawleableUri uri);
}
