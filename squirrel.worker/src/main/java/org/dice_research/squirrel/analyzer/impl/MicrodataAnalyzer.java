package org.dice_research.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.any23.Any23;
import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.configuration.ModifiableConfiguration;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
import org.apache.any23.writer.TripleHandler;
import org.apache.tika.Tika;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.AbstractAnalyzer;
import org.dice_research.squirrel.analyzer.commons.SquirrelTripleHandler;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Any23 Microdata Extractor
 * 
 * @author Geraldo de Souza Junior
 *
 */

public class MicrodataAnalyzer extends AbstractAnalyzer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MicrodataAnalyzer.class);

	public MicrodataAnalyzer(UriCollector collector) {
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

			ModifiableConfiguration modifiableConf = DefaultConfiguration.copy();
			modifiableConf.setProperty("any23.microdata.ns.default", "http://schema.org/");
//			Any23 runner = new Any23(modifiableConf, "html-rdfa11");
			Any23 runner = new Any23("html-microdata");

			runner.setHTTPUserAgent(Any23.DEFAULT_HTTP_CLIENT_USER_AGENT);
			DocumentSource source = new FileDocumentSource(tempFile, curi.getUri().toString());
			TripleHandler handler = new SquirrelTripleHandler(curi, collector, sink);
			runner.extract(source, handler);
			handler.close();

		} catch (Exception e) {
			LOGGER.warn("Could not analyze file for URI: " + curi.getUri().toString() + " :: Analyzer: "
					+ this.getClass().getName());
		}
		tempFile.delete();
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

}
