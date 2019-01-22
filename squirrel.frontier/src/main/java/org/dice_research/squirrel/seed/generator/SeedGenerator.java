package org.dice_research.squirrel.seed.generator;

import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

public interface SeedGenerator extends Runnable {
    /**
     * Return a List of CrawleableUrl
     */
    public List<CrawleableUri> getSeed();
}
