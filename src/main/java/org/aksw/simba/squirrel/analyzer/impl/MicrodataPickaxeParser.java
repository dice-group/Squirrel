package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.commons.io.IOUtils;
import org.apache.jena.Jena;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.github.mautini.pickaxe.Scraper;
import com.google.schemaorg.JsonLdSerializer;
import com.google.schemaorg.core.Thing;

import net.rootdev.javardfa.StatementSink;

public class MicrodataPickaxeParser implements Analyzer {

//	  <dependency>
//    <groupId>com.github.mautini</groupId>
//    <artifactId>schemaorg-java</artifactId>
//    <version>1.0.1</version>
//    </dependency>
	
	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
		Scraper scraper = new Scraper();
		try {
			List<Thing> thingList = scraper.extract(data);
			for (Thing things : thingList) {			
				JsonLdSerializer serializer = new JsonLdSerializer(true /* setPrettyPrinting */);
				String jsonLdStr = serializer.serialize(things);
						    
				Model model = RDFParserTest.createModelFromJSONLD(jsonLdStr);
				String syntax = "N-TRIPLE"; //"N-TRIPLE" and "TURTLE"
				StringWriter out = new StringWriter();
				model.write(out, syntax);
				String result = out.toString();			
				
				sink.addData(curi, result);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return null;
	}	
}
