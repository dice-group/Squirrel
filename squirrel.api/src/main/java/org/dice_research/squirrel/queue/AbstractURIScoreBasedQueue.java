package org.dice_research.squirrel.queue;

import java.util.ArrayList;
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

    protected PriorityQueue<CrawleableUri> queue;

    public AbstractURIScoreBasedQueue() {
        queue = new PriorityQueue<>(100, this);
    }
    /**
     * Each class implementing this method can have its own scoring scheme.
     *
     * @return score
     */
    protected abstract float getURIScore(CrawleableUri uri);

    public void addUri(CrawleableUri uri) {
        float uriScore = getURIScore(uri);
        uri.addData(Constants.URI_DUPLICITY_SCORE, uriScore);
        queue.add(uri);
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
        List<CrawleableUri> uriList = new ArrayList<>(queue);
        return uriList;
    }

    public int compare(CrawleableUri uri1, CrawleableUri uri2) {
        if (uri1.getData(Constants.URI_DUPLICITY_SCORE) != null && uri2.getData(Constants.URI_DUPLICITY_SCORE) != null) {
            float uri1Score = (float) uri1.getData(Constants.URI_DUPLICITY_SCORE);
            float uri2Score = (float) uri2.getData(Constants.URI_DUPLICITY_SCORE);
            if (uri1Score < uri2Score) {
                return -1;
            } else if (uri1Score > uri2Score) {
                return 1;
            }
        }
        return 0;
    }

}
