package org.dice_research.squirrel.fetcher.sparql;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Iterator;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.apache.commons.io.FileUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.tika.io.IOUtils;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.dice_research.squirrel.metadata.ActivityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A simple {@link Fetcher} for SPARQL that tries to get DataSets from a SPARQL
 * endpoint using the query {@value #DATA_SET_QUERY}.
 *
 * @author Geraldo de Souza Jr (gsjunior@uni-paderborn.de)
 *
 */
@Component
public class SparqlDatasetFetcher implements Fetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlDatasetFetcher.class);

    /**
     * The delay that the system will have between sending two queries.
     */

    protected String dataSetQuery = "select ?s where {?s a <http://www.w3.org/ns/dcat#Dataset>.} " ;
    protected String graphQuery = 
			"construct { ?s ?p ?o. " + 
			"?o ?p2 ?o2. } " + 
			"where { " + 
			"graph ?g { " + 
			"?s ?p ?o. " + 
			"OPTIONAL { ?o ?p2 ?o2.} " + 
			"} " + 
			"}";
    


    protected int delay;
    protected int limit = 0;
    protected File dataDirectory = FileUtils.getTempDirectory();
    
    public SparqlDatasetFetcher() {
    	
    }
    
    
    public SparqlDatasetFetcher(int delay, int begin, int limit) {
    	this.delay = delay;
    	dataSetQuery += "OFFSET " + begin;
    	this.limit = limit;
    	
    }
    
    public SparqlDatasetFetcher(int delay){
    	this.delay = delay;
    }

    @Override
    public File fetch(CrawleableUri uri) {
        // Check whether we can be sure that it is a SPARQL endpoint
        boolean shouldBeSparql = Constants.URI_TYPE_VALUE_SPARQL.equals(uri.getData(Constants.URI_TYPE_KEY));
        QueryExecutionFactory qef = null;
        QueryExecution execution = null;
        File dataFile = null;
        OutputStream out = null;
        try {
            // Create query execution instance
            qef = initQueryExecution(uri.getUri().toString());
            // create temporary file
            try {
            	dataFile = File.createTempFile("fetched_", "", dataDirectory);
                out = new BufferedOutputStream(new FileOutputStream(dataFile));
            } catch (IOException e) {
                LOGGER.error("Couldn't create temporary file for storing fetched data. Returning null.", e);
                return null;
            }
            
            execution = qef.createQueryExecution(dataSetQuery);
            ResultSet resultSet = execution.execSelect();
            
            int i = 0;
            
            while(resultSet.hasNext()) {
            	
            	 if(limit != 0 && i>limit) {
            		 LOGGER.info("LIMIT REACHED, STOPING EXECUTION");
            		 execution.close();
            		 break;
            	 }
                 	
            	
              QuerySolution soln = resultSet.nextSolution() ;
  		      String dataSetResource = soln.get("s").toString() ;       // Get a result variable by name.
  		      LOGGER.info("- Now Fetching - " + i + ": " + dataSetResource);
           
		      Query query = QueryFactory.create(graphQuery.replaceAll("\\?s", "<" + dataSetResource + ">")) ;
		      
		      
			  boolean tryAgain = true;
			  
		      while(tryAgain) {
		    	  
		    	try {
		  			Thread.sleep(delay);
		  		} catch (InterruptedException e) {
		  			LOGGER.error("An error occurred when fetching URI: " + uri.getUri().toString()
		  					,e);
		  		}
		      
		      try {	      
		      QueryExecution qexecGraph = org.apache.jena.query.QueryExecutionFactory.createServiceRequest(uri.getUri().toString(), query);
		    	  
		    	  Iterator<Triple> triples = qexecGraph.execConstructTriples();
		    	  
		    	  RDFDataMgr.writeTriples(out, new SelectedTriplesIterator(triples));
		    	  tryAgain = false;

		    	  i++;
		      }catch(QueryExceptionHTTP e) {
		    	  
		    	  if(e.getResponseCode() == 404 || e.getResponseCode() == 500) {
		    		 tryAgain = true; 
		    		 LOGGER.info("Error while fetching " +dataSetResource + ". Trying again...");
		    	  }
		  
		      	}
		      }        
            }
            
            
//            RDFDataMgr.writeTriples(out, new SelectedTriplesIterator(resultSet));
        } catch (Throwable e) {
            // If this should have worked, print a message, otherwise silently return null
            if (shouldBeSparql) {
                LOGGER.error("Couldn't create QueryExecutionFactory for \"" + uri.getUri() + "\". Returning -1.");
                ActivityUtil.addStep(uri, getClass(), e.getMessage());
            }
            return null;
        } finally {
            IOUtils.closeQuietly(out);
            if (execution != null) {
                execution.close();
            }
            if (qef != null) {
                qef.close();
            }
        }
        ActivityUtil.addStep(uri, getClass());
        return dataFile;
    }

    protected QueryExecutionFactory initQueryExecution(String uri) throws ClassNotFoundException, SQLException {
        QueryExecutionFactory qef;
        qef = new QueryExecutionFactoryHttp(uri);
        qef = new QueryExecutionFactoryDelay(qef, delay);
        try {
        	LOGGER.info("Starting to Query uri:" + uri);
            return new QueryExecutionFactoryPaginated(qef, 2000);
        } catch (Exception e) {
            LOGGER.info("Couldn't create Factory with pagination. Returning Factory without pagination. Exception: {}",
                    e.getLocalizedMessage());
            return qef;
        }
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }

    protected static class SelectedTriplesIterator implements Iterator<Triple> {
        private Iterator<Triple> triples;

        public SelectedTriplesIterator(Iterator<Triple> triples) {
            this.triples = triples;
        }

        @Override
        public boolean hasNext() {
            return triples.hasNext();
        }

        @Override
        public Triple next() {
           return triples.next();
        }

    }


}
