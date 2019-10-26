package org.dice_research.squirrel.robots;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.robots.RobotsManager;
import org.dice_research.squirrel.robots.RobotsManagerImpl;
import org.dice_research.squirrel.simulation.AbstractServerMockUsingTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

@RunWith(Parameterized.class)
public class RobotsManagerImplTest extends AbstractServerMockUsingTest {

    /**
     * robots file of datahub.io
     */
    private static final String ROBOTS_TXT_FILE1 = "User-agent: Baiduspider\nDisallow: /\n\n"
            + "User-agent: *\nDisallow: /dataset/rate/\nDisallow: /revision/\n"
            + "Disallow: /dataset/*/history\nDisallow: /api/\n\nUser-Agent: *\nCrawl-Delay: 10";

    /**
     * parts of the robots file of dbpedia.org
     */
    private static final String ROBOTS_TXT_FILE2 = "# robots.txt\n\nUser-agent: *\n"
            + "Crawl-delay: 10\n# Directories\nDisallow: /includes/\n" + "# Files\nDisallow: /INSTALL.txt\n"
            + "# Paths (clean URLs)\nDisallow: /admin/\n" + "# Paths (no clean URLs)\nDisallow: /?q=admin/\n";

    /**
     * Example of a generated robots.txt file
     */
    private static final String ROBOTS_TXT_FILE3 = "User-agent: *\nCrawl-delay: 5\nDisallow: /dataset-0/resource-5";

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(new Object[][] { { ROBOTS_TXT_FILE1, new URI(HTTP_SERVER_ADDRESS + "/test"), true, 2000 },
                { ROBOTS_TXT_FILE1, new URI(HTTP_SERVER_ADDRESS + "/revision/"), false, 2000 },
                { ROBOTS_TXT_FILE1, new URI(HTTP_SERVER_ADDRESS + "/dataset/test"), true, 2000 },
                { ROBOTS_TXT_FILE1, new URI(HTTP_SERVER_ADDRESS + "/dataset/test/history"), false, 2000 },
                { ROBOTS_TXT_FILE2, new URI(HTTP_SERVER_ADDRESS + "/resource/Berlin"), true, 10000 },
                { ROBOTS_TXT_FILE2, new URI(HTTP_SERVER_ADDRESS + "/page/Berlin"), true, 10000 },
                { ROBOTS_TXT_FILE2, new URI(HTTP_SERVER_ADDRESS + "/includes/Berlin"), false, 10000 },
                { ROBOTS_TXT_FILE2, new URI(HTTP_SERVER_ADDRESS + "/INSTALL.txt"), false, 10000 },
                { ROBOTS_TXT_FILE2, new URI(HTTP_SERVER_ADDRESS + "/admin/pwd.txt"), false, 10000 },
                { ROBOTS_TXT_FILE2, new URI(HTTP_SERVER_ADDRESS + "/?q=admin/"), false, 10000 },
                { ROBOTS_TXT_FILE3, new URI(HTTP_SERVER_ADDRESS + "/dataset-0/resource-0"), true, 5000 },
                { ROBOTS_TXT_FILE3, new URI(HTTP_SERVER_ADDRESS + "/dataset-0/resource-5"), false, 5000 } });
    }

    private CrawleableUri uri;
    private boolean isCrawlingAllowed;
    private long expectedMinDelay;

    public RobotsManagerImplTest(String robotsFileContent, URI uri, boolean isCrawlingAllowed, long expectedMinDelay) {
        super(new RobotsFileContainer(robotsFileContent));
        this.uri = new CrawleableUri(uri);
        this.isCrawlingAllowed = isCrawlingAllowed;
        this.expectedMinDelay = expectedMinDelay;
    }

    @Test
    public void test() throws Throwable {
        RobotsManager manager = new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", "")));
        Assert.assertEquals(isCrawlingAllowed, manager.isUriCrawlable(uri));
        Assert.assertEquals(expectedMinDelay, manager.getMinWaitingTime(uri));

        Throwable serverError = ((RobotsFileContainer) container).getThrowable();
        if (serverError != null) {
            throw new AssertionError("The server encounted an error.", serverError);
        }
    }
}
