package org.aksw.simba.squirrel.deduplication.hashing;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.util.List;
import java.util.Set;

/**
 * This component maintains {@link HashValue}s for uris. It provides methods for getting uris with some desired hash values,
 * and also for adding hash values for given uris.
 * The idea is that hash values could be stored in the {@link org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter},
 * as well as in the {@link org.aksw.simba.squirrel.sink.tripleBased.TripleBasedSink}.
 */
public interface UriHashCustodian {

    /**
     * Get all uris that have a common hash value with one of the hash values of the given set.
     *
     * @param hashValuesForComparison The given set of hash values.
     * @return All uris that have a common hash value with one of the hash values of the given list.
     */
    Set<CrawleableUri> getUrisWithSameHashValues(Set<HashValue> hashValuesForComparison);

    /**
     * Add the given hash values for the given uris. Hash values are contained in the uris.
     *
     * @param uris The given uris.
     */
    void addHashValuesForUris(List<CrawleableUri> uris);
}
