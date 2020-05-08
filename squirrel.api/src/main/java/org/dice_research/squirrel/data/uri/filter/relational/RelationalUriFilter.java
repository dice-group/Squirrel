package org.dice_research.squirrel.data.uri.filter.relational;

import org.dice_research.squirrel.data.uri.filter.KnownUriFilter;
import org.dice_research.squirrel.data.uri.filter.UriFilter;

public interface RelationalUriFilter extends UriFilter {
	
	public KnownUriFilter getKnownUriFilter();
	
	public void setKnownUriFilter(KnownUriFilter knownUriFilter);

}
