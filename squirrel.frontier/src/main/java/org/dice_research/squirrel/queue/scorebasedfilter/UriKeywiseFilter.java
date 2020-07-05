package org.dice_research.squirrel.queue.scorebasedfilter;

import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.queue.scorecalculator.IUriScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class filters the {@link CrawleableUri}s to be added to the queue based on the score.
 */
public class UriKeywiseFilter<T> implements IUriKeywiseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UriKeywiseFilter.class);
    private IUriScoreCalculator scoreCalculator;
    private float criticalScore = .2f;      //  default value
    private int minNumberOfUrisToCheck = 5;      //  default value


    public UriKeywiseFilter(IUriScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    public UriKeywiseFilter(IUriScoreCalculator scoreCalculator, float criticalScore, int minNumberOfUrisToCheck) {
        this.scoreCalculator = scoreCalculator;
        this.criticalScore = criticalScore;
        this.minNumberOfUrisToCheck = minNumberOfUrisToCheck;
    }

    @Override
    public Map<T, List<CrawleableUri>> filterUrisKeywise(Map keyWiseUris) {
        Map<T, List<CrawleableUri>> filteredUriMap = new HashMap<>();
        for (Map.Entry<T, List<CrawleableUri>> entry:((Map<T, List<CrawleableUri>>)keyWiseUris).entrySet()) {
            boolean scoresBelowCritical = true;
            List<CrawleableUri> uriList = entry.getValue();
            for (int i = 0; i < (minNumberOfUrisToCheck < uriList.size() ? minNumberOfUrisToCheck : uriList.size()); i++) {
                float score = scoreCalculator.getURIScore(uriList.get(i));
                if (score > criticalScore) {
                    scoresBelowCritical = false;
                    break;
                }
            }
            if (!scoresBelowCritical) {
                for(CrawleableUri uri:uriList) {
                    uri.addData(Constants.URI_SCORE, scoreCalculator.getURIScore(uri));
                }
                filteredUriMap.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredUriMap;
    }

    public float getCriticalScore() {
        return criticalScore;
    }

    public void setCriticalScore(float criticalScore) {
        this.criticalScore = criticalScore;
    }

    public int getMinNumberOfUrisToCheck() {
        return minNumberOfUrisToCheck;
    }

    public void setMinNumberOfUrisToCheck(int minNumberOfUrisToCheck) {
        this.minNumberOfUrisToCheck = minNumberOfUrisToCheck;
    }
}

