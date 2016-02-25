package org.aksw.simba.squirrel.seed.generator;

import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public interface SeedGenerator extends Runnable {
    /**
     * Return a List of CrawleableUrl
     */
    public List<CrawleableUri> getSeed();
}
