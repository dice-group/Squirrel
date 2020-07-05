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
        UriKeywiseFilter uriKeywiseFilter = new UriKeywiseFilter(scoreCalculator, .2f, 3);

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
        keyWiseUris.put("dbpedia.org", dbpediaUris);
        Map<String, List<CrawleableUri>> filteredUris = uriKeywiseFilter.filterUrisKeywise(keyWiseUris);
        Assert.assertEquals(1, filteredUris.size());
        Assert.assertTrue(filteredUris.containsKey("www.lonelyplanet.com"));
        Assert.assertEquals(4, filteredUris.get("www.lonelyplanet.com").size());
        Assert.assertTrue(filteredUris.get("www.lonelyplanet.com").contains(uri5));
        Assert.assertTrue(filteredUris.get("www.lonelyplanet.com").contains(uri6));
        Assert.assertTrue(filteredUris.get("www.lonelyplanet.com").contains(uri7));
        Assert.assertTrue(filteredUris.get("www.lonelyplanet.com").contains(uri8));
    }
}
