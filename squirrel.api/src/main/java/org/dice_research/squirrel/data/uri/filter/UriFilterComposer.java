package org.dice_research.squirrel.data.uri.filter;

public interface UriFilterComposer extends UriFilter {
	
	public KnownUriFilter getKnownUriFilter();
	
	public void setKnownUriFilter(KnownUriFilter knownUriFilter);

}
