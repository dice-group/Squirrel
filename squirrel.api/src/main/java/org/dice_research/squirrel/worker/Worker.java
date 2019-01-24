package org.dice_research.squirrel.worker;

import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

public interface Worker extends Runnable {

    /**
     * Crawls the given URIs and sends URIs that have been found while crawling to
     * the frontier.
     *
     * @param uris
     *            the URIs that should be crawled
     */
    void crawl(List<CrawleableUri> uris);

    /**
     * Crawls the given URI and adds new URIs that have been found while crawling to
     * the given list of new URIs.
     *
     * @param uri
     *            the URI that should be crawled
     */
    void performCrawling(CrawleableUri uri);

    /**
     * Gives the unique URI of the worker.
     * 
     * @return The URI of the worker.
     */
    String getUri();

    /**
     * Indicates whether the worker sends alive messages in order to convince the
     * {@link org.dice_research.squirrel.frontier.Frontier} that he is still alive.
     *
     * @return True iff the worker sends alive messages.
     */
    boolean sendsAliveMessages();
    
    /**
     * Gives the unique id of the worker.
     * @return The id of the worker.
     */
    int getId();

    public void setTerminateFlag(boolean terminateFlag);

}