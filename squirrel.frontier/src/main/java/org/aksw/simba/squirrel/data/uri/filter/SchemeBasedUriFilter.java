package org.aksw.simba.squirrel.data.uri.filter;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This {@link UriFilter} checks whether the given URI has a known scheme.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class SchemeBasedUriFilter implements UriFilter {

    /**
     * The default set of accepted schemes.
     */
    public static final String DEFAULT_SCHEMES[] = new String[] { "http", "https" };

    /**
     * The set of accepted schemes.
     */
    protected Set<String> schemes;

    /**
     * Constructor using the {@link #DEFAULT_SCHEMES}.
     */
    public SchemeBasedUriFilter() {
        this(new HashSet<>(Arrays.asList(DEFAULT_SCHEMES)));
    }

    /**
     * Constructor.
     *
     * @param schemes
     *            the schemes that are accepted by this filter.
     */
    public SchemeBasedUriFilter(Set<String> schemes) {
        this.schemes = schemes;
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        String scheme = uri.getUri().getScheme();
        return (scheme != null) && (schemes.contains(scheme));
    }

    public Set<String> getSchemes() {
        return schemes;
    }

    public void setSchemes(Set<String> schemes) {
        this.schemes = schemes;
    }

}
