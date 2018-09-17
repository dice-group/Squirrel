package org.aksw.simba.squirrel.fetcher;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.mem.InMemorySink;

/*
 * TODO Has to be reworked or deleted
 */
@Deprecated
public class FetcherTest {

    public void run(CrawleableUri crawleableUri) {
        Sink sink = new InMemorySink();
        IpAddressBasedQueue queue = new InMemoryQueue();
        System.out.println(crawleableUri);

        queue.addUri(crawleableUri);
//        Frontier frontier = new FrontierImpl(new InMemoryKnownUriFilter(-1), queue);
//        Worker worker = new WorkerImpl(ZeroMQBasedFrontierClient.create(FRONTIER_ADDRESS, 0), sink,
//                new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))),
//                new GzipJavaUriSerializer(), 2000);
//        (new Thread(worker)).start();
//        frontierWrapper.run();
    }
}
