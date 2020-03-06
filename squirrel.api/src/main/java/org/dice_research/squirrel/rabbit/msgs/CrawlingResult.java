package org.dice_research.squirrel.rabbit.msgs;

import java.io.Serializable;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * This simple structure represents the result of a crawling process.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class CrawlingResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * List of URIs that have been crawled.
     */
    public final List<CrawleableUri> uris;
    /**
     * ID of the worker which has crawled the URIs.
     */
    public final String idOfWorker;

    /**
     * Constructor.
     * 
     * @param uris list of URIs that have been crawled
     * @param idOfWorker ID of the worker which has crawled the URIs
     */
    public CrawlingResult(List<CrawleableUri> uris, String idOfWorker) {
        this.uris = uris;
        this.idOfWorker = idOfWorker;
    }

    /**
     * Constructor.
     * 
     * @param uris list of URIs that have been crawled
     */
    public CrawlingResult(List<CrawleableUri> uris) {
        this(uris, null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + uris.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CrawlingResult other = (CrawlingResult) obj;
        if (uris == null) {
            return other.uris == null;
        } else return uris.equals(other.uris);

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CrawlingResult [uris=");
        uris.toString();
        return builder.toString();
    }
}