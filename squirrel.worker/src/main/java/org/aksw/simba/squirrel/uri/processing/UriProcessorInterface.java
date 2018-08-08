package org.aksw.simba.squirrel.uri.processing;

import java.net.UnknownHostException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

/**
 * Interface for Uri Processor, defines main methods for processing.
 * 
 * @author Ivan Ermilov (iermilov@informatik.uni-leipzig.de)
 *
 */
public interface UriProcessorInterface {
    /**
     * Recognizes the type of {@link CrawleableUri}.
     * 
     * @param uri
     *            the {@link CrawleableUri} instance
     */
    public CrawleableUri recognizeUriType(CrawleableUri uri);

    /**
     * Recognizes the IP address of {@link CrawleableUri}.
     * 
     * @param uri
     *            the {@link CrawleableUri} instance
     * @throws UnknownHostException 
     */
    public CrawleableUri recognizeInetAddress(CrawleableUri uri) throws UnknownHostException;

}
