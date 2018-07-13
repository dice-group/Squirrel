package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;

public class HDTAnalyzer implements Analyzer{

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		 try {
			HDT hdt = HDTManager.loadHDT(data.getAbsolutePath(), null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
