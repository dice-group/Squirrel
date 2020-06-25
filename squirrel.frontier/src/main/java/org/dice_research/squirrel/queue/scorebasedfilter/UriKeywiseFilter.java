package org.dice_research.squirrel.queue.scorebasedfilter;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.queue.scorecalculator.IUriScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class filters the {@link CrawleableUri}s to be added to the queue based on the score.
 */
public class UriKeywiseFilter implements IUriKeywiseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UriKeywiseFilter.class);
    private IUriScoreCalculator scoreCalculator;

    public UriKeywiseFilter(IUriScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    public Map<CrawleableUri, Float> filterUrisKeywise(Map keyWiseUris, int minNumberOfUrisToCheck, float criticalScore) {
        Collection<List<CrawleableUri>> uriLists = keyWiseUris.values();
        Map<CrawleableUri, Float> filteredUriMap = new HashMap<>();
        for (List<CrawleableUri> uriList : uriLists) {
            boolean scoresBelowCritical = true;
            for (int i = 0; i < (minNumberOfUrisToCheck < uriList.size() ? minNumberOfUrisToCheck : uriList.size()); i++) {
                float score = scoreCalculator.getURIScore(uriList.get(i));
                if (score > criticalScore) {
                    scoresBelowCritical = false;
                }
            }
            if (!scoresBelowCritical) {
                for (CrawleableUri uri : uriList) {
                    filteredUriMap.put(uri, scoreCalculator.getURIScore(uri));
                }
            }
        }
        return filteredUriMap;
    }
}

