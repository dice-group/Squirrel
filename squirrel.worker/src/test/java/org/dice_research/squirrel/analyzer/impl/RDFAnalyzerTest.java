package org.dice_research.squirrel.analyzer.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.collect.SimpleUriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.sink.impl.mem.InMemorySink;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RDFAnalyzerTest {
    private long startTime;
    private long endTime;
    private static long totalTime;
    private String resourceName;
    private int expectedNumberOfTriples;

    @Rule
    public Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void succeeded(long ns, Description desc) {
            totalTime += (endTime - startTime);
        }
    };

    @AfterClass
    public static void afterClass() {
        System.err.println(String.format("RDFAnalyzerTest total time: %d", totalTime));
    }

    

    public RDFAnalyzerTest(String resourceName, int expectedNumberOfTriples) {
        this.resourceName = resourceName;
        this.expectedNumberOfTriples = expectedNumberOfTriples;
    }

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(new Object[][] {
                { "rdf_analyzer/new_york/new_york_jsonld", 8603 },
                { "rdf_analyzer/new_york/new_york_n3", 8603 },
                { "rdf_analyzer/new_york/new_york_rdf", 8603 },
                { "rdf_analyzer/new_york/new_york_rdfjson", 8603 },
                { "rdf_analyzer/new_york/new_york_ttl", 8603 },
                { "rdf_analyzer/new_york/new_york_turtle", 8603 },
                { "rdf_analyzer/genders_en/genders_en_jsonld", 8408 },
                { "rdf_analyzer/genders_en/genders_en_rdf", 8408 },
                { "rdf_analyzer/genders_en/genders_en_rdfjson", 8408 },
                { "rdf_analyzer/genders_en/genders_en_tql", 8410 },
                { "rdf_analyzer/genders_en/genders_en_ttl", 8408 },
                { "rdf_analyzer/genders_en/genders_en_turtle", 8408 },
                { "rdf_analyzer/trig_example", 15 },
        });
    }

    @Test
    public void test() throws URISyntaxException, IOException {
        // Initialize the analyzer and the necessary classes
        Serializer serializer = new GzipJavaUriSerializer();
        SimpleUriCollector collector = new SimpleUriCollector(serializer);
        Sink sink = new InMemorySink();
        Analyzer analyzer = new RDFAnalyzer(collector);

        // Prepare the file the analyzer should read
        CrawleableUri curi = new CrawleableUri(new URI("http://aksw.test.org/test"));
        File dataFile = File.createTempFile("analyzer-test-", "");
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourceName);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(dataFile))) {
            IOUtils.copy(in, out);
        }

        // Open the sink and collector
        sink.openSinkForUri(curi);
        collector.openSinkForUri(curi);

        // Need to do this even if other things are moved to Before/After due how Stopwatch works.
        startTime = stopwatch.runtime(TimeUnit.MILLISECONDS);

        // Analyze the file
        analyzer.analyze(curi, dataFile, sink);

        endTime = stopwatch.runtime(TimeUnit.MILLISECONDS);

        // Check the result and close the sink and collector
        Assert.assertEquals("Number of triples in " + resourceName, expectedNumberOfTriples, collector.getSize());
        Assert.assertTrue("Failed parse attempts for " + resourceName + ": " + ((RDFAnalyzer)analyzer).failedParseAttempts + " <= 2",
                ((RDFAnalyzer)analyzer).failedParseAttempts <= 2);
        sink.closeSinkForUri(curi);
        collector.closeSinkForUri(curi);
    }
}