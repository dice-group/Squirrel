package org.aksw.simba.squirrel.iterators;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlBasedIterator implements Iterator<byte[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlBasedIterator.class);

    private static final String SELECT_TABLE_QUERY = "SELECT * FROM uris OFFSET ? FETCH NEXT ? ROWS ONLY ";

    protected PreparedStatement ps;
    protected ResultSet rs;
    protected boolean consumed = true;
    private int start = 0;
    private int next = 5;
    private int page = 5;
    private int countTotal = 0;

    public SqlBasedIterator(PreparedStatement ps, int count) {
        countTotal = count;
        this.ps = ps;
        try {
            ps.setInt(1, start);
            ps.setInt(2, next);
            rs = ps.executeQuery();

        } catch (SQLException e) {
            LOGGER.error("Exception while iterating over the results. Returning false.", e);
        }
    }

    @Override
    public boolean hasNext() {

        synchronized (rs) {
            if (consumed) {
                return true;
            } else {
                return false;
            }
        }

        // }catch (SQLException e) {
        // LOGGER.error("Exception while iterating over the results. Returning null.",
        // e);
        // return false;
        // }
    }

    // @Override
    // public boolean hasNext() {
    // try {
    //
    // synchronized (rs) {
    // if (consumed) {
    // return moveForward_unsecured();
    // } else {
    // return true;
    // }
    // }
    // } catch (SQLException e) {
    // LOGGER.error("Exception while iterating over the results. Returning false.",
    // e);
    // return false;
    // }
    // }

    @Override
    public byte[] next() {
        try {
            synchronized (rs) {
                // System.out.println(rs.getRow());
                if (start < countTotal) {
                    if (!moveForward_unsecured()) {
                        LOGGER.warn("\"next()\" was called while no result was left. Returning null.");
                        return null;
                    }
                }

                return rs.getBytes(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Exception while iterating over the results. Returning null.", e);
            return null;
        }
    }

    // move to next and add 1 to paging
    private boolean moveForward_unsecured() throws SQLException {
        if (rs.isClosed()) {
            return false;
        }

        boolean hasNext = rs.next();

        start = start + 1;
        if (start == next) {

            next = next + page;
            ps = ps.getConnection().prepareStatement(SELECT_TABLE_QUERY);
            ps.setInt(1, start);
            ps.setInt(2, next);
            rs = ps.executeQuery();
            rs.next();
            hasNext = true;
        }

        if (next > countTotal && start >= countTotal) {
            consumed = false;
            ps.close();
            rs.close();
        }
        return hasNext;
    }

}