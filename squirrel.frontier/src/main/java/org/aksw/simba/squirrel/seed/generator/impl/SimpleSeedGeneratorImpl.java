package org.aksw.simba.squirrel.seed.generator.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ivan on 8/18/16.
 */
public class SimpleSeedGeneratorImpl extends AbstractSeedGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeedGenerator.class);

    public SimpleSeedGeneratorImpl(Frontier frontier) {
        super(frontier);
    }

    @Override
    public List<CrawleableUri> getSeed() {
        String[] seedUris = {
                //"http://danbri.org/foaf.rdf", //DUMP
                "http://dbpedia.org/resource/New_York" //DEREFEREANCEABLE
        };
        return this.createCrawleableUriList(seedUris);
    }
}
