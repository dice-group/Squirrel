package org.dice_research.squirrel.queue.scorebasedfilter;

import org.dice_research.squirrel.MongoDBScoreBasedTest;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.queue.scorecalculator.UriScoreCalculator;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UriKeywiseFilterTest extends MongoDBScoreBasedTest {

    @Test
    public void testFilterUrisKeywise() throws URISyntaxException {
        UriScoreCalculator scoreCalculator = new UriScoreCalculator(queryExecFactory);
        UriKeywiseFilter uriKeywiseFilter = new UriKeywiseFilter(scoreCalculator);

        Map<String, List<CrawleableUri>> keyWiseUris = new HashMap<>();
        List<CrawleableUri> dbpediaUris = new ArrayList<>();
        CrawleableUri uri1 = new CrawleableUri(new URI("http://dbpedia.org/resource/Berlin"));
        CrawleableUri uri2 = new CrawleableUri(new URI("http://dbpedia.org/resource/Bangalore"));
        CrawleableUri uri3 = new CrawleableUri(new URI("http://dbpedia.org/resource/New_York_City"));
        CrawleableUri uri4 = new CrawleableUri(new URI("http://dbpedia.org/resource/Moscow"));
        dbpediaUris.add(uri1);
        dbpediaUris.add(uri2);
        dbpediaUris.add(uri3);
        dbpediaUris.add(uri4);
        keyWiseUris.put("dbpedia.org", dbpediaUris);

        Assert.assertEquals(4, uriKeywiseFilter.filterUrisKeywise(keyWiseUris, 2, .001f).size());
        Assert.assertEquals(0, uriKeywiseFilter.filterUrisKeywise(keyWiseUris, 2, .8f).size());

        CrawleableUri uri5 = new CrawleableUri(new URI("https://www.lonelyplanet.com/germany/paderborn"));
        CrawleableUri uri6 = new CrawleableUri(new URI("https://www.lonelyplanet.com/germany/north-rhine-westphalia/dortmund"));
        CrawleableUri uri7 = new CrawleableUri(new URI("https://www.lonelyplanet.com/england/london"));
        CrawleableUri uri8 = new CrawleableUri(new URI("https://www.lonelyplanet.com/france/paris"));
        List<CrawleableUri> lonelyPlanetUris = new ArrayList<>();
        lonelyPlanetUris.add(uri5);
        lonelyPlanetUris.add(uri6);
        lonelyPlanetUris.add(uri7);
        lonelyPlanetUris.add(uri8);
        keyWiseUris.put("www.lonelyplanet.com", lonelyPlanetUris);
        Map<CrawleableUri, Float> filteredUris = uriKeywiseFilter.filterUrisKeywise(keyWiseUris, 2, .2f);

        Assert.assertEquals(4, filteredUris.size());
        Assert.assertTrue(filteredUris.containsKey(uri5));
        Assert.assertTrue(filteredUris.containsKey(uri6));
        Assert.assertTrue(filteredUris.containsKey(uri7));
        Assert.assertTrue(filteredUris.containsKey(uri8));
    }
}
