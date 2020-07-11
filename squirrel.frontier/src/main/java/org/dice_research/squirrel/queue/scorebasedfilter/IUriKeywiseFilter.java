package org.dice_research.squirrel.queue.scorebasedfilter;

import org.dice_research.squirrel.data.uri.CrawleableUri;

import java.util.List;
import java.util.Map;

/**
 * Interface for filtering URIs.
 */
public interface IUriKeywiseFilter<T> {

    /**
     * This method returns the {@link CrawleableUri}s to be added to the queue.
     *
     * @param keyWiseUris map of based on key {@link CrawleableUri}s to be filtered
     * @return {@link Map} of filtered {@link CrawleableUri}s to be added to the queue with their scores
     */
    Map<T, List<CrawleableUri>> filterUrisKeywise(Map<T, List<CrawleableUri>> keyWiseUris);
}
