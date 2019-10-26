package org.dice_research.squirrel.fetcher.ckan.java;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tika.io.IOUtils;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.dice_research.squirrel.fetcher.delay.Delayer;
import org.dice_research.squirrel.metadata.ActivityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.JackanModule;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.model.CkanDataset;

/**
 * Simple Java-based CKAN Fetcher.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SimpleCkanFetcher implements Fetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCkanFetcher.class);

    public static final String CKAN_JSON_OBJECT_MIME_TYPE = "ckan/json";
    public static final byte NEWLINE_CHAR = '\n';
    
    protected boolean checkForUriType = false;
    protected File dataDirectory = FileUtils.getTempDirectory();
    protected ObjectMapper mapper;
    
    public SimpleCkanFetcher() {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, false);
        mapper.registerModule(new JackanModule());
    }
    
    @Override
    public void close() throws IOException {
        // nothing to do
    }

    @Override
    public File fetch(CrawleableUri uri, Delayer delayer) {
        // If this is a CKAN API URI or we do not check it at all
    	LOGGER.info("Starting Ckanfetcher...");
        if(!checkForUriType || Constants.URI_TYPE_VALUE_CKAN.equals(uri.getData(Constants.URI_TYPE_KEY))) {
        	LOGGER.info("Fetching " + uri.getUri().toString());
            CkanClient client = null;
            OutputStream out = null;
            try {
                // wait for the permission for the first request
                delayer.getRequestPermission();
                
                client = new CkanClient(uri.getUri().toString());
                List<String> datasets = client.getDatasetList();
                File dataFile = File.createTempFile("fetched_", "", dataDirectory);
                out = new BufferedOutputStream(new FileOutputStream(dataFile));
                for(String dataset : datasets) {
                    fetchDataset(client, dataset, out);
                    out.write(NEWLINE_CHAR);
                }
                // If we reached this point, we should add a flag that the file contains CKAN JSON
                uri.addData(Constants.URI_HTTP_MIME_TYPE_KEY, Constants.URI_TYPE_VALUE_CKAN);
                ActivityUtil.addStep(uri, getClass());
                return dataFile;
            } catch(CkanException e) {
                LOGGER.info("The given URI does not seem to be a CKAN URI. Returning null");
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
    
    protected void fetchDataset(CkanClient client, String dataset, OutputStream out) {
        try {
            CkanDataset datasetObj = client.getDataset(dataset);
            out.write(mapper.writeValueAsBytes(datasetObj));
        }  catch(CkanException e) {
            LOGGER.info("Couldn't retrieve dataset from CKAN. It will be ignored. Exception: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error while writing dataset to result file.", e);
        }
    }

    /**
     * @return the checkForUriType
     */
    public boolean isCheckForUriType() {
        return checkForUriType;
    }

    /**
     * @param checkForUriType the checkForUriType to set
     */
    public void setCheckForUriType(boolean checkForUriType) {
        this.checkForUriType = checkForUriType;
    }

    /**
     * @return the dataDirectory
     */
    public File getDataDirectory() {
        return dataDirectory;
    }

    /**
     * @param dataDirectory the dataDirectory to set
     */
    public void setDataDirectory(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

}
