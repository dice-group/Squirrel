package org.aksw.simba.squirrel.iterators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlBasedIterator implements Iterator<String> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlBasedIterator.class);

        protected Statement s;
        protected ResultSet rs;
        protected boolean consumed = true;

        public SqlBasedIterator(Statement s) {
            this.s = s;
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
                //TODO  add pagination here
            }
            return hasNext;
        }

    }