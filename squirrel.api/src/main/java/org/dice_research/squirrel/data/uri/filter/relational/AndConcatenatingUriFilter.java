package org.dice_research.squirrel.data.uri.filter.relational;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.filter.KnownUriFilter;
import org.dice_research.squirrel.data.uri.filter.UriFilter;


/**
 * 
 * Relational Uri Filter for the AND operator
 * 
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */

public class AndConcatenatingUriFilter implements RelationalUriFilter{
	
	private KnownUriFilter knownUriFilter;
	private UriFilter uriFilter;
	
	public AndConcatenatingUriFilter(KnownUriFilter knownUriFilter, UriFilter uriFilter) {
		this.knownUriFilter = knownUriFilter;
		this.uriFilter = uriFilter;
	}
	
	public AndConcatenatingUriFilter(KnownUriFilter knownUriFilter) {
		this.knownUriFilter = knownUriFilter;
	}

	@Override
	public boolean isUriGood(CrawleableUri uri) {
		
		if(uriFilter != null) 
			return knownUriFilter.isUriGood(uri) && uriFilter.isUriGood(uri);
		else
			return knownUriFilter.isUriGood(uri);
	}

	@Override
	public void add(CrawleableUri uri) {
		knownUriFilter.add(uri);
		uriFilter.add(uri);
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
