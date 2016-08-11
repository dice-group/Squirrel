package org.aksw.simba.squirrel.fetcher.deref;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontier;
import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontierClient;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.robots.RobotsManager;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

public class DereferencingFetcherTest {

    public static final String FRONTIER_ADDRESS = "tcp://localhost:5501";
    // public static final String FRONTIER_ADDRESS = "ipc://localhost.ipc";

    public static void main(String[] args) throws UnknownHostException, URISyntaxException {
        FileBasedSink sink = new FileBasedSink(new File("/tmp/"), false);
        IpAddressBasedQueue queue = new InMemoryQueue();

        queue.addUri(new CrawleableUri(new URI("https://tinyurl.com/aksworg-ttl"),
                InetAddress.getByAddress(new byte[] { 104, 20, 87, 65 }), UriType.DEREFERENCEABLE));
        Frontier frontier = new FrontierImpl(new InMemoryKnownUriFilter(), queue);
        ZeroMQBasedFrontier frontierWrapper = ZeroMQBasedFrontier.create(frontier, FRONTIER_ADDRESS);
        Worker worker = new WorkerImpl(ZeroMQBasedFrontierClient.create(FRONTIER_ADDRESS, 0), sink,
                new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))), 2000);
        (new Thread(worker)).start();
        frontierWrapper.run();
    }
}
