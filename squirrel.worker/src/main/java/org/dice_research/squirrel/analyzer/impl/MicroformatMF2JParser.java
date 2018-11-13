package org.dice_research.squirrel.analyzer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kylewm.mf2j.Mf2Parser;

import tdb.tools.dumpbpt;

public class MicroformatMF2JParser implements Analyzer {

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		String file = "";
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new FileReader(data));
			while ((line = br.readLine()) != null) {
			     file+= line+"\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Mf2Parser parser = new Mf2Parser()
		    .setIncludeAlternates(true)
		    .setIncludeRelUrls(true);
		//Map<String,Object> parsed = parser.parse(microdata5, new URI("https://kylewm.com"));
		Map<String,Object> parsed = parser.parse(file,URI.create(curi.getUri().toString()));
//		for (Entry<String, Object> string : parsed.entrySet()) {
//		System.out.println(string.getKey() +" = "+string.getValue());
//		}
		
		String json = addContextToJSON(parsed.toString());
		json = replaceVocab(json);
		//System.out.println(json);
		Model model = createModelFromJSONLD(json);
		String syntax = "N-TRIPLE";
		StringWriter out = new StringWriter();
		model.write(out, syntax);
		String result = out.toString();	
		sink.addData(curi, result);
		return null;
	}
	
	public static String addContextToJSON(String data) {
		data = data.trim();
		data = data.substring(1);
		data = "{\r\n" + 
				"\"@context\": {\"@vocab\": \"http://www.dummy.org/#\"},\n"+data;
		return data;
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
			e.printStackTrace();
		}
	    //System.out.println("model size: " + model.size());
	    return model;
	}

	@Override
	public boolean isElegible(CrawleableUri curi, File data) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
