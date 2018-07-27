package org.aksw.simba.squirrel.analyzer.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
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
import net.rootdev.javardfa.Setting;
import net.rootdev.javardfa.StatementSink;

public class JavaRDFaParser implements Analyzer {

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
//		Model model = ModelFactory.createDefaultModel();
//		try {
//			String hf = "0001.html";
//			Model m = ModelFactory.createDefaultModel();
//	        StatementSink statesink = new StatementSink(m);
//	        XMLReader parser = ParserFactory.createReaderForFormat(statesink, Format.XHTML, Setting.OnePointOne);
//	        try {
//				parser.parse(hf);
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
//	Try something like this:
//		String syntax = "RDF/XML-ABBREV"; // also try "N-TRIPLE" and "TURTLE"
//		StringWriter out = new StringWriter();
//		model.write(out, syntax);
//		String result = out.toString();
//		This uses Jena's built-in writers that can already output RDF graphs in the various supported RDF syntaxes, such as RDF/XML and Turtle and N-Triples. If you just want to dump to System.out, then it's even easier:
//		model.write(System.out, "RDF/XML-ABBREV");
	
}