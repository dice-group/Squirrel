package org.aksw.simba.squirrel.iterators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class SqlBasedIterator implements Iterator<byte[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlBasedIterator.class);

    protected PreparedStatement ps;
    protected ResultSet rs;
    protected boolean consumed = true;
    protected boolean hasNext = true;
    private int start = 0;
    private int next = 100;
    private int page = 100;

    public SqlBasedIterator(PreparedStatement ps) {
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
        synchronized (ps) {
            return hasNext_unsecured();
        }
    }

    @Override
    public byte[] next() {
        synchronized (ps) {
            try {
                if (hasNext_unsecured()) {
                    start = start + 1;
                    consumed = true;
                    return rs.getBytes(3);
                } else {
                    return null;
                }
            } catch (SQLException e) {
                LOGGER.error("Exception while iterating over the results. Returning null.", e);
                return null;
            }
        }
    }

    private boolean hasNext_unsecured() {
        try {
            // If the current object has been consumed, move to the next
            if (consumed) {
                if (start == next) {
                    next = next + page;
                    ps.setInt(1, start);
                    ps.setInt(2, next);
                    rs = ps.executeQuery();
                    hasNext = rs.next();
                    consumed = false;
                } else {
                    hasNext = rs.next();
                    consumed = false;
                }
            }
            
            if(!hasNext) {
            	rs.close();
            	ps.close();
            }
            
            return hasNext;
        } catch (SQLException e) {
            LOGGER.error("Exception while iterating over the results. Returning null.", e);
            return false;
        }
    }

}