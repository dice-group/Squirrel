package org.dice_research.squirrel.worker.standalone;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.frontier.Frontier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Implementation of {@link Frontier} interface to print all the results to the command line.
 */
public class FrontierCommandLine implements Frontier {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierCommandLine.class);
    @Override
    public List<CrawleableUri> getNextUris() {
        return null;
    }

    @Override
    public void addNewUri(CrawleableUri uri) {
        LOGGER.info("New uri - " + uri.getUri().toString());
    }

    @Override
    public void addNewUris(List<CrawleableUri> newUris) {
        for (CrawleableUri uri: newUris)
            addNewUri(uri);
    }

    @Override
    public void crawlingDone(List<CrawleableUri> uris) {
        LOGGER.info("Crawling has been successfully complete.");
    }

    @Override
    public int getNumberOfPendingUris() {
        return 0;
    }

    @Override
    public boolean doesRecrawling() {
        return false;
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("Closing the frontier");
    }
}
