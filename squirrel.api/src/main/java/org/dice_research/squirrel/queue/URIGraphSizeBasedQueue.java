package org.dice_research.squirrel.queue;

import org.dice_research.squirrel.data.uri.CrawleableUri;

public class URIGraphSizeBasedQueue extends AbstractURIRankBasedQueue {

    public URIGraphSizeBasedQueue() {
    }

	protected double getURIRank(CrawleableUri uri) {
		double uriRank = getGraphSize();
		return uriRank;
	}

	private double getGraphSize() {
		// Query sparql endpoint to get the graph sizefor a node
        return 0;
	}

}
