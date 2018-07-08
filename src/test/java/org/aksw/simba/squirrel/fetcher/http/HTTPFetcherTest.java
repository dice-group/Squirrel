package org.aksw.simba.squirrel.fetcher.http;


import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertNotNull;

public class HTTPFetcherTest {


    @Test
    public void fetch() throws Exception{
        CrawleableUri tempUri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
        HTTPFetcher fetcher = new HTTPFetcher();
        File data = fetcher.fetch(tempUri);
        assertNotNull(data);

    }

}
