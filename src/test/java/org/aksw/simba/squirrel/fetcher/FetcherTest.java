package org.aksw.simba.squirrel.fetcher;

import java.io.File;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontier;
import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontierClient;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

public class FetcherTest {

    public static final String FRONTIER_ADDRESS = "tcp://localhost:5501";
    // public static final String FRONTIER_ADDRESS = "ipc://localhost.ipc";

    public void run(CrawleableUri crawleableUri) {
        FileBasedSink sink = new FileBasedSink(new File("/tmp/"), false);
        IpAddressBasedQueue queue = new InMemoryQueue();

        System.out.println(crawleableUri);

        queue.addUri(crawleableUri);
        Frontier frontier = new FrontierImpl(new InMemoryKnownUriFilter(-1), queue);
        ZeroMQBasedFrontier frontierWrapper = ZeroMQBasedFrontier.create(frontier, FRONTIER_ADDRESS);
        Worker worker = new WorkerImpl(ZeroMQBasedFrontierClient.create(FRONTIER_ADDRESS, 0), sink,
                new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))), 2000);
        (new Thread(worker)).start();
        frontierWrapper.run();
    }
}
