package org.dice_research.squirrel.queue;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * This abstract class has methods to obtain URI based on a scoring scheme.
 * Each of the classes implementing this class can have their own scoring scheme.
 */
public abstract class AbstractURIScoreBasedQueue implements UriQueue, Comparator<CrawleableUri> {

    protected PriorityQueue<CrawleableUri> queue = new PriorityQueue<>();

    /**
     * Each class implementing this method can have its own scoring scheme.
     *
     * @return score
     */
    protected abstract float getURIScore(CrawleableUri uri);

    public void addUri(CrawleableUri uri) {
        float uriScore = getURIScore(uri);
        uri.addData(Constants.URI_DUPLICITY_SCORE, uriScore);
    }

    @Override
    public void open() {
        // nothing to do
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Returns a list of URIs which are sorted according to their score
     *
     * @return Returns a list of URIs which are sorted according to their score
     */
    public List<CrawleableUri> getNextUris() {
        return (List<CrawleableUri>) queue;
    }

    public int compare(CrawleableUri uri1, CrawleableUri uri2) {
        if (uri1.getData(Constants.URI_DUPLICITY_SCORE) != null && uri2.getData(Constants.URI_DUPLICITY_SCORE) != null) {
            Double uri1Score = (Double) uri1.getData(Constants.URI_DUPLICITY_SCORE);
            Double uri2Score = (Double) uri2.getData(Constants.URI_DUPLICITY_SCORE);
            if (uri1Score < uri2Score) {
                return -1;
            } else if (uri1Score > uri2Score) {
                return 1;
            }
        }
        return 0;
    }

}
