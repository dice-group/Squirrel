package org.aksw.simba.squirrel.collect;


import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.sql.DataSource;


import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.iterators.SqlBasedIterator;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * An implementation of the {@link UriCollector} interface that is backed by a
 * SQL database.
 * 
 * @author Geralod Souza Junior (gsjunior@mail.uni-paderborn.de)
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 * @NotThreadSafe because the prepared statement objects used internally are not
 *                stateless.
 */
@NotThreadSafe
public class SqlBasedUriCollector implements UriCollector, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlBasedUriCollector.class);

    protected static final String COUNT_URIS_QUERY = "SELECT COUNT(*) AS TOTAL FROM ?";

//    protected static final String COUNT_URIS_QUERY = "SELECT COUNT(*) AS TOTAL FROM ? where uri = ?";

    protected static final String CREATE_TABLE_QUERY = "CREATE TABLE ? (uri VARCHAR(1024), serial INT, data BLOB, PRIMARY KEY(uri,serial));";
    protected static final String DROP_TABLE_QUERY = "DROP TABLE ";
    protected static final String INSERT_URI_QUERY_PART_1 = " INSERT INTO ";
    protected static final String INSERT_URI_QUERY_PART_2 = "(uri,serial,data) VALUES(?,?,?)";
    // protected static final String CLEAR_TABLE_QUERY = "DELETE FROM uris";
    private static final String SELECT_TABLE_QUERY = "SELECT * FROM ? OFFSET ? FETCH NEXT ? ROWS ONLY ";
    private static final String TABLE_NAME_KEY = "URI_COLLECTOR_TABLE_NAME";
    private static final int MAX_ALPHANUM_PART_OF_TABLE_NAME = 30;
    private static final int DEFAULT_BUFFER_SIZE = 30;
    private static final Pattern TABLE_NAME_GENERATE_REGEX = Pattern.compile("[^0-9a-zA-Z]*");
    private long total_uris = 0;
    
    

    public static SqlBasedUriCollector create(Serializer serializer) {
        return create(serializer, "foundUris");
    }

    public static SqlBasedUriCollector create(Serializer serializer, String dbPath) {
        SqlBasedUriCollector collector = null;
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(System.out);
        }
        Statement s = null;
        try {
            Connection dbConnection = DriverManager.getConnection("jdbc:hsqldb:mem:" + dbPath, "SA", "");
            // PreparedStatement createTableStmt =
            // dbConnection.prepareStatement(CREATE_TABLE_QUERY);
            // PreparedStatement dropTableStmt =
            // dbConnection.prepareStatement(DROP_TABLE_QUERY);
            // PreparedStatement insertStmt =
            // dbConnection.prepareStatement(INSERT_URI_QUERY);
            collector = new SqlBasedUriCollector(dbConnection,
                    /* createTableStmt, dropTableStmt, insertStmt, */ serializer);
        } catch (Exception e) {
            LOGGER.error("Error while creating a local database for storing the extracted URIs. Returning null.", e);
        } finally {
            try {
                if (s != null) {
                    s.close();
                }
            } catch (SQLException e) {
            }
        }
        return collector;
    }

    protected Connection dbConnection;
    protected Serializer serializer;
    protected int bufferSize = DEFAULT_BUFFER_SIZE;
    protected Map<String, UriTableStatus> knownUris = new HashMap<>();

    public SqlBasedUriCollector(DataSource dataSource, Serializer serializer) throws SQLException {
        this.dbConnection = dataSource.getConnection();
        this.serializer = serializer;
    }
    
    public SqlBasedUriCollector(Connection dbConnection, Serializer serializer) throws SQLException {
        this.dbConnection = dbConnection;
        this.serializer = serializer;
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        String tableName = getTableName(uri);
        try {
            dbConnection.createStatement().executeUpdate(CREATE_TABLE_QUERY.replaceAll("\\?", tableName));
            dbConnection.commit();
            UriTableStatus table = UriTableStatus.create(tableName, dbConnection, bufferSize);
            // PreparedStatement ps = dbConnection.prepareStatement(CREATE_TABLE_QUERY);
            knownUris.put(uri.getUri().toString(), table);
        } catch (Exception e) {
            LOGGER.info("Couldn't create table for URI \"" + uri.getUri() + "\". ", e);
        }
    }

    @Override
    public Iterator<byte[]> getUris(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        if (knownUris.containsKey(uriString)) {
            UriTableStatus table = knownUris.get(uriString);
            synchronized (table) {
                try {
                    String tableName = table.getTableName();
                    // Make sure everything has been committed
                    table.commitPendingChanges();

                    PreparedStatement ps = dbConnection
                            .prepareStatement(SELECT_TABLE_QUERY.replaceFirst("\\?", tableName));
                    return new SqlBasedIterator(ps);

                } catch (SQLException e) {
                    LOGGER.error("Exception while querying URIs from database({}). Returning empty Iterator.",
                        e.getMessage());
                }
            }
        } else {
            LOGGER.error("Got an unknown URI \"{}\". Returning empty Iterator.", uri.getUri().toString());
        }
        return Collections.emptyIterator();
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        addUri(uri, triple.getSubject());
        addUri(uri, triple.getPredicate());
        addUri(uri, triple.getObject());
    }

    protected void addUri(CrawleableUri uri, Node node) {
        if (node.isURI()) {
            try {
                addNewUri(uri, new CrawleableUri(new URI(node.getURI())));
            } catch (URISyntaxException e) {
                LOGGER.error("Couldn't process extracted URI. It will be ignored.", e);
            }
        }
    }
    

    @Override
    public void addNewUri(CrawleableUri uri, CrawleableUri newUri) {
        String uriString = uri.getUri().toString();
        if (knownUris.containsKey(uriString)) {
            UriTableStatus table = knownUris.get(uriString);
            synchronized (table) {
                try {
                    table.addUri(newUri.getUri().toString(), serializer.serialize(newUri));
                } catch (IOException e) {
                    LOGGER.error("Couldn't serialize URI \"" + newUri.getUri() + "\". It will be ignored.", e);
                } catch (Exception e) {
                    LOGGER.error("Couldn't add URI \"" + newUri.getUri() + "\". It will be ignored.", e);
                }
            }
        } else {
            LOGGER.error("Got an unknown URI \"{}\". It will be ignored.", uri.getUri().toString());
        }
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        if (knownUris.containsKey(uriString)) {
            UriTableStatus table = knownUris.remove(uriString);
            synchronized (table) {
                try {
                    dbConnection.createStatement().executeUpdate(DROP_TABLE_QUERY + getTableName(uri));
                    dbConnection.commit();
                } catch (SQLException e) {
                    LOGGER.warn("Couldn't drop table of URI \"" + uri + "\". It will be ignored.", e);
                }
            }
        } else {
            LOGGER.info("Should close \"{}\" but it is not known. It will be ignored.", uri.getUri().toString());
        }
    }


    public long getSize() {
        return total_uris;
    }

    public long getSize(CrawleableUri uri) {
        long totalUris = 0;
        String uriString = uri.getUri().toString();
        if (knownUris.containsKey(uriString)) {
            UriTableStatus table = knownUris.get(uriString);
            synchronized (table) {
                try {
		                    String tableName = table.getTableName();
		                    // Make sure everything has been committed
		                    table.commitPendingChanges();
		    	PreparedStatement ps = dbConnection
		                .prepareStatement(COUNT_URIS_QUERY.replaceFirst("\\?", tableName));
		    	
//		    	ps.setString(1, uri.getUri().toString());
		    	ResultSet rs = ps.executeQuery();
		    	while(rs.next()) {
		    		totalUris = rs.getLong(1);	
		    	}
		    	
		    	ps.close();
		    	rs.close();
                }catch(Exception e) {
                	LOGGER.error("Could not compute size for uri:. ", uri.getUri().toString());
                }
            }
        }
    	
    	return totalUris;
    }

    @Override
    public void close() throws IOException {
        // It might be necessary to go through the list of known URIs and close all of
        // the remaining URIs
        try {
            dbConnection.close();
        } catch (SQLException e) {
        }
    }

    /**
     * Retrieves the URIs table name from its properties or generates a new table
     * name and adds it to the URI (using the {@value #TABLE_NAME_KEY} property).
     * 
     * @param uri
     *            the URI for which a table name is needed.
     * @return the table name of the URI
     */
    protected static String getTableName(CrawleableUri uri) {
        if (uri.getData().containsKey(TABLE_NAME_KEY)) {
            return (String) uri.getData().get(TABLE_NAME_KEY);
        } else {
            String tableName = generateTableName(uri.getUri().toString());
            uri.addData(TABLE_NAME_KEY, tableName);
            return tableName;
        }
    }

    /**
     * Generates a table name based on the given URI. Only alphanumeric characters
     * of the URI are kept. If the URI exceeds the length of
     * {@link #MAX_ALPHANUM_PART_OF_TABLE_NAME}={@value #MAX_ALPHANUM_PART_OF_TABLE_NAME}
     * the exceeding part is cut off. After that the hash value of the original URI
     * is appended.
     * 
     * @param uri
     *            the URI for which a table name has to be generated
     * @return the table name of the URI
     */
    protected static String generateTableName(String uri) {
        String[] parts = TABLE_NAME_GENERATE_REGEX.split(uri);
        int pos = 0;
        StringBuilder builder = new StringBuilder();
        // Collect the alphanumeric parts of the URI
        while ((pos < parts.length) && (builder.length() < MAX_ALPHANUM_PART_OF_TABLE_NAME)) {
            // If the first character that would be added to the builder is a digit
            if ((builder.length() == 0) && (parts[pos].length() > 0) && Character.isDigit(parts[pos].charAt(0))) {
                // add a character in front of it
                builder.append('A');
            }
            builder.append(parts[pos]);
            ++pos;
        }
        // If the given String did not contain any useful characters, add at least a
        // single character before adding the hash
        if (builder.length() == 0) {
            builder.append('A');
        }

        // If we exceeded the maximum length of the alphanumeric part, delete the last
        // characters
        if (builder.length() > MAX_ALPHANUM_PART_OF_TABLE_NAME) {
            builder.delete(MAX_ALPHANUM_PART_OF_TABLE_NAME, builder.length());
        }
        // Append the hash code of the original URI
        int hashCode = uri.hashCode();
        builder.append(hashCode < 0 ? -hashCode : hashCode);
        return builder.toString();
    }

    protected static class UriTableStatus {
        private final String tableName;
        private final PreparedStatement insertStmt;
        private final Map<String, byte[]> buffer;
        private final int bufferSize;

        public static UriTableStatus create(String tableName, Connection dbConnection, int bufferSize)
                throws SQLException {
            StringBuilder builder = new StringBuilder();
            builder.append(INSERT_URI_QUERY_PART_1);
            builder.append(tableName);
            builder.append(INSERT_URI_QUERY_PART_2);
            PreparedStatement insertStmt = dbConnection.prepareStatement(builder.toString());
            // insertStmt.batch
            return new UriTableStatus(tableName, insertStmt, bufferSize);
        }

        public UriTableStatus(String tableName, PreparedStatement insertStmt, int bufferSize) {
            this.tableName = tableName;
            this.insertStmt = insertStmt;
            buffer = new HashMap<>(2 * bufferSize);
            this.bufferSize = bufferSize;

        }

        public void addUri(String uri, byte[] serializedUri) {
        	 
            synchronized (buffer) {
                buffer.put(uri, serializedUri);
                if (buffer.size() >= bufferSize) {
                    execute_unsecured();
                }
            }
        }

        public void commitPendingChanges() {
            synchronized (buffer) {
                execute_unsecured();
            }
        }

        private void execute_unsecured() {
            try {
                for (String uri : buffer.keySet()) {
                    insertStmt.setString(1, uri);
                    insertStmt.setInt(2, uri.hashCode());
                    insertStmt.setBytes(3, buffer.get(uri));
                    try {
                        insertStmt.execute();
                    } catch (Exception e) {
//                    	if(!e.getMessage().substring(0, e.getMessage().indexOf(":")).equals("integrity constraint violation"))
//                    	LOGGER.warn("URI already exists in the table. It will be ignored.", e);

                        LOGGER.debug("Error while inserting URI (java.sql.SQLIntegrityConstraintViolationException is ok)", e);

                    }
                }
                insertStmt.getConnection().commit();
            } catch (BatchUpdateException e) {
//                 LOGGER.debug("URI already exists in the table. It will be ignored.", e);

            } catch (Exception e) {
//                LOGGER.error("Error while inserting a batch of URIs. They will be ignored.", e);
            }
            buffer.clear();
        }

        @Deprecated
        private void executeAsBatch_unsecured() {
            try {
                for (String uri : buffer.keySet()) {
                    insertStmt.setString(1, uri);
                    insertStmt.setInt(2, uri.hashCode());
                    insertStmt.setBytes(3, buffer.get(uri));
                    insertStmt.addBatch();
                }
            } catch (Exception e) {
                LOGGER.error("Error while creating insert statement for URI. It will be ignored.", e);
            }
            try {
                int[] insertResult = insertStmt.executeBatch();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Insert result was {}", Arrays.toString(insertResult));
                }
                insertStmt.getConnection().commit();
            } catch (BatchUpdateException e) {
                // LOGGER.error("URI already exists in the table. It will be ignored.", e);
            } catch (Exception e) {
                LOGGER.error("Error while inserting a batch of URIs. They will be ignored.", e);
            }
            buffer.clear();
        }

        public String getTableName() {
            return tableName;
        }
    }

}
