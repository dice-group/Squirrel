package org.aksw.simba.squirrel.sink;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

/**
 * This interface defines the basic functionality of all sinks, i.e., they can
 * be opened and closed for a given URI.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface SinkBase {

    /**
     * Opens the sink to process data for the given URI.
     * 
     * @param uri
     *            the URI for which data should be stored.
     */
    public void openSinkForUri(CrawleableUri uri);

    /**
     * Closes the resources necessary for storing the data of the given URI.
     * 
     * @param uri
     *            the URI for which data has been stored and for which the resources
     *            should be freed.
     */
    public void closeSinkForUri(CrawleableUri uri);

}
