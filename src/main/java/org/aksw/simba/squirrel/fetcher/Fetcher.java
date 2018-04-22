package org.aksw.simba.squirrel.fetcher;

import java.io.Closeable;
import java.io.File;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public interface Fetcher extends Closeable {

    /**
     * Fetches a stream of data from the given URI, stores it and returns a {@link File}
     * object pointing to the stored data. If an error occurs, {@code null} is
     * returned.
     * 
     * @param uri The URI from which data should be fetched.
     * @return A {@link File} object pointing to the downloaded data or {@code null} if an error occurred.
     */
    public File fetch(CrawleableUri uri);
    
}
