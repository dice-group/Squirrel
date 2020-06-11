package org.dice_research.squirrel.queue.scorecalculator;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 *
 * Interface for getting score based on specific score calculators
 *
 */
public interface IURIScoreCalculator {
    /**
     * Returns the score based on specific score calculation implementation
     *
     * @return uri score
     *
     */
    float getURIScore(CrawleableUri uri);
}
