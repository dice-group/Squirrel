package org.aksw.simba.squirrel.sink.collect;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Iterator;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.AbstractSinkDecorator;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlBasedUriCollector extends AbstractSinkDecorator implements UriCollector, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlBasedUriCollector.class);

    protected static final String DROP_TABLE_QUERY = "DROP TABLE uris";
    protected static final String CREATE_TABLE_QUERY = "CREATE TABLE uris (uri varchar(500) primary key)";
    protected static final String CLEAR_TABLE_QUERY = "DELETE FROM uris";

    public static SqlBasedUriCollector create(Sink decorated) {
        SqlBasedUriCollector collector = null;
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(System.out);
        }
        Statement s = null;
        try {
            Connection dbConnection = DriverManager.getConnection("jdbc:hsqldb:foundUris", "SA", "");
            s = dbConnection.createStatement();
            try {
                s.executeUpdate(DROP_TABLE_QUERY);
            } catch (Exception e) {
                LOGGER.info("Couldn't drop table \"uris\". Maybe it is not existing.");
            }
            s.executeUpdate(CREATE_TABLE_QUERY);
            PreparedStatement insertStmt = dbConnection.prepareStatement("INSERT INTO uris VALUES(?)");
            collector = new SqlBasedUriCollector(dbConnection, insertStmt, decorated);
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
    protected PreparedStatement insertStmt;

    public SqlBasedUriCollector(Connection dbConnection, PreparedStatement insertStmt, Sink decorated) {
        super(decorated);
        this.dbConnection = dbConnection;
        this.insertStmt = insertStmt;
    }

    @Override
    public Iterator<String> getUris() {
        try {
            Statement s = dbConnection.createStatement();
            ResultSet rs = s.executeQuery("SELECT uri FROM uris");
            return new ResultIterator(rs, s);
        } catch (SQLException e) {
            LOGGER.error("Exception while querying URIs from database. Returning null.");
        }
        return null;
    }

    @Override
    public void reset() {
        try {
            Statement s = dbConnection.createStatement();
            s.executeUpdate(CLEAR_TABLE_QUERY);
        } catch (SQLException e) {
            LOGGER.error("Couldn't clear the table containing the extracted URIs.", e);
        }
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        addUri(triple.getSubject());
        addUri(triple.getPredicate());
        addUri(triple.getObject());
        super.addTriple(uri, triple);
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        reset();
        super.openSinkForUri(uri);
    }

    protected void addUri(Node node) {
        if (node.isURI()) {
            try {
                insertStmt.setString(1, node.getURI());
                insertStmt.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                // tried to insert a URI a second time
            } catch (SQLException e) {
                LOGGER.error("Exception while trying to insert new URI.", e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            insertStmt.close();
        } catch (SQLException e) {
        }
        try {
            dbConnection.close();
        } catch (SQLException e) {
        }
    }

    protected static class ResultIterator implements Iterator<String> {

        protected Statement s;
        protected ResultSet rs;
        protected boolean consumed = true;

        public ResultIterator(ResultSet rs, Statement s) {
            this.s = s;
            this.rs = rs;
        }

        @Override
        public boolean hasNext() {
            try {
                synchronized (rs) {
                    if (consumed) {
                        return moveForward_unsecured();
                    } else {
                        return true;
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Exception while iterating over the results. Returning false.", e);
                return false;
            }
        }

        @Override
        public String next() {
            try {
                synchronized (rs) {
                    if (consumed) {
                        if (!moveForward_unsecured()) {
                            LOGGER.warn("\"next()\" was called while no result was left. Returning null.");
                            return null;
                        }
                    }
                    consumed = true;
                    return rs.getString(1);
                }
            } catch (SQLException e) {
                LOGGER.error("Exception while iterating over the results. Returning null.", e);
                return null;
            }
        }

        private boolean moveForward_unsecured() throws SQLException {
            if (rs.isClosed()) {
                return false;
            }
            boolean hasNext = rs.next();
            if (hasNext) {
                consumed = false;
            } else {
                // close the result set
                rs.close();
                s.close();
            }
            return hasNext;
        }

    }

}
