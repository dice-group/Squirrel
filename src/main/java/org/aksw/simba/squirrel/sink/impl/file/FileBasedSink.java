package org.aksw.simba.squirrel.sink.impl.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Triple;

public class FileBasedSink implements Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedSink.class);

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final byte SEPERATOR[] = " ".getBytes(CHARSET);
    private static final byte PRE_GRAPH_URI_SEPERATOR[] = " <".getBytes(CHARSET);
    private static final byte END_OF_QUAD[] = "> .\n".getBytes(CHARSET);

    protected File outputDirectory;
    protected boolean useCompression;
    protected Map<String, OutputStream> streamMapping = new HashMap<String, OutputStream>();

    public FileBasedSink(File outputDirectory, boolean useCompression) {
        this.outputDirectory = outputDirectory;
        this.useCompression = useCompression;
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        String uriString = uri.getUri().toString();
        if (streamMapping.containsKey(uriString)) {
            OutputStream outputStream = streamMapping.get(uriString);
            try {
                outputStream.write(triple.getSubject().toString().getBytes(CHARSET));
                outputStream.write(SEPERATOR);
                outputStream.write(triple.getPredicate().toString().getBytes(CHARSET));
                outputStream.write(SEPERATOR);
                outputStream.write(triple.getObject().toString().getBytes(CHARSET));
                outputStream.write(PRE_GRAPH_URI_SEPERATOR);
                outputStream.write(uri.getUri().toString().getBytes(CHARSET));
                outputStream.write(END_OF_QUAD);
            } catch (Exception e) {
                LOGGER.error("Exception while writing the triple \"" + triple.toString() + "\" from the URI \""
                        + uri.getUri().toString() + "\". Ignoring it.", e);
            }
        }
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        try {
            OutputStream outputStream = new FileOutputStream(
                    outputDirectory.getAbsolutePath() + File.separator + generateFileName(uriString, useCompression));
            if (useCompression) {
                outputStream = new GZIPOutputStream(outputStream);
            }
            streamMapping.put(uriString, outputStream);
        } catch (IOException e) {
            LOGGER.error("Exception while trying to open file for \"" + uriString + "\" to use as sink.", e);
        }
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        if (streamMapping.containsKey(uriString)) {
            IOUtils.closeQuietly(streamMapping.get(uriString));
            streamMapping.remove(uriString);
        } else {
            LOGGER.error("Should close the sink for the URI \"" + uriString + "\" but couldn't find it.");
        }
    }

    public static String generateFileName(String uri, boolean useCompression) {
        return UriUtils.generateFileName(uri, useCompression);
    }
}
