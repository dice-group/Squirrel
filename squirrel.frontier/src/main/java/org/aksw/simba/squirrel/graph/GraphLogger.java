package org.aksw.simba.squirrel.graph;

import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

/**
 * A GraphLogger is used to log the graph structure that is created by crawling
 * new URIs given a set of URIs. Not that the GraphLogger might only log the
 * domains and their connections to each other.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface GraphLogger {

    /**
     * This method logs the connection between the crawled URIs and the new
     * URIs.
     * 
     * @param crawledUris
     *            URIs that have been crawled
     * @param newUris
     *            URIs that have been found during crawling the other list of
     *            URIs
     */
    public void log(List<CrawleableUri> crawledUris, List<CrawleableUri> newUris);
}
