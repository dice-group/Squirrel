package org.aksw.simba.squirrel.seed.generator;

import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public interface SeedGeneratorZeroMQ extends Runnable {	
    /**
     * Send a list of CrawleableUri to frontier
     * Socket for frontier is specified in the constructor
     */
    public void sendStaticSeed();

    /**
     * Return an array of CrawleableUri defined inside
     * SeedGeneratorZeroMQ class
     */
    public List<CrawleableUri> getStaticSeed();
}
