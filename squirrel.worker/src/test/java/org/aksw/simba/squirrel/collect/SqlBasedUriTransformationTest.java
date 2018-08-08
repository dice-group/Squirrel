package org.aksw.simba.squirrel.collect;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import org.aksw.simba.squirrel.utils.TempFileHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests
 * {@link SqlBasedUriCollector#getTableName(org.aksw.simba.squirrel.data.uri.CrawleableUri)}.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@RunWith(Parameterized.class)
public class SqlBasedUriTransformationTest {

    /**
     * A static connection to a temporary database that is used to check the
     * correctness of the generate table names.
     */
    private static Connection dbConnection;

    @BeforeClass
    public static void createDb() throws SQLException, IOException {
        File dbdir = TempFileHelper.getTempDir("dbTest", "");
        System.out.println("using " + dbdir);
        dbConnection = DriverManager.getConnection("jdbc:hsqldb:" + dbdir.getAbsolutePath() + File.separator + "test",
                "SA", "");
    }

    @AfterClass
    public static void closeDb() throws SQLException {
        dbConnection.close();
    }

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(new Object[][] { { "" }, { "http://example.org/example" } });
    }

    private String uri;

    public SqlBasedUriTransformationTest(String uri) {
        this.uri = uri;
    }

    @Test
    public void test() throws Exception {
        String tableName = SqlBasedUriCollector.generateTableName(uri);
        try {
            dbConnection.createStatement().executeUpdate("CREATE TABLE " + tableName + " (id int primary key)");
        } catch (Exception e) {
            throw new Exception("Got an exception when trying to create table with name \"" + tableName
                    + "\" generated based on URI \"" + uri + "\".", e);
        }
    }
}
