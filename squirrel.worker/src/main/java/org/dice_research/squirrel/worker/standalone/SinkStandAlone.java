package org.dice_research.squirrel.worker.standalone;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Implementation of {@link Sink} interface and prints all the results to the command line.
 */
public class SinkStandAlone implements Sink {
    private static final Logger LOGGER = LoggerFactory.getLogger(SinkStandAlone.class);

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {
        LOGGER.info("Received uri - " + uri.getUri().toString());
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        LOGGER.info("Received triple - " + triple.toString());
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        LOGGER.debug("Opening sink for uri.");
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        LOGGER.debug("Closing sink for uri.");
    }
}
