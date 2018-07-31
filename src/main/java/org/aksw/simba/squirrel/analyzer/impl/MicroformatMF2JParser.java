package org.aksw.simba.squirrel.analyzer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
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
		for (Entry<String, Object> string : parsed.entrySet()) {
		System.out.println(string.getKey() +" = "+string.getValue());
		}	
		return null;
	}

}
