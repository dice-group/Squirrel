package org.dice_research.squirrel.seed.generator.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;
import org.dice_research.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.dice_research.squirrel.data.uri.filter.UriFilterConfigurator;
import org.dice_research.squirrel.data.uri.filter.UriFilterComposer;
import org.dice_research.squirrel.data.uri.norm.DomainBasedUriGenerator;
import org.dice_research.squirrel.data.uri.norm.NormalizerImpl;
import org.dice_research.squirrel.data.uri.norm.UriGenerator;
import org.dice_research.squirrel.data.uri.norm.WellKnownPathUriGenerator;
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

    public void setUp() {
        queue = new InMemoryQueue();
        
        List<UriGenerator> uriGenerators = new ArrayList<UriGenerator>();
        uriGenerators.add(new DomainBasedUriGenerator());
        uriGenerators.add(new WellKnownPathUriGenerator());
        
        List<String> sessionIDs = new ArrayList<String>();
        Map<String, Integer> mapDefaultPort = new HashedMap<String, Integer>();
        
        UriFilterComposer relationalUriFilter = new UriFilterConfigurator(new InMemoryKnownUriFilter(false, -1),"");
        
        frontier = new FrontierImpl(new NormalizerImpl(sessionIDs,mapDefaultPort), relationalUriFilter, queue,uriGenerators);
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
