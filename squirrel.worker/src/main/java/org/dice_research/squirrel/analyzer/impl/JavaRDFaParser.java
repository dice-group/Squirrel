package org.dice_research.squirrel.analyzer.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Factory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hamcrest.core.Is;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import net.rootdev.javardfa.ParserFactory;
import net.rootdev.javardfa.ParserFactory.Format;
import net.rootdev.javardfa.jena.JenaStatementSink;
import net.rootdev.javardfa.Setting;
import net.rootdev.javardfa.StatementSink;

public class JavaRDFaParser implements Analyzer {

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
//		try {
//			Model m = ModelFactory.createDefaultModel();
//			
//	        StatementSink statesink = new org.aksw.simba.squirrel.analyzer.impl.JenaStatementSink(m);
//	        //statesink.setBase(curi.getUri().toString());
//	        XMLReader parser = ParserFactory.createReaderForFormat(statesink, Format.XHTML, Setting.OnePointOne);
//	        //parser.setProperty(JAXPConstants.JAXP_SCHEMA_SOURCE, curi.getUri().toString());
//	        try {
//				parser.parse(data.getAbsolutePath());
//				String syntax = "N-TRIPLE"; //"N-TRIPLE" and "TURTLE"
//				StringWriter out = new StringWriter();
//				m.write(out, syntax,curi.getUri().toString());
//				String result = out.toString();
//				result = replaceBaseUri(result,curi.getUri().toString(),data.getPath());
//				sink.addData(curi, result);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return null;
		}	
	
	private String replaceBaseUri(String result,String base,String oldbase) {
		oldbase = "file:///"+oldbase.replace("\\", "/");
		oldbase = oldbase.substring(0, oldbase.lastIndexOf("/"));
		base = base.substring(0, base.lastIndexOf("/"));
		result = result.replace(oldbase, base);
		//System.out.println(result);
		//System.out.println(base);
		//System.out.println(oldbase);
		return result;
	}	
}