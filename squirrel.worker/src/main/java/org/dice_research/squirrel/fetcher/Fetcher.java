package org.dice_research.squirrel.fetcher;

import java.io.Closeable;
import java.io.File;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.delay.Delayer;
import org.dice_research.squirrel.fetcher.delay.DummyDelayer;

/**
 * Interface of a class that fetches the data of a given {@link CrawleableUri}
 * instance.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface Fetcher extends Closeable {

    /**
     * Fetches a stream of data from the given URI, stores it and returns a
     * {@link File} object pointing to the stored data. If an error occurs,
     * {@code null} is returned.
     *
     * @param uri The URI from which data should be fetched.
     * @return A {@link File} object pointing to the downloaded data or {@code null}
     *         if an error occurred.
     */
    public default File fetch(CrawleableUri uri) throws RuntimeException {
        return fetch(uri, DummyDelayer.get());
    }

    /**
     * Fetches a stream of data from the given URI by following the delay
     * implemented by the given {@link Delayer}, stores it and returns a
     * {@link File} object pointing to the stored data. If an error occurs,
     * {@code null} is returned.
     *
     * @param uri The URI from which data should be fetched.
     * @return A {@link File} object pointing to the downloaded data or {@code null}
     *         if an error occurred.
     */
    public File fetch(CrawleableUri uri, Delayer delayer);

}
