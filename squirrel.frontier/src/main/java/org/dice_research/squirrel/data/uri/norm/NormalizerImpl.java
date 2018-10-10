package org.dice_research.squirrel.data.uri.norm;

import org.dice_research.squirrel.data.uri.CrawleableUri;

public class NormalizerImpl implements UriNormalizer {

    @Override
    public CrawleableUri normalize(CrawleableUri uri) {
        // Copy Normalization from https://github.com/crawler-commons/crawler-commons/blob/master/src/main/java/crawlercommons/filters/basic/BasicURLNormalizer.java
        // OR use URI.normalize()
        
        // Check whether the query part of a URI has to be sorted
        
        // Filter attributes of the URI
        return uri;
    }

}
