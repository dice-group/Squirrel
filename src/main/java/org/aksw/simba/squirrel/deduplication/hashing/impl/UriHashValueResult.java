package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.io.Serializable;
import java.util.List;

/**
 * Sent by the {@link org.aksw.simba.squirrel.components.DeduplicatorComponent} to the
 * {@link org.aksw.simba.squirrel.components.FrontierComponent} indicating that the hash values for the {@link #uris}
 * have been computed.
 */
public class UriHashValueResult implements Serializable {

    public List<CrawleableUri> uris;

    /**
     * Constructor.
     *
     * @param uris Value for {@link #uris}.
     */
    public UriHashValueResult(List<CrawleableUri> uris) {
        this.uris = uris;
    }
}
