package org.aksw.simba.squirrel.frontier.impl.zeromq;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.robots.RobotsManager;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

public class SimpleZeroMQTest {

    public static final String FRONTIER_ADDRESS = "tcp://localhost:5501";
    // public static final String FRONTIER_ADDRESS = "ipc://localhost.ipc";

    public static void main(String[] args) throws UnknownHostException, URISyntaxException {
        IpAddressBasedQueue queue = new InMemoryQueue();
        queue.addUri(new CrawleableUri(new URI("http://localhost/test1_X"),
                InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 })));
        queue.addUri(new CrawleableUri(new URI("http://localhost/test2_X"),
                InetAddress.getByAddress(new byte[] { 127, 0, 0, 2 })));
        queue.addUri(new CrawleableUri(new URI("http://localhost/test2_XXXX"),
                InetAddress.getByAddress(new byte[] { 127, 0, 0, 2 })));
        queue.addUri(new CrawleableUri(new URI("http://localhost/test3_XXXX"),
                InetAddress.getByAddress(new byte[] { 127, 0, 0, 3 })));
        queue.addUri(new CrawleableUri(new URI("http://localhost/test3_X"),
                InetAddress.getByAddress(new byte[] { 127, 0, 0, 3 })));
        Frontier frontier = new FrontierImpl(new InMemoryKnownUriFilter(-1), queue);
        ZeroMQBasedFrontier frontierWrapper = ZeroMQBasedFrontier.create(frontier, FRONTIER_ADDRESS);
        Worker worker = new TestWorker(ZeroMQBasedFrontierClient.create(FRONTIER_ADDRESS, 0), null,
                new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))), 2000);
        (new Thread(worker)).start();
        Worker worker2 = new TestWorker(ZeroMQBasedFrontierClient.create(FRONTIER_ADDRESS, 1), null,
                new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))), 2000);
        (new Thread(worker2)).start();
        frontierWrapper.run();
    }

    public static class TestWorker extends WorkerImpl {

        private static final Logger LOGGER = LoggerFactory.getLogger(SimpleZeroMQTest.TestWorker.class);

        public TestWorker(Frontier frontier, Sink sink, RobotsManager manager, long waitingTime) {
            super(frontier, sink, manager, waitingTime);
        }

        @Override
        public void performCrawling(CrawleableUri uri, List<CrawleableUri> newUris) {
            // check robots.txt
            if (manager.isUriCrawlable(uri.getUri())) {
                // download/analyze URI (based on the URI type)
                LOGGER.debug("I start crawling {} now...", uri);
                if (uri.getUri().toString().endsWith("X")) {
                    String oldUriString = uri.getUri().toString();
                    try {
                        newUris.add(new CrawleableUri(new URI(oldUriString.substring(0, oldUriString.length() - 1)),
                                uri.getIpAddress(), uri.getType()));
                    } catch (URISyntaxException e) {
                        LOGGER.error("Exception while trying to creat URI.", e);
                    }
                }
            } else {
                LOGGER.debug("Crawling {} is not allowed by the RobotsManager.", uri);
            }
        }
    }
}
