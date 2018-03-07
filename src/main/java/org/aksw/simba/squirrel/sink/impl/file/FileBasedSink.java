package org.aksw.simba.squirrel.sink.impl.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.commons.collections15.MapUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.system.StreamOps;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFWriter;
import org.apache.log4j.lf5.util.StreamUtils;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBasedSink implements Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedSink.class);

    private static final byte PRE_URI[] = "<".getBytes(Constants.DEFAULT_CHARSET);
    private static final byte POST_URI[] = ">".getBytes(Constants.DEFAULT_CHARSET);
    private static final byte SEPERATOR[] = " ".getBytes(Constants.DEFAULT_CHARSET);
    private static final byte END_OF_QUAD[] = " .\n".getBytes(Constants.DEFAULT_CHARSET);

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
    protected Map<String, OutputStream> streamMapping = MapUtils.synchronizedMap(new HashMap<String, OutputStream>());
    
    /**
     * Jena Language to use to write a model to file via the jena API
     */
    protected Lang lang;
    protected String fileExtension;
    

    public FileBasedSink(File outputDirectory, boolean useCompression, Lang lang) {
        this.outputDirectory = outputDirectory;
        this.useCompression = useCompression;
        this.lang = lang;
        if(lang != null){
            fileExtension = lang.getFileExtensions().get(0);
        }
        openSinkForUri(new CrawleableUri(Constants.DEFAULT_META_DATA_GRAPH_URI));
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
    	String uriString = uri.getUri().toString();
    	if(uri.getData().containsKey(Constants.URI_CRAWLING_ACTIVITY_URI)) {
    		uriString = (String) uri.getData().get(Constants.URI_CRAWLING_ACTIVITY_URI);
    	}
        
        OutputStream outputStream = getStream(uri);
        if (outputStream != null) {
            try {
                outputStream.write(PRE_URI);
                outputStream.write(triple.getSubject().toString().getBytes(Constants.DEFAULT_CHARSET));
                outputStream.write(POST_URI);
                outputStream.write(SEPERATOR);
                outputStream.write(PRE_URI);
                outputStream.write(triple.getPredicate().toString().getBytes(Constants.DEFAULT_CHARSET));
                outputStream.write(POST_URI);
                outputStream.write(SEPERATOR);
                if (triple.getObject().isURI()) {
                    outputStream.write(PRE_URI);
                    outputStream.write(triple.getObject().toString().getBytes(Constants.DEFAULT_CHARSET));
                    outputStream.write(POST_URI);
                } else {
                    outputStream.write(triple.getObject().toString().getBytes(Constants.DEFAULT_CHARSET));
                }
                outputStream.write(SEPERATOR);
                outputStream.write(PRE_URI);
                outputStream.write(uriString.getBytes(Constants.DEFAULT_CHARSET));
                outputStream.write(POST_URI);
                outputStream.write(END_OF_QUAD);
            } catch (Exception e) {
                LOGGER.error("Exception while writing the triple \"" + triple.toString() + "\" from the URI \""
                        + uriString + "\". Ignoring it.", e);
            }
        }
    }
    
    @Override
    public void addModel(CrawleableUri uri, Model model)
    {
        OutputStream os = getStream(uri);
        if (os != null)
        { 
            StreamRDF rdfWriter = StreamRDFWriter.getWriterStream(os, lang);
            StreamOps.graphToStream(model.getGraph(), rdfWriter);
        }else{
            LOGGER.error("Error opening OutputStream for URI {}. Could not write model to file.", uri.getUri().toString());
        }
    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {
        OutputStream outputStream = getStream(uri);
        if (outputStream != null) {
            try {
                StreamUtils.copy(stream, outputStream);
            } catch (Exception e) {
                LOGGER.error("Exception while writing unstructed data to file.", e);
            }
        }
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        // Add the URI but do not open a stream
        streamMapping.put(uri.getUri().toString(), null);
    }

    private OutputStream getStream(CrawleableUri uri) {
                
        String uriString = uri.getUri().toString();      
        
        //if the given uriString ends with the metadata suffix we want to use the Jena API to store the model to file and not use compression
        boolean isMcloudMetadata = uriString.endsWith(Constants.MCLOUD_METADATA_URI_SUFFIX);
        boolean compress = useCompression && !isMcloudMetadata;
        
        if (streamMapping.containsKey(uriString)) {
            OutputStream outputStream = streamMapping.get(uriString);
            if (outputStream == null) {
                try {
                    outputStream = new FileOutputStream(outputDirectory.getAbsolutePath() + File.separator
                            + generateFileName(uriString, compress, isMcloudMetadata, fileExtension));
                    if (compress) {
                        outputStream = new GZIPOutputStream(outputStream);
                    }
                    streamMapping.put(uriString, outputStream);
                } catch (IOException e) {
                    LOGGER.error("Exception while trying to open file for \"" + uriString + "\" to use as sink.", e);
                }
            }
            return outputStream;
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
        if (streamMapping.containsKey(uriString)) {
            IOUtils.closeQuietly(streamMapping.get(uriString));
            streamMapping.remove(uriString);
        } else {
            LOGGER.error("Should close the sink for the URI \"" + uriString + "\" but couldn't find it.");
        }
    }
    
    /**
     * @param uri to generate the filename for
     * @param useCompression whether to compress the file into a gzip archive
     * @param isMetadataFile true if given file stores a metadata dataset of a mCloud resource. In this case compression should not be used.
     * @param fileExtension
     * @return the filename as String
     */
    public static String generateFileName(String uri, boolean useCompression, boolean isMetadataFile, String fileExtension) {
        
        String filename = UriUtils.generateFileName(uri, useCompression);
        return isMetadataFile ? "METADATA_" + filename + "." + fileExtension : filename;
        
    }

}
