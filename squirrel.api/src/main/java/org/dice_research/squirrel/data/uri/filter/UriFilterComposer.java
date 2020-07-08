package org.dice_research.squirrel.data.uri.filter;

/**
 * 
 * This class represents a composition of two or more filters,
 * requiring at least one @link {org.dice_research.squirrel.data.uri.filter.KnownUriFilter}
 * 
 *  * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 * 
 */

public interface UriFilterComposer extends UriFilter {
	
	public KnownUriFilter getKnownUriFilter();
	
	public void setKnownUriFilter(KnownUriFilter knownUriFilter);

}
