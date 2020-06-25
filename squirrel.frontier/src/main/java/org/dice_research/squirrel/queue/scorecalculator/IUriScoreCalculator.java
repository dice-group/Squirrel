package org.dice_research.squirrel.queue.scorecalculator;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 *  Interface for getting duplicity score of a Uri.
 */
public interface IUriScoreCalculator {

    /**
     * Returns the score based on specific score calculation implementation
     *
     * @return {@link CrawleableUri} score
     */
    float getURIScore(CrawleableUri uri);
}
