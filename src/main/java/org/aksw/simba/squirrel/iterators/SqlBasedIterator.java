package org.aksw.simba.squirrel.iterators;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlBasedIterator implements Iterator<byte[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlBasedIterator.class);


    protected PreparedStatement ps;
    protected ResultSet rs;
    protected boolean hasNext = true;
    private int start = 0;
    private int next = 5;
    private int page = 5;

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

        synchronized (rs) {
        	try {

        		if(start == next) {
        			next = next + page;
                    ps.setInt(1, start);
                    ps.setInt(2, next);
                    rs = ps.executeQuery();
                    hasNext = rs.next();
        		}else {
        			hasNext = rs.next();
        		}
        		
        		return hasNext;
        	}catch(SQLException e) {
        		LOGGER.error("Exception while iterating over the results. Returning null.", e);
        		return false;
        	}
        	
            
        }

    }



    @Override
    public byte[] next() {
        
            synchronized (rs) {
                // System.out.println(rs.getRow());
	            	try {
	            		if(hasNext) {
	            			 start = start + 1;
	            			 return rs.getBytes(3);
	            		}else {
	            			LOGGER.error("Exception while iterating over the results. Returning null.");
	            			return null;
	            		}
		               
	            	}catch (SQLException e) {
	            				LOGGER.error("Exception while iterating over the results. Returning null.", e);
	            				return null;
					}
                    	
                }

                
            }
        
 

}