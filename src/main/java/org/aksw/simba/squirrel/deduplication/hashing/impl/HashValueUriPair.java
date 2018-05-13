package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.deduplication.hashing.HashValue;

import java.io.Serializable;

/**
 * A tuple of {@link HashValue} and {@link CrawleableUri} used for data exchange between {@link org.aksw.simba.squirrel.worker.Worker} and
 * {@link org.aksw.simba.squirrel.frontier.Frontier}.
 */
public class HashValueUriPair implements Serializable {

    public HashValue hashValue;
    public CrawleableUri uri;

    /**
     * Constructor.
     *
     * @param hashValue Value for {@link #hashValue}.
     * @param uri       Value for {@link #uri}.
     */
    public HashValueUriPair(HashValue hashValue, CrawleableUri uri) {
        this.hashValue = hashValue;
        this.uri = uri;
    }

    /**
     * Standard constructor not setting any values for the attributes.
     */
    public HashValueUriPair() {
    }


}
