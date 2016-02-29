package org.aksw.simba.squirrel.fetcher.dump;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.fetcher.Fetcher;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class DumpFetcher implements Fetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DumpFetcher.class);

    @Override
    public int fetch(CrawleableUri uri, Sink sink) {
        //Download files to temp folder
        String tempfolder = "/tmp/";
        String tempfile = UriUtils.generateFileName(uri.toString(), false);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(uri.getUri());
        try {
            LOGGER.debug("Executing request: " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = entity.getContent();
                try {
                    File outputFile = new File(tempfolder, tempfile);
                    LOGGER.debug("Saving archive from {} to {}", uri.toString(), outputFile.getPath());
                    OutputStream outputStream = new FileOutputStream(outputFile);
                    IOUtils.copy(inputStream, outputStream);
                    outputStream.close();
                } finally {
                    inputStream.close();
                }
            }
            response.close();
            httpclient.close();
        } catch (ClientProtocolException ServerFail){
            LOGGER.error("Worker failed to prcess request: " + ServerFail.getMessage());
        } catch (IOException ClientProtocolException) {
            LOGGER.error("Worker could not process request: " + ClientProtocolException.getMessage());
        }

        //archive?
          //Unpack
          //remove original archive

        //iterate over all rdf files for this worker (downloaded/extracted)
        //Write to sink
        return 0;
    }
}
