package org.dice_research.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import org.apache.tika.Tika;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.AbstractAnalyzer;
import org.dice_research.squirrel.analyzer.commons.SquirrelClerezzaSink;
import org.dice_research.squirrel.collect.SimpleUriCollector;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.sink.impl.mem.InMemorySink;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.semarglproject.rdf.rdfa.RdfaParser;
import org.semarglproject.source.StreamProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Analyzer to extract RDFa format triples
 * 
 * @author Geraldo de Souza Junior
 *
 */

public class RDFaAnalyzer extends AbstractAnalyzer {

	private static final Logger LOGGER = LoggerFactory.getLogger(RDFaAnalyzer.class);

	public RDFaAnalyzer(UriCollector collector) {
		super(collector);
	}

	@Override
	public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {

		HtmlCleaner cleaner = new HtmlCleaner();

		CleanerProperties props = new CleanerProperties();

		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		File tempFile = null;
		try {
			TagNode tagNode = cleaner.clean(data);

			tempFile = File.createTempFile("file_", ".html");
			FileWriter writer = new FileWriter(tempFile);

			new PrettyXmlSerializer(props).write(tagNode, writer, "utf-8");

			StreamProcessor streamProcessor = new StreamProcessor(RdfaParser.connect(SquirrelClerezzaSink.connect(curi,collector,sink)));
			streamProcessor.process(tempFile, curi.getUri().toString());

			tempFile.delete();

		} catch (Exception e) {
			LOGGER.error("Could not analyze file for URI: " + curi.getUri().toString() + " :: Analyzer: "
					+ this.getClass().getName(),e);

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
			if ("text/html".equals(mimeType)) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("An error was found when verify eligibility", e);
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		Sink sink = new InMemorySink();
		CrawleableUri curi = new CrawleableUri(new URI("http://www.dice-research.org/rdfaTest"));
		curi.addData(Constants.URI_HTTP_MIME_TYPE_KEY, "text/html");
		File file = new File("/home/gsjunior/Documentos/rdfa.html");
		
		UriCollector collector = new SimpleUriCollector(new GzipJavaUriSerializer());
		
		new RDFaAnalyzer(collector).analyze(curi, file, sink);
		
		
	}

}
