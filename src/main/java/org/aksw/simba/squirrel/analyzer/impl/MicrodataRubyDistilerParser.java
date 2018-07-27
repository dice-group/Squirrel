package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;

public class MicrodataRubyDistilerParser implements Analyzer {

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		// TODO Auto-generated method stub
		// http://rdf.greggkellogg.net/distiller?command=serialize
		// An Adresse HTML GET Request senden um Daten zu bekommen.
		return null;
	}

}
