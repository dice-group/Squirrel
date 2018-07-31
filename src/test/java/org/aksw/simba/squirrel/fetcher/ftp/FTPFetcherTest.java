package org.aksw.simba.squirrel.fetcher.ftp;

import static org.junit.Assert.assertNull;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.junit.Before;
import org.junit.Test;

public class FTPFetcherTest {

//	private UriProcessor uriProcessor;
//	private CrawleableUri uri;

	@Before
	public void prepare() throws UnknownHostException, URISyntaxException {

//		uriProcessor = new UriProcessor();
		// TODO: this test takes ages
		// TODO: Use mock FTP server here (docker or Java mock server)
//		uri = uriProcessor.recognizeInetAddress(new CrawleableUri(new URI("ftp://ftp.ncbi.nlm.nih.gov/pubchem/RDF")));
//		uri = uriProcessor.recognizeInetAddress(new CrawleableUri(new URI("ftp://ftp.ncbi.nlm.nih.gov/pubchem/RDF/void.ttl")));
	}


    @Test
    public void shouldNotFetchHTTPLinks() throws Exception {
        FTPFetcher fetcher = new FTPFetcher();
        File data = fetcher.fetch(new CrawleableUri(new URI("http://dbpedia.org/resource/New_York")));
        fetcher.close();
        assertNull(data);
    }

}