package org.dice_research.squirrel.queue.scorecalculator;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 *  Interface for getting score of a Uri.
 */
public interface IUriScoreCalculator {

    /**
     * Returns a score for the given URI based on the implemented scoring function.
     *
     * @return {@link CrawleableUri} score
     */
    float getURIScore(CrawleableUri uri);
}
