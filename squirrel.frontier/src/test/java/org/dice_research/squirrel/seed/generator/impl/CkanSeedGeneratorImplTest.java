package org.dice_research.squirrel.seed.generator.impl;


import org.dice_research.squirrel.data.uri.filter.InMemoryKnownUriFilter;

import org.dice_research.squirrel.data.uri.norm.NormalizerImpl;
import org.dice_research.squirrel.frontier.Frontier;
import org.dice_research.squirrel.frontier.impl.FrontierImpl;
import org.dice_research.squirrel.queue.InMemoryQueue;
import org.dice_research.squirrel.queue.IpAddressBasedQueue;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * TODO Has to be reworked.
 * 
 * Created by ivan on 04.03.16.
 */
public class CkanSeedGeneratorImplTest extends TestCase {
    private CkanSeedGeneratorImpl ckanSeedGenerator;
    private IpAddressBasedQueue queue;
    private Frontier frontier;


 //   private OutDatedUriRetreiver outDatedUriRetreiver = SparqlhostConnector.create("http://localhost:8890/sparql-auth", "dba", "pw123");

    public void setUp() {
        queue = new InMemoryQueue();
        frontier = new FrontierImpl(new NormalizerImpl() , new InMemoryKnownUriFilter(false, -1), queue,null);

        ckanSeedGenerator = new CkanSeedGeneratorImpl(frontier);
    }

    /**
     * Get list of URIs to crawl from datahub.io
     * Should be more than 100 URIs
     * Actual size is a bit more than 5000 URIs
     */
    @Ignore
    @Test
    public void testGetSeed() {
        //List<CrawleableUri> seedUris = ckanSeedGenerator.getSeed();
        //assertTrue(seedUris.size() > 100);
    }
}
