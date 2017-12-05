package org.aksw.simba.squirrel.fetcher.ftp;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

public class FTPFetcherTest {
    @Test
    public void shouldNotFetchHTTPLinks() throws Exception {
        CrawleableUri danbriUri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
        FTPFetcher fetcher = new FTPFetcher();
        File data = fetcher.fetch(danbriUri);
        assertNull(data);
    }

}
