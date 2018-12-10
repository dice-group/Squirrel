package org.dice_research.squirrel.fetcher.sparql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Sparql Fetcher to fetch <http://www.w3.org/ns/dcat#Dataset>
 * 
 * @author Geraldo Junior gsjunior@mail.uni-paderborn.de
 *
 */

public class SparqlDatasetFetcher implements Fetcher {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(SparqlDatasetFetcher.class);

    /**
     * The delay that the system will have between sending two queries.
     */
    private int DELAY = 2000;

    protected File dataDirectory = FileUtils.getTempDirectory();
    protected int page_size = 1000;
    protected int begin = 0;
    
    protected String dataSetQuery = "select ?s where {?s a <http://www.w3.org/ns/dcat#Dataset>.}  OFFSET :begin LIMIT :lim" ;
    
    protected String graphQuery = 
			"construct { ?s ?p ?o. " + 
			"?o ?p2 ?o2. } " + 
			"where { " + 
			"graph ?g { " + 
			"?s ?p ?o. " + 
			"OPTIONAL { ?o ?p2 ?o2.} " + 
			"} " + 
			"}";
    
    public SparqlDatasetFetcher() {
    }
    
    public SparqlDatasetFetcher(int DELAY) {
    	this();
    	this.DELAY = DELAY;
    }
    
    
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
		  CrawleableUri curi = new CrawleableUri(new URI("https://www.europeandataportal.eu/sparql"));
		  
		  new SparqlDatasetFetcher(1000).fetch(curi);

	}
    
    @Override
    public File fetch(CrawleableUri uri) {
    	File dataFile = null;
    	ZipOutputStream out = null;
    	try {
            dataFile = File.createTempFile("fetched_", "", dataDirectory);
            out = new ZipOutputStream(new FileOutputStream(dataFile));
        } catch (IOException e) {
            LOGGER.error("Couldn't create temporary file for storing fetched data. Returning null.", e);
            return null;
        }
    	
      String service = uri.getUri().toString();
      boolean goon = true;
      
      while(goon) {
    	
	      String pageQuery = this.dataSetQuery.replaceAll("\\:begin", String.valueOf(begin)).replaceAll("\\:lim", String.valueOf(page_size));
	      
	      LOGGER.info("Fetching from " + begin + " to " + (begin*page_size + page_size));
	
		  Query query = QueryFactory.create(pageQuery) ;
		  
		  QueryExecution qexecDataSet = QueryExecutionFactory.sparqlService(service, query);
		    ResultSet resultsDataSet = qexecDataSet.execSelect() ;
		    
		    if(!resultsDataSet.hasNext()) {
		    	break;
		    }
		    
		    for ( ; resultsDataSet.hasNext() ; )
		    {
		      QuerySolution soln = resultsDataSet.nextSolution() ;
		      String dataSetResource = soln.get("s").toString() ;       // Get a result variable by name.
		      
		      LOGGER.info("- Now Fetching: " + dataSetResource);
		      query = QueryFactory.create(graphQuery.replaceAll("\\?s", "<" + dataSetResource + ">")) ;
		      
		      
		      //TODO implement fault security check
		      
			  boolean tryAgain = true;
			  
		      while(tryAgain) {
		    	  
		    	try {
		  			Thread.sleep(DELAY);
		  		} catch (InterruptedException e) {
		  			LOGGER.error("An error occurred when fetching URI: " + uri.getUri().toString()
		  					,e);
		  		}
		      
		      try {	      
		      QueryExecution qexecGraph = QueryExecutionFactory.sparqlService(service, query);
		    	  
		    	  Iterator<Triple> triples = qexecGraph.execConstructTriples();
		    	  
		    	  File entry = File.createTempFile("entry_", "", dataDirectory);
		    	  ZipEntry ze= new ZipEntry(entry.getAbsolutePath());
		    	  out.putNextEntry(ze);
		    	  RDFDataMgr.writeTriples(out, new SelectedTriplesIterator(triples));
		    	  tryAgain = false;
		    	  entry.delete();
		    	  out.flush();
		    	  out.closeEntry();
		      }catch(QueryExceptionHTTP e) {
		    	  
		    	  if(e.getResponseCode() == 404 || e.getResponseCode() == 500) {
		    		 tryAgain = true; 
		    		 System.out.println("Error while fetching " +dataSetResource + ". Trying again...");
		    		 LOGGER.info("Error while fetching " +dataSetResource + ". Trying again...");
		    	  }
		  
		      } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		      	}
		      }
	
		    }
		    begin = begin + page_size;
      }
	  
	try {
		out.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     return dataFile;
    }


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
