package org.dice_research.squirrel.analyzer.impl;

import java.io.File;
import java.util.Iterator;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.dice_research.squirrel.analyzer.AbstractAnalyzer;
import org.dice_research.squirrel.analyzer.commons.FilterSinkRDF;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonAnalyzer extends AbstractAnalyzer {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonAnalyzer.class);

	public JsonAnalyzer(UriCollector collector) {
		super(collector);
	}

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		try {
			StreamRDF filtered = new FilterSinkRDF(curi, sink, collector,tripleEncoder);
			RDFDataMgr.parse(filtered, data.getAbsolutePath(), Lang.JSONLD);
			return collector.getUris(curi);
		} catch (Exception e) {
			LOGGER.error("Exception while analyzing. Aborting. ", e);
			return null;
		}
	}

	@Override
	public boolean isElegible(CrawleableUri curi, File data) {
		return curi.getData("type").equals("json");
	}

}
