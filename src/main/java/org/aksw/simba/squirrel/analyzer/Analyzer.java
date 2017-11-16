package org.aksw.simba.squirrel.analyzer;


import java.io.File;
import java.util.Iterator;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;

public interface Analyzer {
	
	
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink);
	
}
