package org.aksw.simba.squirrel.worker;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.util.List;

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
     * {@link org.aksw.simba.squirrel.frontier.Frontier} that he is still alive.
     *
     * @return True iff the worker sends alive messages.
     */
    boolean sendsAliveMessages();

    public void setTerminateFlag(boolean terminateFlag);

}