package org.dice_research.squirrel.fetcher.manage;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.dice_research.squirrel.fetcher.ftp.FTPFetcher;
import org.dice_research.squirrel.fetcher.http.HTTPFetcher;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.*;

public class SimpleOrderedFetcherManagerTest {

    @Test
    public void testCase1() throws Exception {
        CrawleableUri uri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
        FetcherDummy4Tests dummyTest1 = new FetcherDummy4Tests(true);
        FetcherDummy4Tests dummyTest2 = new FetcherDummy4Tests(true);
        SimpleOrderedFetcherManager manager = new SimpleOrderedFetcherManager(dummyTest1, dummyTest2);
        manager.setFetchers(dummyTest1, dummyTest2);
        File resultFile = manager.fetch(uri);
        assertEquals(true,dummyTest1.isPostFlag()); // 1st fetcher has been called
        assertNotNull(dummyTest1.getResult()); // checks that the result of first fetcher is not null i.e. result!=null
        assertEquals(false, dummyTest2.isPostFlag());// checks that the 2nd fetcher has not been called as the result of first fetcher is not null
    }
    @Test
    public void testCase2() throws Exception {
        CrawleableUri uri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
        FetcherDummy4Tests dummyTest1 = new FetcherDummy4Tests(false);
        FetcherDummy4Tests dummyTest2 = new FetcherDummy4Tests(true);
        SimpleOrderedFetcherManager manager = new SimpleOrderedFetcherManager(dummyTest1, dummyTest2);
        manager.setFetchers(dummyTest1, dummyTest2);
        File resultFile = manager.fetch(uri);
        assertEquals(true,dummyTest1.isPostFlag()); // 1st fetcher has been called
        assertNull(dummyTest1.getResult()); // optional, to make sure that the result is null
        assertEquals(true, dummyTest2.isPostFlag()); // 2nd fetcher has been called
    }
    @Test
    public void testCase3() throws Exception {
        CrawleableUri uri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
        FetcherDummy4Tests dummyTest1 = new FetcherDummy4Tests(false);
        FetcherDummy4Tests dummyTest2 = new FetcherDummy4Tests(false);
        SimpleOrderedFetcherManager manager = new SimpleOrderedFetcherManager(dummyTest1, dummyTest2);
        manager.setFetchers(dummyTest1, dummyTest2);
        File resultFile = manager.fetch(uri);
        assertEquals(true,dummyTest1.isPostFlag()); // 1st fetcher has been called
        assertNull(dummyTest1.getResult()); //optional
        assertEquals(true, dummyTest2.isPostFlag()); //2nd fetcher has been called
    }


}