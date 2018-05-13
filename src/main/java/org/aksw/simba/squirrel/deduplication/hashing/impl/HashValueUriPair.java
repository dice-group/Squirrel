package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.deduplication.hashing.HashValue;

import java.io.Serializable;

public class HashValueUriPair implements Serializable {

    public HashValue hashValue;
    public CrawleableUri uri;

    public HashValueUriPair(HashValue hashValue, CrawleableUri uri) {
        this.hashValue = hashValue;
        this.uri = uri;
    }

    public HashValueUriPair() {
    }


}
