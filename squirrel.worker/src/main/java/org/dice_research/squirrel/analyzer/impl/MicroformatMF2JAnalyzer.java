package org.dice_research.squirrel.analyzer.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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

import com.kylewm.mf2j.Mf2Parser;

/**
 * 
 * MF2JParser for Microformats
 * 
 * @author Meyer1995
 *
 */

public class MicroformatMF2JAnalyzer extends AbstractAnalyzer {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MicroformatMF2JAnalyzer.class);

	public MicroformatMF2JAnalyzer(UriCollector collector) {
		super(collector);
	}

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		String file = "";
		BufferedReader br;
		String line;
		String result = "";
		try {
			br = new BufferedReader(new FileReader(data));
			while ((line = br.readLine()) != null) {
			     file+= line+"\n";
			}
			
			Mf2Parser parser = new Mf2Parser()
				    .setIncludeAlternates(true)
				    .setIncludeRelUrls(true);
				//Map<String,Object> parsed = parser.parse(microdata5, new URI("https://kylewm.com"));
				Map<String,Object> parsed = parser.parse(file,URI.create(curi.getUri().toString()));

				
				String json = addContextToJSON(parsed.toString());
				json = replaceVocab(json);
				Model model = createModelFromJSONLD(json);
				String syntax = "N-TRIPLE";
				StringWriter out = new StringWriter();
				model.write(out, syntax);
				result = out.toString();
				StreamRDF filtered = new FilterSinkRDF(curi, sink, collector,tripleEncoder); 
				RDFDataMgr.parse(filtered, new ByteArrayInputStream(result.getBytes()), Lang.NTRIPLES);	
		} catch (Exception e) {
			LOGGER.warn("Could not analyze file for URI: " + curi.getUri().toString() + " :: Analyzer: " + this.getClass().getName());

		}

		return collector.getUris(curi);
	}
	
	public static String addContextToJSON(String data) {
		String dt = data.trim();
		dt = dt.substring(1);
		dt = "{\r\n" + 
				"\"@context\": {\"@vocab\": \"http://www.dummy.org/#\"},\n"+dt;
		return dt;
	}
	
	public static String replaceVocab(String data) {
		return data.replace("http://www.dummy.org/#", "http://www.w3.org/2006/vcard/ns#");
	}
	
	/**
	 * Creates a Model from JSON-LD
	 * @param content the data in JSON-LD
	 * @return the model
	 */
	public static Model createModelFromJSONLD(String content) {
		Model model = null;
		try {
			model = ModelFactory.createDefaultModel()
			        .read(IOUtils.toInputStream(content, "UTF-8"), null, "JSON-LD");
		} catch (IOException e) {
			LOGGER.error("Exception while analyzing. Aborting. ", e);
		}
	    return model;
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
