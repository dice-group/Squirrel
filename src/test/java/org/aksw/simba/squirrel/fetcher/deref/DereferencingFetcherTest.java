package org.aksw.simba.squirrel.fetcher.deref;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.fetcher.FetcherTest;
import org.junit.Test;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class DereferencingFetcherTest extends FetcherTest {

    @Test
    public void testDeferenceableFetcher() throws UnknownHostException, URISyntaxException {
        CrawleableUri crawleableUri = new CrawleableUri(new URI("https://tinyurl.com/aksworg-ttl"),
                InetAddress.getByAddress(new byte[] { 104, 20, 87, 65 }), UriType.DEREFERENCEABLE);
        this.run(crawleableUri);
    }
}


