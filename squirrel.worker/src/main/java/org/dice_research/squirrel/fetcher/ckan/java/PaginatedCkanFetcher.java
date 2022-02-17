package org.dice_research.squirrel.fetcher.ckan.java;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.tika.io.IOUtils;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.dice_research.squirrel.fetcher.delay.Delayer;
import org.dice_research.squirrel.metadata.ActivityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.CkanQuery;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.model.CkanDataset;

/**
 * Simple Java-based CKAN Fetcher.
 * 
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */

public class PaginatedCkanFetcher extends SimpleCkanFetcher implements Fetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaginatedCkanFetcher.class);
    
    private final int DEFAULT_TIMEOUT = 10000;
    private int timeout;
    private final int PAGESIZE = 100;
    
  /**
   * Time out for the Ckan Client  
   * @param timeout
   */
    public PaginatedCkanFetcher(int timeout) {
    	this.timeout = timeout;
	}
    
    public PaginatedCkanFetcher() {
		this.timeout = DEFAULT_TIMEOUT;
	}
    

    @Override
    public File fetch(CrawleableUri uri, Delayer delayer) {
        LOGGER.info("Fetching " + uri.getUri().toString());
        CkanClient client = null;
        OutputStream out = null;
        if (!checkForUriType || Constants.URI_TYPE_VALUE_CKAN.equals(uri.getData(Constants.URI_TYPE_KEY))) {

            try {

                client = CkanClient.builder()
                        .setCatalogUrl(uri.getUri().toString())
                        .setTimeout(timeout)
                        .build();
                

                List<String> datasets = client.getDatasetList();
                LOGGER.info("Found: " + datasets.size() + " datasets");
                File dataFile = File.createTempFile("fetched_", "", dataDirectory);
                out = new BufferedOutputStream(new FileOutputStream(dataFile));

                int offset = 0;
                int totalPages = datasets.size() / PAGESIZE;

                do {
                    delayer.getRequestPermission();
                    LOGGER.info("Fetching Page: " + String.valueOf(offset / PAGESIZE) + " of " + totalPages);
                    fetchDataset(client, PAGESIZE, offset, out);
                    offset = offset + PAGESIZE;
                } while (offset <= datasets.size());

                // If we reached this point, we should add a flag that the file contains CKAN
                // JSON
                ActivityUtil.addStep(uri, getClass());
                uri.addData(Constants.URI_HTTP_MIME_TYPE_KEY, Constants.URI_TYPE_VALUE_CKAN);
                return dataFile;
            } catch (CkanException e) {
                LOGGER.info("The given URI: {} does not seems to be a CKAN URI. Returning null", uri.getUri().toString());
                ActivityUtil.addStep(uri, getClass(), e.getMessage());
                return null;
            } catch (IOException e) {
                LOGGER.error("Error while writing result file. Returning null.", e);
                ActivityUtil.addStep(uri, getClass(), e.getMessage());
                return null;
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while waiting for request permission. Returning null.");
                ActivityUtil.addStep(uri, getClass(), e.getMessage());
                return null;
            } finally {
                IOUtils.closeQuietly(out);
                delayer.requestFinished();
            }

        }
        return null;
    }

    protected void fetchDataset(CkanClient client, int pageSize, int offSet, OutputStream out) {
        try {
            CkanQuery query = CkanQuery.filter();
            List<CkanDataset> queryDS = client.searchDatasets(query, pageSize, offSet).getResults();

            for (CkanDataset datasetObj : queryDS) {
                out.write(mapper.writeValueAsBytes(datasetObj));
                out.write(NEWLINE_CHAR);
            }

        } catch (CkanException e) {
            LOGGER.info("Couldn't retrieve dataset from CKAN. It will be ignored. Exception: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error while writing dataset to result file.", e);
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        PaginatedCkanFetcher fetcher = new PaginatedCkanFetcher();
        fetcher.setCheckForUriType(false);
        File datafile = fetcher.fetch(new CrawleableUri(new URI("https://open.alberta.ca/")));
        System.out.println(datafile != null ? datafile.toString() : "null");
        fetcher.close();
    }
}
