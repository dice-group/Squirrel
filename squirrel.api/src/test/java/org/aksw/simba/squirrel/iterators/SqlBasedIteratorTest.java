package org.aksw.simba.squirrel.iterators;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.sql.ResultSet;

import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.utils.TempFileHelper;
import org.dice_research.squirrel.iterators.SqlBasedIterator;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlBasedIteratorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlBasedIteratorTest.class);
    private static Connection dbConnection;
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE ? (uri VARCHAR(255), serial INT, data BLOB, PRIMARY KEY(uri,serial));";
    private static final String tableName = "A0" ;
    PreparedStatement stmt;
    String uris = "http://example.org/example";

    @BeforeClass
    public static void createDb() throws SQLException, IOException {
        File dbdir = TempFileHelper.getTempDir("dbTest", "");
        System.out.println("using " + dbdir);
        dbConnection = DriverManager.getConnection("jdbc:hsqldb:" + dbdir.getAbsolutePath() + File.separator + "test",
            "SA", "");
        dbConnection.createStatement().executeUpdate(CREATE_TABLE_QUERY.replaceAll("\\?", tableName));
        dbConnection.commit();
    }


    @Test
    public void test() throws SQLException {
        //test to check SqlBasedIterator when the table has no values;
        try {
            stmt = dbConnection.prepareStatement("select * from  A0 OFFSET ? FETCH NEXT ? ROWS ONLY");
            stmt.setInt(1,0);//1 specifies the first parameter in the query
            stmt.setInt(2,3);
            SqlBasedIterator obj = new SqlBasedIterator(stmt);
            boolean has_Next =  obj.hasNext();
            Assert.assertFalse(has_Next);
        } catch (SQLException e) {
            LOGGER.error("Exception while selecting the values from table");
        }

    }


    @Test
    public void testIterate() throws SQLException, IOException {
        GzipJavaUriSerializer serializer = new GzipJavaUriSerializer();
        PreparedStatement insertStmt = dbConnection.prepareStatement("INSERT INTO A0(uri,serial,data) values(?,?,?)");
        for (int i = 0; i < 10; i++){
            String uri = uris + i;
            insertStmt.setString(1, uri);
            insertStmt.setInt(2, uri.hashCode());
            byte[] blob = serializer.serialize(uri);
            insertStmt.setBytes(3, blob);
            insertStmt.execute();
        }
        try {
            stmt = dbConnection.prepareStatement("select * from A0 OFFSET ? FETCH NEXT ? ROWS ONLY");
            stmt.setInt(1,0);//1 specifies the first parameter in the query
            stmt.setInt(2,3);
            SqlBasedIterator obj = new SqlBasedIterator(stmt);
            byte[] has_Next =  obj.next();
            System.out.println(has_Next);
            ResultSet rs = stmt.executeQuery();
            byte[] expected = rs.getBytes(3);
            Assert.assertArrayEquals(expected , has_Next);
        } catch (SQLException e) {
            LOGGER.error("Error while iterating through the values in table");
        }

    }

    @AfterClass
    public static void closeDb() throws SQLException {
        dbConnection.close();
    }

}
