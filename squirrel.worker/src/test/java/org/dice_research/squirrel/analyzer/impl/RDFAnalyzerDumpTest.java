package org.dice_research.squirrel.analyzer.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.collect.SimpleUriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.sink.impl.mem.InMemorySink;
import org.junit.Assert;
import org.junit.Test;

public class RDFAnalyzerDumpTest {
    private String resourceName = "rdf_analyzer/dump";

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
        // Analyze the file
        analyzer.analyze(curi, dataFile, sink);
        // Check the result

        // Close the sink and collector
        sink.closeSinkForUri(curi);
        collector.closeSinkForUri(curi);
    }
}
