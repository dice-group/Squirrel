package org.aksw.simba.squirrel.analyzer.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.any23.Any23;
import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.configuration.ModifiableConfiguration;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.jena.sparql.function.library.namespace;

public class MicroformatParser implements Analyzer {

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		
		String[] formats = new String[] {"html-mf-adr","html-mf-geo", "html-mf-hcalendar","html-mf-hcard","html-mf-hlisting","html-mf-hrecipe","html-mf-hresume","html-mf-hreview","html-mf-hreview-aggregate","html-mf-license","html-mf-species","html-mf-xfn"};
		ModifiableConfiguration modifiableConf = DefaultConfiguration.copy();
		String oldPropertyValue = modifiableConf.setProperty("any23.extraction.context.iri", curi.getUri().toString());	
		Any23 runner = new Any23(modifiableConf,formats);
				
		runner.setHTTPUserAgent(runner.DEFAULT_HTTP_CLIENT_USER_AGENT);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TripleHandler handler = new NTriplesWriter(out);
		
		try {
			runner.extract(data, handler);
			handler.close();
			String result = out.toString("UTF-8");
			//System.out.println(result);
		    sink.addData(curi, result);
		} catch (IOException | ExtractionException | TripleHandlerException e) {
			e.printStackTrace();
		}    
		return null;
	}

}
