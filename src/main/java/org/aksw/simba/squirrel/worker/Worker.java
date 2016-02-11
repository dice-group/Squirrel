package org.aksw.simba.squirrel.worker;

import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public interface Worker extends Runnable {

    /**
     * Crawls the given URIs and sends URIs that have been found while crawling
     * to the frontier.
     * 
     * @param uris
     *            the URIs that should be crawled
     */
    public void crawl(List<CrawleableUri> uris);

    /**
     * Crawls the given URI and adds new URIs that have been found while
     * crawling to the given list of new URIs.
     * 
     * @param uri
     *            the URI that should be crawled
     * @param newUris
     *            the new URIs that have been extracted will be added to this
     *            list.
     */
    public void performCrawling(CrawleableUri uri, List<CrawleableUri> newUris);
}
