package org.dice_research.squirrel.seed.generator.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;
import org.dice_research.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.dice_research.squirrel.data.uri.filter.UriFilterComposer;
import org.dice_research.squirrel.data.uri.filter.UriFilterConfigurator;
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
@SuppressWarnings("deprecation")
@Ignore
public class CkanSeedGeneratorImplTest extends TestCase {
    private Frontier frontier;

    @Test
    public void setUp() {
    	IpAddressBasedQueue queue = new InMemoryQueue();
        //frontier = new FrontierImpl(new NormalizerImpl() , new InMemoryKnownUriFilter(false, -1), queue,null);

        
        List<UriGenerator> uriGenerators = new ArrayList<UriGenerator>();
        uriGenerators.add(new DomainBasedUriGenerator());
        uriGenerators.add(new WellKnownPathUriGenerator());
        
        List<String> sessionIDs = new ArrayList<String>();
        Map<String, Integer> mapDefaultPort = new HashedMap<String, Integer>();
        
        UriFilterComposer relationalUriFilter = new UriFilterConfigurator(new InMemoryKnownUriFilter(false, -1),"");
        
        frontier = new FrontierImpl(new NormalizerImpl(sessionIDs,mapDefaultPort), relationalUriFilter, queue,uriGenerators);
        CkanSeedGeneratorImpl ckanSeedGenerator = new CkanSeedGeneratorImpl(frontier);
        ckanSeedGenerator.toString();
    }


}
