package org.aksw.simba.squirrel.seed.generator.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.seed.generator.SeedGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSeedGenerator implements SeedGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeedGenerator.class);
    private Frontier frontier;

    public AbstractSeedGenerator(Frontier frontier) {
        this.frontier = frontier;
    }

    public void run() {
        LOGGER.debug("Started Seed Generator...");
        LOGGER.debug("Sending static seed to frontier...");
        frontier.addNewUris(this.getSeed());
    }

    public List<CrawleableUri> createCrawleableUriList(String[] seedUris) {
        CrawleableUriFactoryImpl crawleableUriFactoryImpl = new CrawleableUriFactoryImpl();
        java.util.List<CrawleableUri> seed;
        seed = new ArrayList<CrawleableUri>();
        for(int i=0; i<seedUris.length; i++) {
            seed.add(crawleableUriFactoryImpl.create(seedUris[i]));
        }

        return seed;
    }
}
