package org.aksw.simba.squirrel.fetcher.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.fetcher.dump.DumpFetcher;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPFetcher extends DumpFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FTPFetcher.class);

    @Override
    protected String downloadFile(CrawleableUri uri, String tempfolder) {
        // Download files to temp folder
        FTPClient client = new FTPClient();
        String tempfile = UriUtils.generateFileName(uri.toString(), false);
        File outputFile = new File(tempfolder, tempfile);
        OutputStream output = null;
        try {
            client.connect(uri.getIpAddress());
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                client.disconnect();
                LOGGER.error("FTP server refused connection ({}). Returning null.", client.getReplyString());
                return null;
            }

            client.enterLocalPassiveMode();
            output = new FileOutputStream(outputFile);
            if (!client.retrieveFile(uri.getUri().getPath(), output)) {
                LOGGER.error("Downloading {} was not succesful. Returning null.", uri.getUri().toString());
            }
        } catch (Exception e) {
            LOGGER.error("Exception while trying to download (" + uri.getUri().toString() + "). Returning null.", e);
            return null;
        } finally {
            IOUtils.closeQuietly(output);
            try {
                client.logout();
                client.disconnect();
            } catch (IOException e) {
            }
        }
        return outputFile.getPath();
    }

}
