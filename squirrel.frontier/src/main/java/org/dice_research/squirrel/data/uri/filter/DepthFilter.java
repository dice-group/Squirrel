package org.dice_research.squirrel.data.uri.filter;

import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Depth Filter implementation
 * 
 * Check if the uriIsGood based on the current depth level
 * 
 * * @author Geraldo Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */

public class DepthFilter implements UriFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBKnowUriFilter.class);

	/**
	 * The maximum depth allowed for the filter
	 */
	private int max_depth;


	public DepthFilter(int max_depth) {
		this.max_depth = max_depth;
	}

	@Override
	public boolean isUriGood(CrawleableUri uri) {
		
			if (uri.getData().containsKey(Constants.URI_DEPTH)) {
				int depth = Integer.parseInt(uri.getData(Constants.URI_DEPTH).toString());
				if (depth > max_depth) {
					LOGGER.debug("Max Depth reached. Uri {} is not good", uri.toString());
					return false;
				}else {
					LOGGER.debug("URI {} is good", uri.toString());
					return true;
				}
				
			} else {
				LOGGER.debug("Depth depth is not being stored for Uri :{} . Please check the queue parameters.", uri.toString());
				return false;
			}
				

	}


	@Override
	public void add(CrawleableUri uri) {
		// TODO Auto-generated method stub
		
	}


}
