package org.dice_research.squirrel.queue.uriscorecalculator;

import org.dice_research.squirrel.MongoDBScoreBasedTest;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.queue.scorecalculator.UriDuplicityScoreCalculator;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class UriScoreCalculatorTest extends MongoDBScoreBasedTest {

    @Test
    public void testURIScore() throws URISyntaxException {
        UriDuplicityScoreCalculator uriScoreCalculator = new UriDuplicityScoreCalculator(queryExecFactory);
        float score1 = uriScoreCalculator.getURIScore(new CrawleableUri(new URI("http://dbpedia.org/resource/Berlin")));
        float score2 = uriScoreCalculator.getURIScore(new CrawleableUri(new URI("http://dbpedia.org/resource/Bangalore")));
        Assert.assertEquals(.1f, score1, .0001);
        Assert.assertEquals(.125f, score2, .0001);
    }
}
