package org.aksw.simba.squirrel.fetcher.dump;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.fetcher.FetcherTest;
import org.junit.Test;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class DumpFetcherTest extends FetcherTest {

    @Test
    public void testDumpFetcherNotArchived() throws UnknownHostException, URISyntaxException {
        byte[] ipAddr = new byte[]{ (byte) 207, (byte) 241, (byte) 225, (byte) 202 };
        CrawleableUri crawleableUri = new CrawleableUri(new URI("http://openlibrary.org/books/OL6807502M.rdf"),
                InetAddress.getByAddress(ipAddr), UriType.DUMP);
        this.run(crawleableUri);
    }

    @Test
    public void testDumpFetcherArchivedTtl() throws UnknownHostException, URISyntaxException {
        //archived ttl fails
        byte[] ipAddr = new byte[]{(byte) 207, (byte) 241, (byte) 225, (byte) 202};
        CrawleableUri crawleableUri = new CrawleableUri(new URI("http://www.ontosearch.com/dumps/dump.ttl.zip"),
                InetAddress.getByAddress(ipAddr), UriType.DUMP);

        this.run(crawleableUri);
    }

    @Test
    public void testDumpFetcherArchivedRdfNt() throws UnknownHostException, URISyntaxException {
        //This one is RDF which is NT actually, fails
        byte[] ipAddr = new byte[]{ (byte) 80, (byte) 64, (byte) 114, (byte) 207 };
        CrawleableUri crawleableUri = new CrawleableUri(new URI("http://dati.camera.it/ocd/dump/assemblea-17.rdf.zip"),
                InetAddress.getByAddress(ipAddr), UriType.DUMP);

        this.run(crawleableUri);
    }

    @Test
    public void testDumpFetcherArchivedRdf() throws UnknownHostException, URISyntaxException {
        byte[] ipAddr = new byte[]{ (byte) 134, (byte) 245, (byte) 12, (byte) 27 };
        CrawleableUri crawleableUri = new CrawleableUri(new URI("http://zbw.eu/beta/external_identifiers/jel/download/jel.rdf.zip"),
                InetAddress.getByAddress(ipAddr), UriType.DUMP);

        this.run(crawleableUri);
    }
}
