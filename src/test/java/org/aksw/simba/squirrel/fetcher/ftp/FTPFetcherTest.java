package org.aksw.simba.squirrel.fetcher.ftp;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.aksw.simba.squirrel.uri.processing.UriProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.*;

public class FTPFetcherTest {

	private UriProcessor uriProcessor;
	private CrawleableUri uri;

	@Before
	public void prepare() throws UnknownHostException, URISyntaxException {

		uriProcessor = new UriProcessor();
		// TODO: this test takes ages
		// TODO: Use mock FTP server here (docker or Java mock server)
//		uri = uriProcessor.recognizeInetAddress(new CrawleableUri(new URI("ftp://ftp.ncbi.nlm.nih.gov/pubchem/RDF")));
//		uri = uriProcessor.recognizeInetAddress(new CrawleableUri(new URI("ftp://ftp.ncbi.nlm.nih.gov/pubchem/RDF/void.ttl")));
	}


    @Test
    public void shouldNotFetchHTTPLinks() throws Exception {
        FTPFetcher fetcher = new FTPFetcher();
        File data = fetcher.fetch(uri);
        fetcher.close();
        assertNull(data);
    }

}
