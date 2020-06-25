package org.dice_research.squirrel.queue.scorebasedfilter;

import org.dice_research.squirrel.data.uri.CrawleableUri;

import java.util.List;
import java.util.Map;

/**
 * Interface for filtering URIs based on duplicity score
 */
public interface IUriKeywiseFilter<T> {

    /**
     * This method returns the {@link CrawleableUri}s to be added to the queue after filtering out the ones based on their score
     *
     * @param keyWiseUris            map of based on key {@link CrawleableUri}s to be filtered
     * @param minNumberOfUrisToCheck minimum number of {@link CrawleableUri}s to be checked for their score before filtering
     * @param criticalScore          the critical score to check for filtering
     * @return {@link Map} of {@link CrawleableUri}s to be added to the queue with their scores
     */
    Map<CrawleableUri, Float> filterUrisKeywise(Map<T, List<CrawleableUri>> keyWiseUris, int minNumberOfUrisToCheck, float criticalScore);
}
