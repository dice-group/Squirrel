package org.dice_research.squirrel.analyzer.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.any23.Any23;
import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.configuration.ModifiableConfiguration;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Any23 Microdata Parser 
 * 
 * @author Meyer1995
 *
 */

public class MicrodataParser extends AbstractAnalyzer {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(MicrodataParser.class);

	
	public MicrodataParser(UriCollector collector) {
    	super(collector);
	}

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		
		ModifiableConfiguration modifiableConf = DefaultConfiguration.copy();

		modifiableConf.setProperty("any23.extraction.context.iri", curi.getUri().toString());	
		Any23 runner = new Any23(modifiableConf,"html-microdata");
						
		runner.setHTTPUserAgent(Any23.DEFAULT_HTTP_CLIENT_USER_AGENT);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TripleHandler handler = new NTriplesWriter(out);
		
		
		try {
			runner.extract(data, handler);
			handler.close();
			String result = out.toString("UTF-8");

			StreamRDF filtered = new FilterSinkRDF(curi, sink, collector); 
			RDFDataMgr.parse(filtered, new ByteArrayInputStream(result.getBytes()), Lang.NTRIPLES);
			
		} catch (IOException | ExtractionException | TripleHandlerException  e) {
			LOGGER.warn("Could not analyze file for URI: " + curi.getUri().toString() + " :: Analyzer: " + this.getClass().getName());
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
