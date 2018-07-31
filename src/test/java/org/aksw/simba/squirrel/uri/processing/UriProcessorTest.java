package org.aksw.simba.squirrel.uri.processing;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;

import junit.framework.TestCase;

/**
 * @author Ivan Ermilov (iermilov@informatik.uni-leipzig.de)
 */
public class UriProcessorTest extends TestCase {
	protected UriProcessor uriProcessor;

	public void setUp() {
		uriProcessor = new UriProcessor();
	}

	/**
	 * Uri with .rdf extension should be recognized as UriType.DUMP
	 * @throws URISyntaxException
	 */
	public void testRecognizeUriTypeRdfDump() throws URISyntaxException {
	    URI uriToCrawl = new URI("http://xmlns.com/foaf/spec/index.rdf");
		CrawleableUri crawleableUri = new CrawleableUri(uriToCrawl);

		crawleableUri = uriProcessor.recognizeUriType(crawleableUri);
		assertTrue(crawleableUri.getType() == UriType.DUMP);
	}

	/**
	 * Uri with .n3 extension should be recognized as UriType.DUMP
	 * @throws URISyntaxException
	 */
	public void testRecognizeUriTypeRdfDumpN3() throws URISyntaxException {
	    URI uriToCrawl = new URI("http://quebec.bio2rdf.org/download/data/chebi/chebi.n3.gz");
	 	CrawleableUri crawleableUri = new CrawleableUri(uriToCrawl);

		crawleableUri = uriProcessor.recognizeUriType(crawleableUri);
		assertTrue(crawleableUri.getType() == UriType.DUMP);
	}

	/**
	 * Uri with .nt extension should be recognized as UriType.DUMP
	 * @throws URISyntaxException
	 */
	public void testRecognizeUriTypeRdfDumpNt() throws URISyntaxException {
	    URI uriToCrawl = new URI("http://spcdata.digitpa.gov.it/data/ipa.nt");
	 	CrawleableUri crawleableUri = new CrawleableUri(uriToCrawl);

		crawleableUri = uriProcessor.recognizeUriType(crawleableUri);
		assertTrue(crawleableUri.getType() == UriType.DUMP);
	}

	/**
	 * Uri with .tar extension should be recognized as UriType.DUMP
	 * @throws URISyntaxException
	 */
	public void testRecognizeUriTypeRdfDumpTar() throws URISyntaxException {
	    URI uriToCrawl = new URI("http://aseg.cs.concordia.ca/secold/download/static/secold_v_001.tar");
	    // http://aseg.cs.concordia.ca/secold/download/static/secold_v_001.tar
	    // http://aemet.linkeddata.es/source/rdf/data.zip
	 	CrawleableUri crawleableUri = new CrawleableUri(uriToCrawl);

		crawleableUri = uriProcessor.recognizeUriType(crawleableUri);
		assertTrue(crawleableUri.getType() == UriType.DUMP);
	}

	/**
	 * Uri with .zip extension should be recognized as UriType.DUMP
	 * @throws URISyntaxException
	 */
	public void testRecognizeUriTypeRdfDumpZip() throws URISyntaxException {
	    URI uriToCrawl = new URI("http://aemet.linkeddata.es/source/rdf/data.zip");
	 	CrawleableUri crawleableUri = new CrawleableUri(uriToCrawl);

		crawleableUri = uriProcessor.recognizeUriType(crawleableUri);
		assertTrue(crawleableUri.getType() == UriType.DUMP);
	}

	/**
	 * Uri with sparql in the path should be recognized as UriType.SPARQL
	 * @throws URISyntaxException
	 */
	public void testRecognizeUriTypeSparql() throws URISyntaxException {
	    URI uriToCrawl = new URI("http://gendr.bio2rdf.org/sparql");
	 	CrawleableUri crawleableUri = new CrawleableUri(uriToCrawl);

		crawleableUri = uriProcessor.recognizeUriType(crawleableUri);
		assertTrue(crawleableUri.getType() == UriType.SPARQL);
	}

	/**
	 * Uri with /page/ in the path should be recognized as UriType.DEREFERENCEABLE
	 * /page/ in URI signifies that it is hosted using pubby
	 * @throws URISyntaxException
	 */
	public void testRecognizeUriTypeDereferenceablePubbyPage() throws URISyntaxException {
	    URI uriToCrawl = new URI("http://dbpedia.org/page/Berlin");
	 	CrawleableUri crawleableUri = new CrawleableUri(uriToCrawl);

		crawleableUri = uriProcessor.recognizeUriType(crawleableUri);
		assertTrue(crawleableUri.getType() == UriType.DEREFERENCEABLE);
	}

	/**
	 * Uri with /resource/ in the path should be recognized as UriType.DEREFERENCEABLE
	 * /resource/ in URI signifies that it is hosted using pubby
	 * @throws URISyntaxException
	 */
	public void testRecognizeUriTypeDereferenceablePubbyResource() throws URISyntaxException {
	    URI uriToCrawl = new URI("http://dbpedia.org/resource/Berlin");
	 	CrawleableUri crawleableUri = new CrawleableUri(uriToCrawl);

		crawleableUri = uriProcessor.recognizeUriType(crawleableUri);
		assertTrue(crawleableUri.getType() == UriType.DEREFERENCEABLE);
	}

	/**
	 * Should return IP address of the URI
	 * @throws URISyntaxException
	 */
	public void testIpAddress() throws URISyntaxException {
	    URI uriToCrawl = new URI("http://xmlns.com/foaf/spec/index.rdf");
		CrawleableUri crawleableUri = new CrawleableUri(uriToCrawl);

		try {
			crawleableUri = uriProcessor.recognizeInetAddress(crawleableUri);
			assertTrue(crawleableUri.getIpAddress().getHostAddress().toString().matches("75.101.157.128"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Should be able to handle a bunch of random data input
	 */
	public void testNull() throws URISyntaxException {
		URI uriToCrawl = null;
		CrawleableUri crawleableUri = new CrawleableUri(uriToCrawl);

		try {
			crawleableUri = uriProcessor.recognizeInetAddress(crawleableUri);
			assertTrue(crawleableUri.getUri().toString().matches(""));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
