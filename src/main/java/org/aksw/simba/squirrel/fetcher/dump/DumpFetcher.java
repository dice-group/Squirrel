package org.aksw.simba.squirrel.fetcher.dump;

import com.hp.hpl.jena.graph.Triple;
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
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.jena.riot.system.StreamRDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class DumpFetcher implements Fetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DumpFetcher.class);

    @Override
    public int fetch(CrawleableUri uri, Sink sink) {
        int tripleCount = 0;
        String filePath = this.downloadFile(uri, "/tmp/");
        LOGGER.debug("{} is saved to {}", uri.toString(), filePath);
        String fileType = this.detectFileType(uri, filePath);
        LOGGER.debug("Detected file type: {}", fileType);

        if(fileType.contains("application/zip")) {
            LOGGER.debug("Archive extraction not implemented, skipping.");
            return tripleCount;
        }

        Lang hint;
        if(this.detectSerialization(uri.toString(), "rdf").equals("rdf")) {
            LOGGER.debug("Detected rdf/xml serialization.");
            hint = Lang.RDFXML;
        } else if (this.detectSerialization(uri.toString(), "ttl").equals("ttl")) {
            LOGGER.debug("Detected ttl serialization.");
            hint = Lang.TURTLE;
        } else if (this.detectSerialization(uri.toString(), "nt").equals("nt")) {
            LOGGER.debug("Detected nt serialization.");
            hint = Lang.NT;
        } else if (this.detectSerialization(uri.toString(), "n3").equals("n3")) {
            hint = Lang.N3;
            LOGGER.debug("Detected n3 serialization.");
        } else {
            LOGGER.error("Could not detect serialization for {}. Skipping.", uri.toString());
            return tripleCount;
        }

        PipedRDFIterator<Triple> iterator = new PipedRDFIterator<Triple>();
        final PipedRDFStream<Triple> inStream = new PipedTriplesStream(iterator);
        RDFDataMgr.parse(inStream, filePath, hint, null);
        sink.openSinkForUri(uri);
        while (iterator.hasNext()) {
            Triple next = iterator.next();
            sink.addTriple(uri, next);
            ++tripleCount;
        }
        sink.closeSinkForUri(uri);
        return tripleCount;
    }

    private String downloadFile(CrawleableUri uri, String tempfolder) {
        //Download files to temp folder
        String tempfile = UriUtils.generateFileName(uri.toString(), false);
        File outputFile = new File(tempfolder, tempfile);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(uri.getUri());
        try {
            LOGGER.debug("Executing request: " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = entity.getContent();
                try {
                    LOGGER.debug("Saving file from {} to {}", uri.toString(), outputFile.getPath());
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
        return outputFile.getPath();
    }

    private String detectFileType(CrawleableUri uri, String filePath) {
        Path path = FileSystems.getDefault().getPath(filePath);
        String fileType = "";
        try {
            fileType = Files.probeContentType(path);
        } catch (IOException e) {
            LOGGER.error("Could not probe type of {}", filePath);
        }
        return fileType;
    }

    private String detectSerialization(String uriString, String serialization) {
        String[] regexs = {".*\\."+serialization+".*"};
        if(UriUtils.isStringMatchRegexs(uriString, regexs)) {
            return serialization;
        } else {
            return "";
        }
    }
}
