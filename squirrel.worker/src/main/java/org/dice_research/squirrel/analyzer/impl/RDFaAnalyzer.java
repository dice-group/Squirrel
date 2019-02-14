package org.dice_research.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.access.TcManager;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.tika.Tika;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.AbstractAnalyzer;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.semarglproject.clerezza.core.sink.ClerezzaSink;
import org.semarglproject.rdf.ParseException;
import org.semarglproject.rdf.rdfa.RdfaParser;
import org.semarglproject.source.StreamProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		MGraph model = createClerezzaModel(curi.getUri().toString());

		StreamProcessor streamProcessor = new StreamProcessor(RdfaParser.connect(ClerezzaSink.connect(model)));

		try {
			streamProcessor.process(tempFile, curi.getUri().toString());
		} catch (ParseException e) {
			LOGGER.debug(e.getMessage());
		}
		System.out.println("Model size = " + model.size());

		Iterator<Triple> tripleIterator = model.getGraph().iterator();
		List<org.apache.jena.graph.Triple> listTriples = new ArrayList<org.apache.jena.graph.Triple>();

		while (tripleIterator.hasNext()) {
			Triple t = tripleIterator.next();
			boolean isUri = true;
			URI uri = null;
			try {
				uri = new URI(t.getObject().toString().substring(1, t.getObject().toString().length() - 1));
			} catch (URISyntaxException e) {
				isUri = false;
			}

			Node s = NodeFactory
					.createURI(t.getSubject().toString().substring(1, t.getSubject().toString().length() - 1));
			Node p = NodeFactory.createURI(t.getPredicate().getUnicodeString());
			Node o = isUri ? NodeFactory.createURI(uri.toString())
					: NodeFactory.createLiteral(
							t.getObject().toString().substring(1, t.getObject().toString().length() - 1));

			org.apache.jena.graph.Triple triple = new org.apache.jena.graph.Triple(s, p, o);
			collector.addTriple(curi, triple);
			sink.addTriple(curi, triple);
			System.out.println(triple);
		}

		return collector.getUris(curi);
	}

	private MGraph createClerezzaModel(String uri) {
		TcManager manager = TcManager.getInstance();
		UriRef graphUri = new UriRef(uri);
		if (manager.listMGraphs().contains(graphUri)) {
			manager.deleteTripleCollection(graphUri);
		}
		return manager.createMGraph(graphUri);
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
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("An error was found when verify eligibility", e);
		}
		return false;
	}

}
