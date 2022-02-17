package org.dice_research.squirrel.data.uri.filter;

import java.util.ArrayList;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * 
 * This class represents a Relational Uri Filter for the AND and OR operators
 * It is possible to compose two or more filters and use then with relational operators
 * 
 * The constructor requires at least one @link {org.dice_research.squirrel.data.uri.filter.KnownUriFilter}
 * a list of @link {org.dice_research.squirrel.data.uri.filter.UriFilter}
 * and the Operator. The operator can be <<AND>> or <<OR>>.
 * 
 * If the Operator is <<AND>>, the isUriGood(CrawleableUri) method will return true if
 *  all the UriFilters will return true in their respectives isUriGood(CrawleableUri) methods 
 *  
 *  In case the Operator is <<OR>>, if at least one of then returns true, the isUriGood(CrawleableUri) method will return true.

 * 
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */

public class UriFilterConfigurator implements UriFilterComposer {

	private KnownUriFilter knownUriFilter;
	private List<UriFilter> listUriFilters;
	private final String OPERATOR;

	public UriFilterConfigurator(KnownUriFilter knownUriFilter, List<UriFilter> listUriFilters,String operator) {
		this.knownUriFilter = knownUriFilter;
		this.listUriFilters = listUriFilters;
		this.OPERATOR = operator;
	}

	public UriFilterConfigurator(KnownUriFilter knownUriFilter,String operator) {
		this.knownUriFilter = knownUriFilter;
		this.listUriFilters = new ArrayList<UriFilter>();
		this.OPERATOR = operator;
	}

	@Override
	public boolean isUriGood(CrawleableUri uri) {

		if(this.OPERATOR.equals("OR"))
			return computeOrOperation(uri);
		else 
			return computeAndOperation(uri);
	}

	private boolean computeAndOperation(CrawleableUri uri) {
		boolean isUrisGood = false;

		for (UriFilter uriFilter : listUriFilters) {
			isUrisGood = uriFilter.isUriGood(uri);
			if(!isUrisGood)
				break;
		}

		return isUrisGood && knownUriFilter.isUriGood(uri);
	}
	
	private boolean computeOrOperation(CrawleableUri uri) {
		boolean isUrisGood = false;

		for (UriFilter uriFilter : listUriFilters) {
			isUrisGood = uriFilter.isUriGood(uri);
			if(isUrisGood)
				break;
		}

		return isUrisGood || knownUriFilter.isUriGood(uri);
	}

	@Override
	public void add(CrawleableUri uri) {
		knownUriFilter.add(uri);
		listUriFilters.forEach(f -> f.add(uri));
	}

	@Override
	public KnownUriFilter getKnownUriFilter() {
		return this.knownUriFilter;
	}

	@Override
	public void setKnownUriFilter(KnownUriFilter knownUriFilter) {
		this.knownUriFilter = knownUriFilter;
	}

}
