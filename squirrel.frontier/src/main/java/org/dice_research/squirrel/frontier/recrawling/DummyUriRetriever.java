package org.dice_research.squirrel.frontier.recrawling;

import java.util.ArrayList;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * 
 * A dummmy implementation to return an empty list
 * 
 * @author Geraldo de Souza Junior  - gsjunior@uni-paderborn.de
 *
 */
public class DummyUriRetriever implements OutDatedUriRetriever {

	@Override
	public List<CrawleableUri> getUriToRecrawl() {
		// TODO Auto-generated method stub
		return new ArrayList<CrawleableUri> ();
	}

}
