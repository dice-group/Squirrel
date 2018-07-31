package org.aksw.simba.squirrel;

import org.junit.Test;

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
