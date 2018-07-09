package org.aksw.simba.squirrel.data.uri.info;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.RethinkDBMockTest;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class RDBURIReferencesTest {

    private RethinkDB r;
    private RDBConnector connector;
    private RDBURIReferences rdburiReferences;

    //DUMMY DATA
    CrawleableUriFactoryImpl factory = new CrawleableUriFactoryImpl();
    private final CrawleableUri mainURI1 = factory.create("https://www.philippheinisch.de/");
    private final CrawleableUri mainURI2 = factory.create("https://www.philippheinisch.de/aboutMe.html");
    private final List<CrawleableUri> foundURI1 = Collections.singletonList(factory.create("https://www.philippheinisch.de/projects.html"));
    private final List<CrawleableUri> foundURI2 = Collections.singletonList(factory.create("https://www.philippheinisch.de/multi/index.php"));

    /**
     * For functionality regarding the starting of rethinkdb container
     */
    private RethinkDBMockTest rethinkDBMockTest;

    @Before
    public void setUp() throws Exception {
        r = RethinkDB.r;
        connector = new RDBConnector(RethinkDBMockTest.DB_HOST_NAME, RethinkDBMockTest.DB_PORT);
        rdburiReferences = new RDBURIReferences(RethinkDBMockTest.DB_HOST_NAME, RethinkDBMockTest.DB_PORT, URIShortcutMode.TOTAL_URI);

        // to get rethinkdb container running
        rethinkDBMockTest = new RethinkDBMockTest();
        rethinkDBMockTest.setUpRDB();

        //run it
        rdburiReferences.open();
    }

    @After
    public void tearDown() throws Exception {
        rdburiReferences.close();
        rethinkDBMockTest.tearDownRDB();
    }

    @Test
    public void add() {
        rdburiReferences.add(mainURI1, foundURI1);
        rdburiReferences.add(mainURI2, foundURI2);
        rdburiReferences.add(mainURI1, foundURI2);

        Cursor cursor = r.db(RDBURIReferences.DATABASE_NAME).table(RDBURIReferences.TABLE_NAME).orderBy().optArg("index", r.asc("id")).run(connector.connection);

        HashMap row = (HashMap) cursor.next();
        List<String> listInDB = ((ArrayList<String>) row.get(RDBURIReferences.COLUMN_FOUNDURIS));
        assertEquals("Prove for the first index (" + mainURI1.getUri() + ") the founded URI list. Should have a size of 2 because of the update. Current list: " + listInDB.toArray(), 2, listInDB.size());
        row = (HashMap) cursor.next();
        listInDB = ((ArrayList<String>) row.get(RDBURIReferences.COLUMN_FOUNDURIS));
        assertEquals("Prove for the second index (" + mainURI1.getUri() + ") the founded URI list. Should have a size of 1 because of no update. Current list: " + listInDB.toArray(), 1, listInDB.size());
    }

    @Test
    public void walkThroughCrawledGraph() {
        assertFalse("Get iterator for crawled graph. Should be empty because \"onlyCrawledURIs\" is true", rdburiReferences.walkThroughCrawledGraph(1, true, true).hasNext());
        assertTrue("Get iterator for crawled graph. Should be not empty because \"onlyCrawledURIs\" is true", rdburiReferences.walkThroughCrawledGraph(1, true, false).hasNext());
        assertFalse("Get iterator for crawled graph. Should be not empty because \"offset\" is 2 and we have only 2 entries", rdburiReferences.walkThroughCrawledGraph(2, true, false).hasNext());
    }
}
