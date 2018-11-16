package org.dice_research.squirrel.analyzer.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.tika.Tika;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.AbstractAnalyzer;
import org.dice_research.squirrel.analyzer.commons.FilterSinkRDF;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.semarglproject.rdf.NTriplesSerializer;
import org.semarglproject.rdf.ParseException;
import org.semarglproject.rdf.rdfa.RdfaParser;
import org.semarglproject.sink.CharOutputSink;
import org.semarglproject.source.StreamProcessor;
import org.semarglproject.vocab.RDFa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Semargl RDFaParser 
 * 
 * @author Meyer1995
 *
 */

public class RDFaSemarglParser extends AbstractAnalyzer {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(RDFaSemarglParser.class);

	

	public RDFaSemarglParser(UriCollector collector) {
		super(collector);
	}

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
		
		try {
			sp.process(data,curi.getUri().toString());
			sink.addData(curi,outputstream.toString("UTF-8"));

			 StreamRDF filtered = new FilterSinkRDF(curi, sink, collector); 
			 RDFDataMgr.parse(filtered, new ByteArrayInputStream(outputstream.toString("UTF-8").getBytes()), Lang.NTRIPLES);
			
		} catch (ParseException | UnsupportedEncodingException e) {
			LOGGER.error("Exception while analyzing. Aborting. ", e);
		}		
		
		return collector.getUris(curi);
	}

	@Override
	public boolean isElegible(CrawleableUri curi, File data) {
		String contentType = (String) curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);
        if ((contentType != null && contentType.equals("text/html"))) {
            return true;
        }
        Tika tika = new Tika();
        try (InputStream is = new FileInputStream(data)) {
            String mimeType = tika.detect(is);
            if (mimeType.equals("text/html")) {
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("An error was found when verify eligibility", e);
        }
        return false;
	}
}
