package org.aksw.simba.squirrel.analyzer.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.semarglproject.rdf.NTriplesSerializer;
import org.semarglproject.rdf.ParseException;
import org.semarglproject.rdf.rdfa.RdfaParser;
import org.semarglproject.sink.CharOutputSink;
import org.semarglproject.sink.CharSink;
import org.semarglproject.source.StreamProcessor;
import org.semarglproject.vocab.RDFa;

public class RDFaSemarglParser implements Analyzer {

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		// TODO Auto-generated method stub
		
		CharOutputSink outputSink = new CharOutputSink();
		outputSink.setBaseUri(curi.getUri().toString());
			
		StreamProcessor sp = new StreamProcessor(
			RdfaParser.connect(NTriplesSerializer.connect(outputSink)));
		sp.setProperty(RdfaParser.RDFA_VERSION_PROPERTY, RDFa.VERSION_11);

		ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
		outputSink.connect(outputstream);
		
		//outputSink.connect(sink);
				
		//outputSink.setProperty(CharOutputSink.CHARSET_PROPERTY, "UTF-8");
		
		//sp.setProperty(RdfaParser.ENABLE_PROCESSOR_GRAPH, false);
			
		
		try {
			sp.process(data,curi.getUri().toString());
			sink.addData(curi,outputstream.toString("UTF-8"));
		} catch (ParseException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
}
