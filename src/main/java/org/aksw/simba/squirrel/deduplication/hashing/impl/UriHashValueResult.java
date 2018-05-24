package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.io.Serializable;
import java.util.List;

public class UriHashValueResult implements Serializable {

    public List<CrawleableUri> uris;

    public UriHashValueResult(List<CrawleableUri> uris) {
        this.uris = uris;
    }
}
