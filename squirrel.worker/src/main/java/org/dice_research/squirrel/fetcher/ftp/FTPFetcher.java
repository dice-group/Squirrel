package org.dice_research.squirrel.fetcher.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.dice_research.squirrel.fetcher.delay.Delayer;
import org.dice_research.squirrel.metadata.ActivityUtil;
import org.dice_research.squirrel.utils.Closer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple fetcher using the FTP protocol.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */

public class FTPFetcher implements Fetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FTPFetcher.class);

    protected static final Set<String> ACCEPTED_SCHEMES = new HashSet<String>(Arrays.asList("ftp", "ftps"));

    protected File dataDirectory = FileUtils.getTempDirectory();
    private FTPRecursiveFetcher recursiveFetcher;

    @Override
    public File fetch(CrawleableUri uri, Delayer delayer) {
        // Check whether this fetcher can handle the given URI
        if ((uri == null) || (uri.getUri() == null) || (!ACCEPTED_SCHEMES.contains(uri.getUri().getScheme()))) {
            return null;
        }
        // create temporary file
        File dataFile = null;
        try {
            dataFile = File.createTempFile("fetched_", "", dataDirectory);
        } catch (IOException e) {
            LOGGER.error("Couldn't create temporary file for storing fetched data. Returning null.", e);
            ActivityUtil.addStep(uri, getClass(), e.getMessage());
            return null;
        }
        return requestData(uri, dataFile);
    }

    @SuppressWarnings("resource")
    private File requestData(CrawleableUri uri, File dataFile) {

        // Download file to temp folder
        FTPClient client = new FTPClient();
        OutputStream output = null;
        try {

            client.connect(uri.getIpAddress());
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                client.disconnect();
                LOGGER.error("FTP server refused connection ({}). Returning null.", client.getReplyString());
                return null;
            }

            client.enterLocalPassiveMode();
            client.login("anonymous", "");

            if (client.mlistFile(uri.getUri().getPath()).isDirectory()) {
                Path path = Files.createTempDirectory("file_");
                recursiveFetcher = new FTPRecursiveFetcher(path);
                recursiveFetcher.listDirectory(client, uri.getUri().getPath(), "", 0);
                dataFile = path.toFile();

            } else {
                output = new FileOutputStream(dataFile);
                if (!client.retrieveFile(uri.getUri().getPath(), output)) {
                    LOGGER.error("Downloading {} was not successful. Returning null.", uri.getUri().toString());
                }
            }

        } catch (Exception e) {
            LOGGER.error("Exception while trying to download (" + uri.getUri().toString() + "). Returning null.", e);
            ActivityUtil.addStep(uri, getClass(), e.getMessage());
            return null;
        } finally {
            Closer.close(output);
            try {
                client.logout();
                client.disconnect();
            } catch (IOException e) {
            }
        }
        ActivityUtil.addStep(uri, getClass());
        return dataFile;
    }


    @Override
    public void close() {
        // nothing to do
    }


}
