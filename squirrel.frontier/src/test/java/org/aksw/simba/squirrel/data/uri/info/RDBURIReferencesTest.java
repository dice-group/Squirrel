package org.aksw.simba.squirrel.data.uri.info;

import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.RethinkDBBasedTest;
import org.aksw.simba.squirrel.RethinkDBMockTest;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Simulates a RethinkDB ({@link RethinkDBBasedTest}), that is filled with some crawled URIs with found URIs.
 * See RDBURIReferences
 *
 * @author Philipp Heinisch
 */
public class RDBURIReferencesTest extends RethinkDBBasedTest {

    private RDBURIReferences rdburiReferences;

    //DUMMY DATA
    private final CrawleableUriFactoryImpl factory = new CrawleableUriFactoryImpl();
    private final CrawleableUri mainURI1 = factory.create("https://www.philippheinisch.de/");
    private final CrawleableUri mainURI2 = factory.create("https://www.philippheinisch.de/aboutMe.html");
    private final List<CrawleableUri> foundURI1 = Collections.singletonList(factory.create("https://www.philippheinisch.de/projects.html"));
    private final List<CrawleableUri> foundURI2 = Collections.singletonList(factory.create("https://www.philippheinisch.de/multi/index.php"));

    @Before
    public void setUp() {
        rdburiReferences = new RDBURIReferences(RethinkDBMockTest.DB_HOST_NAME, RethinkDBMockTest.DB_PORT, URIShortcutMode.TOTAL_URI);

        //run it
        rdburiReferences.open();

        rdburiReferences.add(mainURI1, foundURI1);
        rdburiReferences.add(mainURI2, foundURI2);
        rdburiReferences.add(mainURI1, foundURI2);
    }

    @After
    public void tearDown() {
        rdburiReferences.close();
    }

    @Test
    @SuppressWarnings("all")
    public void add() {
        Cursor cursor = r.db(RDBURIReferences.DATABASE_NAME).table(RDBURIReferences.TABLE_NAME).run(connection);

        while (cursor.hasNext()) {
            HashMap row = (HashMap) cursor.next();
            List<String> listInDB = ((ArrayList<String>) row.get(RDBURIReferences.COLUMN_FOUNDURIS));
            if (row.get(RDBURIReferences.COLUMN_URI).equals(mainURI1))
                assertEquals("Prove for the first index (" + row.get(RDBURIReferences.COLUMN_URI) + ") the founded URI list. Should have a size of 2 because of the update. Current list: " + listInDB.toArray(), 2, listInDB.size());
            else if (row.get(RDBURIReferences.COLUMN_URI).equals(mainURI2))
                assertEquals("Prove for the second index (" + row.get(RDBURIReferences.COLUMN_URI) + ") the founded URI list. Should have a size of 1 because of no update. Current list: " + listInDB.toArray(), 1, listInDB.size());
        }
    }

    @Test
    public void walkThroughCrawledGraph() {
        assertTrue("Get iterator for crawled graph. Should contain only one empty entry because \"onlyCrawledURIs\" is true", rdburiReferences.walkThroughCrawledGraph(1, true, true).next().getValue().isEmpty());
        assertTrue("Get iterator for crawled graph. Should be not empty because \"onlyCrawledURIs\" is false", rdburiReferences.walkThroughCrawledGraph(1, true, false).hasNext());
        assertFalse("Get iterator for crawled graph. Should be empty because \"offset\" is 2 (skip 2 from the beginning) and we have only 2 entries", rdburiReferences.walkThroughCrawledGraph(2, false, false).hasNext());
    }
}
