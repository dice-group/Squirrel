package org.aksw.simba.squirrel.seed.generator.impl;

import junit.framework.TestCase;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.BlacklistUriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;

import java.util.List;

/**
 * Created by ivan on 04.03.16.
 */
public class CkanSeedGeneratorImplTest extends TestCase {
    private CkanSeedGeneratorImpl ckanSeedGenerator;
    private IpAddressBasedQueue queue;
    private Frontier frontier;

    public void setUp() {
        queue = new InMemoryQueue();
        frontier = new FrontierImpl(new BlacklistUriFilter(), queue);
        ckanSeedGenerator = new CkanSeedGeneratorImpl(frontier);
    }

    /**
     * Get list of URIs to crawl from datahub.io
     * Should be more than 100 URIs
     * Actual size is a bit more than 5000 URIs
     */
    public void testGetSeed() {
        List<CrawleableUri> seedUris = ckanSeedGenerator.getSeed();
        assertTrue(seedUris.size() > 100);
    }
}
