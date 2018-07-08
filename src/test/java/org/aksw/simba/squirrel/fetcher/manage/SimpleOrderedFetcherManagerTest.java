package org.aksw.simba.squirrel.fetcher.manage;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertNull;

public class SimpleOrderedFetcherManagerTest {

    @Test
    public void fetch() throws Exception{
        CrawleableUri tempUri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
        SimpleOrderedFetcherManager fetcher = new SimpleOrderedFetcherManager();
        File data = fetcher.fetch(tempUri);
        assertNull(data);


    }


}
