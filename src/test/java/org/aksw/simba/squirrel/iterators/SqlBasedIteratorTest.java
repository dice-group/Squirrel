package org.aksw.simba.squirrel.iterators;

    import java.io.File;
    import java.io.IOException;
    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.PreparedStatement;
    import java.sql.SQLException;
    import java.util.regex.Pattern;

    import org.aksw.simba.squirrel.utils.TempFileHelper;
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



    @Test
    public void test() throws SQLException {
        PreparedStatement stmt;
        String uri = "http://example.org/example";
        String tableName = generateTableName(uri);
        dbConnection.createStatement().executeUpdate(CREATE_TABLE_QUERY.replaceAll("\\?", tableName));
        dbConnection.commit();
        try {
            stmt = dbConnection.prepareStatement("select * from "+ tableName +" OFFSET ? FETCH NEXT ? ROWS ONLY");
            stmt.setInt(1,0);//1 specifies the first parameter in the query
            stmt.setInt(2,3);
            SqlBasedIterator obj = new SqlBasedIterator(stmt);
            boolean has_Next =  obj.hasNext();
            Assert.assertFalse(has_Next);
        } catch (SQLException e) {
            LOGGER.error("Exception while selecting the values from table");
        }

    }


    private static String generateTableName(String uri) {
        final Pattern TABLE_NAME_GENERATE_REGEX = Pattern.compile("[^0-9a-zA-Z]*");
        String[] parts = TABLE_NAME_GENERATE_REGEX.split(uri);
        int pos = 0;
        StringBuilder builder = new StringBuilder();
        while ((pos < parts.length) && (builder.length() < 30)) {
            if ((builder.length() == 0) && (parts[pos].length() > 0) && Character.isDigit(parts[pos].charAt(0))) {
                builder.append('A');
            }
            builder.append(parts[pos]);
            ++pos;
        }
        if (builder.length() == 0) {
            builder.append('A');
        }

        if (builder.length() > 30) {
            builder.delete(30, builder.length());
        }
        int hashCode = uri.hashCode();
        builder.append(hashCode < 0 ? -hashCode : hashCode);
        return builder.toString();
    }
}
