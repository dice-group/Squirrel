package org.aksw.simba.squirrel.sink.impl.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBasedSink implements Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedSink.class);

    protected File outputDirectory;
    protected boolean useCompression;
    protected Map<String, OutputStream> streamMapping = new HashMap<String, OutputStream>();

    public FileBasedSink(File outputDirectory, boolean useCompression) {
        this.outputDirectory = outputDirectory;
        this.useCompression = useCompression;
    }

    @Override
    public void addTriple(CrawleableUri uri, String data) {
        String uriString = uri.getUri().toString();
        if (streamMapping.containsKey(uriString)) {
            OutputStream outputStream = streamMapping.get(uriString);
            // TODO write data to output stream
        }
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        try {
            String uriString = uri.getUri().toString();
            OutputStream outputStream = new FileOutputStream(
                    outputDirectory.getAbsolutePath() + File.separator + generateFileName(uriString, useCompression));
            if (useCompression) {
                outputStream = new GZIPOutputStream(outputStream);
            }
            streamMapping.put(uri.getUri().toString(), outputStream);
        } catch (IOException e) {
            LOGGER.error("Exception while trying to open file for to use as sink.", e);
        }
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        if (streamMapping.containsKey(uriString)) {
            IOUtils.closeQuietly(streamMapping.get(uriString));
        }
    }

    public static String generateFileName(String uri, boolean useCompression) {
        StringBuilder builder = new StringBuilder(uri.length() + 10);
        char chars[] = uri.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (Character.isLetterOrDigit(chars[i])) {
                builder.append(chars[i]);
            } else {
                builder.append('_');
            }
        }
        if (useCompression) {
            builder.append(".gz");
        }
        return builder.toString();
    }
}
