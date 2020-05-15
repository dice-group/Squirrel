package org.dice_research.squirrel.queue;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * This abstract class manages has methods to obtain URI based on a ranking scheme.
 * Each of the classes implementing this class can have their own ranking scheme.
 *
 */
public abstract class AbstractURIRankBasedQueue implements UriQueue, Comparator<CrawleableUri> {

	protected PriorityQueue<CrawleableUri> queue = new PriorityQueue<CrawleableUri>();

    /**
     * Each class implementing this method can have its own ranking scheme.
     * @return rank
     */
	protected abstract double getURIRank(CrawleableUri uri);

    public void addUri(CrawleableUri uri) {
    	double uriRank = getURIRank(uri);
    	uri.addData("", uriRank);
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
	 * Returns a list of URIs which are sorted according to their rank
	 *
	 * @return Returns a list of URIs which are sorted according to their rank
	 */
	public List<CrawleableUri> getNextUris() {
		return (List<CrawleableUri>)queue;
	}

	public int compare(CrawleableUri uri1, CrawleableUri uri2) {
		if(uri1.getData("")!=null && uri2.getData("")!=null) {
			Double uri1Rank = (Double) uri1.getData("");
			Double uri2Rank = (Double) uri2.getData("");
			if (uri1Rank < uri2Rank) {
				return -1;
			} else if (uri1Rank > uri1Rank) {
				return 1;
			}
		}
		return 0;
	}

}
