package org.dice_research.squirrel.analyzer;

import java.io.File;
import java.util.Iterator;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;

public interface Analyzer {
	
	

    public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink);

    public boolean isElegible(CrawleableUri curi, File data);

}
