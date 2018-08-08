package org.aksw.simba.squirrel.graph.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TabSeparatedGraphLoggerTest {

    private static final String LINEBREAK = String.format("%n");

    @Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { new CrawleableUri[] { new CrawleableUri(new URI("http://example.org/test1")) },
                new CrawleableUri[] { new CrawleableUri(new URI("http://example.org/test2")) },
                new String[] { "\"example.org\"\t\"example.org\"" + LINEBREAK } });
        testConfigs.add(
                new Object[] { new CrawleableUri[] { new CrawleableUri(new URI("http://example.org/resource/test1")) },
                        new CrawleableUri[] { new CrawleableUri(new URI("http://example.org/test2")) },
                        new String[] { "\"example.org/resource\"\t\"example.org\"" + LINEBREAK } });
        testConfigs.add(new Object[] { new CrawleableUri[] { new CrawleableUri(new URI("http://example.org/test1")) },
                new CrawleableUri[] { new CrawleableUri(new URI("http://example.org/test2")),
                        new CrawleableUri(new URI("http://example2.org/test1")),
                        new CrawleableUri(new URI("http://example2.org/resource/test1")) },
                new String[] { "\"example.org\"\t\"example.org|example2.org|example2.org/resource\"" + LINEBREAK,
                        "\"example.org\"\t\"example.org|example2.org/resource|example2.org\"" + LINEBREAK,
                        "\"example.org\"\t\"example2.org|example.org|example2.org/resource\"" + LINEBREAK,
                        "\"example.org\"\t\"example2.org|example2.org/resource|example.org\"" + LINEBREAK,
                        "\"example.org\"\t\"example2.org/resource|example2.org|example.org\"" + LINEBREAK,
                        "\"example.org\"\t\"example2.org/resource|example.org|example2.org\"" + LINEBREAK } });
        return testConfigs;
    }

    private CrawleableUri cralwedUri[];
    private CrawleableUri newUris[];
    private String expectedContents[];

    public TabSeparatedGraphLoggerTest(CrawleableUri[] cralwedUri, CrawleableUri[] newUris, String expectedContents[]) {
        this.cralwedUri = cralwedUri;
        this.newUris = newUris;
        this.expectedContents = expectedContents;
    }

    @Test
    public void test() throws IOException {
        File tmp = File.createTempFile("test_", ".tmp");

        TabSeparatedGraphLogger logger = TabSeparatedGraphLogger.create(tmp);
        logger.log(Arrays.asList(cralwedUri), Arrays.asList(newUris));
        logger.close();

        String content = FileUtils.readFileToString(tmp);
        Set<String> possibleExpectedResults = new HashSet<String>(Arrays.asList(expectedContents));
        Assert.assertTrue("\"" + content + "\" couldn't be found inside the list of possible solutions: "
                + possibleExpectedResults.toString(), possibleExpectedResults.contains(content));
    }
}
