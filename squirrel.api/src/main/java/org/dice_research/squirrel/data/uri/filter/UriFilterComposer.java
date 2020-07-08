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
	
	/**
	 * Returnsthe KnowUriFilter from this {@link UriFilterComposer}
	 * 
	 * @return KnownUriFilter
	 */
	public KnownUriFilter getKnownUriFilter();
	
	
	/**
	 * Set the KnowUriFilter for this {@link UriFilterComposer}
	 * 
	 * @param knownUriFilter
	 */
	public void setKnownUriFilter(KnownUriFilter knownUriFilter);

}
