package org.dice_research.squirrel.fetcher;

import java.io.Closeable;
import java.io.File;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.ftp.FTPFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Fetcher extends Closeable {
    public static final Logger LOGGER = LoggerFactory.getLogger(FTPFetcher.class);

    /**
     * Fetches a stream of data from the given URI, stores it and returns a {@link File}
     * object pointing to the stored data. If an error occurs, {@code null} is
     * returned.
     *
     * @param uri The URI from which data should be fetched.
     * @return A {@link File} object pointing to the downloaded data or {@code null} if an error occurred.
     */
    public File fetch(CrawleableUri uri) throws RuntimeException;
    
    
    
    /**
     * Pauses the thread according to the delay from robots.txt
     *
     * @param uri The URI from which data should be delayed.
     * @param delay the amount of delay (in ms).
     */
    public default void wait(CrawleableUri uri,int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            LOGGER.warn("Delay before crawling \"" + uri.getUri().toString() + "\" interrupted.", e);

        }
    }

}
