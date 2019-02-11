package org.dice_research.squirrel.sink.impl.file;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.collections15.MapUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFWriter;
import org.apache.log4j.lf5.util.StreamUtils;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.UriUtils;
import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.utils.Closer;
import org.dice_research.squirrel.vocab.Prefixes;
import org.dice_research.squirrel.vocab.Squirrel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBasedSink implements Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedSink.class);

    public static final Lang DEFAULT_OUTPUT_LANG = Lang.TURTLE;

    /**
     * Directory to which the files of this sink are written.
     */
    protected File outputDirectory;
    /**
     * Flag whether a compression algorithm should be used.
     */
    protected boolean useCompression;
    /**
     * Synchronized mapping of crawled URIs to their output stream.
     */
    protected Map<String, StreamStatus> streamMapping = MapUtils.synchronizedMap(new HashMap<String, StreamStatus>());
    /**
     * Language used for the output files.
     */
    protected Lang outputLang;

    public FileBasedSink(File outputDirectory, boolean useCompression) {
        this(outputDirectory, DEFAULT_OUTPUT_LANG, useCompression);
    }
    
    public FileBasedSink(File outputDirectory, Lang outputLang, boolean useCompression) {
        this.outputDirectory = outputDirectory;
        this.outputLang = outputLang;
        this.useCompression = useCompression;
        openSinkForUri(new CrawleableUri(Constants.DEFAULT_META_DATA_GRAPH_URI));
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        StreamStatus status = getStream(uri);
        try {
            status.getTripleOutputStream().triple(triple);
            status.increaseTripleCount();
        } catch (Exception e) {
            LOGGER.error("Exception while writing the triple \"" + triple.toString() + "\" from the URI \""
                    + uri.getUri().toString() + "\". Ignoring it.", e);
        }
    }

    @Override
    public void addData(CrawleableUri uri, InputStream is) {
        StreamStatus stream = getStream(uri);
        try {
            StreamUtils.copy(is, stream.getDataOutputStream());
        } catch (Exception e) {
            LOGGER.error("Exception while writing unstructed data to file.", e);
        }
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        // Add the URI but do not open a stream
        streamMapping.put(uri.getUri().toString(), null);
    }

    private StreamStatus getStream(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        if (streamMapping.containsKey(uriString)) {
            StreamStatus stream = streamMapping.get(uriString);
            if (stream == null) {
                stream = new StreamStatus(uri, outputDirectory, outputLang, useCompression);
                streamMapping.put(uriString, stream);
            }
            return stream;
        } else {
            LOGGER.error(
                    "A stream for {} was requested but openSinkForUri hasn't been called before. It will be ignored.",
                    uri.getUri().toString());
            return null;
        }
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        StreamStatus status = null;
        if (streamMapping.containsKey(uriString)) {
//            LOGGER.debug("PerformanceAnalysis triple count for uri " + uriString + " is: " + streamMapping.get(uriString).tripleCount);
            Closer.close(streamMapping.get(uriString));
            status = streamMapping.remove(uriString);
        } else {
            LOGGER.error("Should close the sink for the URI \"" + uriString + "\" but couldn't find it.");
        }
        // Add provenance information 
        CrawlingActivity activity = (CrawlingActivity) uri.getData(Constants.URI_CRAWLING_ACTIVITY);
        if ((activity != null) && (status != null) && (status.tripleCount > 0)) {
            activity.setNumberOfTriples(status.tripleCount);
        }
    }

    public static String generateFileName(CrawleableUri uri, Lang outputLang, boolean useCompression) {
        String fileEnding = null;
        if (outputLang != null) {
            fileEnding = outputLang.getFileExtensions().get(0);
        }
        if (useCompression) {
            if (fileEnding == null) {
                fileEnding = "gz";
            } else {
                fileEnding += ".gz";
            }
        }
        return UriUtils.generateFileName(uri, fileEnding);
    }

    protected static class StreamStatus implements Closeable {
        public int tripleCount = -1;
        protected CrawleableUri uri;
        protected String uriString;
        protected File outputDirectory;
        protected boolean useCompression;
        protected Lang outputLang;
        protected StreamRDF tripleStream;
        protected OutputStream tripleOutputStream;
        protected OutputStream dataOutputStream;

        public StreamStatus(CrawleableUri uri, File outputDirectory, Lang outputLang, boolean useCompression) {
            this.uri = uri;
            this.uriString = uri.getUri().toString();
            this.outputDirectory = outputDirectory;
            this.outputLang = outputLang;
            this.useCompression = useCompression;
        }

        public synchronized StreamRDF getTripleOutputStream() throws IOException {
            if (tripleOutputStream == null) {
                tripleOutputStream = createStream(null, true);
                tripleStream = StreamRDFWriter.getWriterStream(tripleOutputStream, outputLang);
                // Define prefixes the stream should use
                for(Entry<String, String> prefix : Prefixes.PREFIX_TO_URI.entrySet()) {
                    tripleStream.prefix(prefix.getKey(), prefix.getValue());
                }
                tripleCount = 0;
            }
            return tripleStream;
        }

        public synchronized OutputStream getDataOutputStream() throws IOException {
            if (dataOutputStream == null) {
                dataOutputStream = createStream(".dat", false);
            }
            return dataOutputStream;
        }

        protected OutputStream createStream(String postFix, boolean isRdfFile) throws IOException {
            File file = new File(outputDirectory.getAbsolutePath() + File.separator
                    + generateFileName(uri, (isRdfFile ? outputLang : null), useCompression));
            OutputStream outputStream = new FileOutputStream(file);
            if (useCompression) {
                outputStream = new GZIPOutputStream(outputStream);
            }
            // Add the information that we created an output file
            CrawlingActivity activity = (CrawlingActivity) uri.getData(Constants.URI_CRAWLING_ACTIVITY);
            if (activity != null) {
                activity.addOutputResource(file.toURI().toString(), Squirrel.ResultFile);
            }
            return outputStream;
        }

        public void increaseTripleCount() {
            ++tripleCount;
        }

        @Override
        public void close() throws IOException {
            tripleStream.finish();
            Closer.close(tripleOutputStream, LOGGER);
            Closer.close(dataOutputStream, LOGGER);
        }
    }
}
