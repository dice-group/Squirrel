package org.aksw.simba.squirrel.iterators;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlBasedIterator implements Iterator<String> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlBasedIterator.class);
	
	private static int START = 0;
	private static int NEXT = 5;
	private static int PAGE = 5;
	private static int COUNT_TOTAL = 0;
	
	private static final String SELECT_TABLE_QUERY = "SELECT * FROM uris OFFSET ? FETCH NEXT ? ROWS ONLY ";
	private static final String COUNT_TABLE = "SELECT COUNT(*) as TOTAL FROM uris";
//	private static final String SELECT_TABLE_QUERY = "SELECT * FROM uris ";

        protected PreparedStatement ps;
        protected ResultSet rs;
        protected boolean consumed = true;

        public SqlBasedIterator(PreparedStatement ps, int COUNT) {
        	COUNT_TOTAL = COUNT;
            this.ps = ps;
            try {
            	ps.setInt(1, START);
            	ps.setInt(2, NEXT);
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
        		
//        	}catch (SQLException e) {
//                LOGGER.error("Exception while iterating over the results. Returning null.", e);
//                return false;
//            }
        }

//        @Override
//        public boolean hasNext() {
//            try {
//            	
//                synchronized (rs) {
//                    if (consumed) {
//                        return moveForward_unsecured();
//                    } else {
//                        return true;
//                    }
//                }
//            } catch (SQLException e) {
//                LOGGER.error("Exception while iterating over the results. Returning false.", e);
//                return false;
//            }
//        }

        @Override
        public String next() {
            try {
                synchronized (rs) {
//                	System.out.println(rs.getRow());
                    if (START < COUNT_TOTAL) {
                        if (!moveForward_unsecured()) {
                            LOGGER.warn("\"next()\" was called while no result was left. Returning null.");
                            return null;
                        }
                    }
                 
                    return rs.getString(1);
                }
            } catch (SQLException e) {
                LOGGER.error("Exception while iterating over the results. Returning null.", e);
                return null;
            }
        }

        //move to next and add 1 to paging
        private boolean moveForward_unsecured() throws SQLException {
            if (rs.isClosed()) {
                return false;
            }

            boolean hasNext = rs.next();
            
            START = START + 1;
            if (START == NEXT) {
                
            	NEXT  = NEXT + PAGE;
            	ps = ps.getConnection().prepareStatement(SELECT_TABLE_QUERY);
            	ps.setInt(1, START);
            	ps.setInt(2, NEXT);
				rs = ps.executeQuery();
				rs.next();
                hasNext = true;
            }
            
            if(NEXT > COUNT_TOTAL && START >= COUNT_TOTAL) {
            	consumed = false;
            	ps.close();
            	rs.close();
            }
            return hasNext;
        }

    }