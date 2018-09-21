package org.dice_research.squirrel.seed.generator.impl;

import junit.framework.TestCase;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.dice_research.squirrel.frontier.Frontier;
import org.dice_research.squirrel.frontier.impl.FrontierImpl;
import org.dice_research.squirrel.queue.InMemoryQueue;
import org.dice_research.squirrel.queue.IpAddressBasedQueue;
import org.dice_research.squirrel.seed.generator.impl.CkanSeedGeneratorImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * TODO Has to be reworked.
 * 
 * Created by ivan on 04.03.16.
 */
public class CkanSeedGeneratorImplTest extends TestCase {
    private CkanSeedGeneratorImpl ckanSeedGenerator;
    private IpAddressBasedQueue queue;
    private Frontier frontier;

    public void setUp() {
        queue = new InMemoryQueue();
        frontier = new FrontierImpl(new InMemoryKnownUriFilter(false, -1), queue);
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
