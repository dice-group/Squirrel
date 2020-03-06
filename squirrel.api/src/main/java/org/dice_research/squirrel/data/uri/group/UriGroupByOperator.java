package org.dice_research.squirrel.data.uri.group;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * A URI group by operator defines they way how several URIs are transformed
 * into groups or chunks of URIs based on a certain key value they share.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 * @param <T>
 *            the class of the key
 */
public interface UriGroupByOperator<T> {

    /**
     * Retrieves the key value (i.e., the identifier of its group) from the given
     * URI
     * 
     * @param uri
     *            the URI for which the key value should be retrieved.
     * @return the key value
     */
    public T retrieveKey(CrawleableUri uri);

    /**
     * Transforms the given set of URIs into groups based on their key values that
     * are retrieved for each of them.
     * 
     * @param uris
     *            the set of URIs which should be grouped
     * @return a mapping from the key values (i.e., group identifiers) to the single
     *         groups
     */
    @SuppressWarnings("unchecked")
    public default Map<T, List<CrawleableUri>> groupByKey(Collection<CrawleableUri> uris) {
        if (uris == null) {
            return Collections.EMPTY_MAP;
        }
        final UriGroupByOperator<T> groupByOperator = this;
        return uris.stream().collect(Collectors.groupingBy(c -> (T) groupByOperator.retrieveKey((CrawleableUri) c)));
    }
}
