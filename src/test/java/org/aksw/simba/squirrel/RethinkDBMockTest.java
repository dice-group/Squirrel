package org.aksw.simba.squirrel;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class RethinkDBMockTest extends RethinkDBBasedTest {

    @Test
    public void test() {
        r.dbCreate("testDb").run(connection);
        List<String> dbList = r.dbList().run(connection);
        assertTrue(dbList.contains("testDb"));
        r.dbDrop("testDb");
    }
}
