package org.dice_research.squirrel.data.uri.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RegexBasedFilterTest {

    protected boolean uriMatchesPattern;
    protected boolean lowerCaseComparison;
    protected CrawleableUri uri;
    protected String[] patterns;

    public RegexBasedFilterTest(boolean uriMatchesPattern, boolean lowerCaseComparison, CrawleableUri uri,
            String[] patterns) {
        super();
        this.uriMatchesPattern = uriMatchesPattern;
        this.lowerCaseComparison = lowerCaseComparison;
        this.uri = uri;
        this.patterns = patterns;
    }

    @Test
    public void testWhiteList() {
        RegexBasedWhiteListFilter filter = new RegexBasedWhiteListFilter(
                AbstractRegexBasedFilter.parsePatterns(Arrays.asList(patterns)), lowerCaseComparison);
        if (uriMatchesPattern) {
            Assert.assertTrue(filter.isUriGood(uri));
        } else {
            Assert.assertFalse(filter.isUriGood(uri));
        }
    }

    @Test
    public void testBlackList() {
        RegexBasedBlackListFilter filter = new RegexBasedBlackListFilter(
                AbstractRegexBasedFilter.parsePatterns(Arrays.asList(patterns)), lowerCaseComparison);
        if (uriMatchesPattern) {
            Assert.assertFalse(filter.isUriGood(uri));
        } else {
            Assert.assertTrue(filter.isUriGood(uri));
        }
    }

    @Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();

        // exact String (mis)match
        testConfigs.add(new Object[] { true, true, new CrawleableUri(new URI("http://example.org/test1")),
                new String[] { "http://example\\.org/test1" } });
        testConfigs.add(new Object[] { true, false, new CrawleableUri(new URI("http://example.org/test1")),
                new String[] { "http://example\\.org/test1" } });
        testConfigs.add(new Object[] { false, true, new CrawleableUri(new URI("http://example.org/test1")),
                new String[] { "http://example\\.org/test2" } });
        testConfigs.add(new Object[] { false, false, new CrawleableUri(new URI("http://example.org/test1")),
                new String[] { "http://example\\.org/test2" } });
        testConfigs.add(new Object[] { false, true, new CrawleableUri(new URI("http://dbpedia.org/resource/New_York")),
                new String[] { "http://dbpedia.org/resource/New_York" } });
        testConfigs.add(new Object[] { true, false, new CrawleableUri(new URI("http://dbpedia.org/resource/New_York")),
                new String[] { "http://dbpedia.org/resource/New_York" } });

        // prefix match
        testConfigs.add(new Object[] { true, true, new CrawleableUri(new URI("http://example.org/test123")),
                new String[] { "http://example\\.org/test1" } });
        testConfigs.add(new Object[] { true, false, new CrawleableUri(new URI("http://example.org/test123")),
                new String[] { "http://example\\.org/test1" } });

        // star pattern
        testConfigs.add(new Object[] { true, true, new CrawleableUri(new URI("http://example.org/test1")),
                new String[] { "http://example\\.org/.*" } });
        testConfigs.add(new Object[] { true, false, new CrawleableUri(new URI("http://example.org/test1")),
                new String[] { "http://example\\.org/.*" } });

        // With start and end position
        testConfigs.add(new Object[] { false, true, new CrawleableUri(new URI("http://example.org/test123")),
                new String[] { "^http://example\\.org/test1$" } });
        testConfigs.add(new Object[] { false, false, new CrawleableUri(new URI("http://example.org/test123")),
                new String[] { "^http://example\\.org/test1$" } });

        return testConfigs;

    }
}
