package org.aksw.simba.squirrel.queue;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This class tests the following situation. Three different threads are running
 * in parallel sharing the same {@link IpAddressBasedQueue} instance. The first
 * thread adds URIs to the queue until the second thread has finished its work.
 * Every added URI has the same type and one of two IPs.
 * </p>
 * 
 * <p>
 * The second thread takes a single chunk, thus, blocks one of the two IPs and
 * sets this IP in the third thread. After that the second thread sleeps for a
 * long time. At the end of this sleeping period, it informs the other two
 * threads about its termination, marks the ip as accessible and terminates.
 * </p>
 * 
 * <p>
 * The third thread requests new chunks from the queue (with a lower frequency
 * than the first thread is adding them) and checks whether it gets chunks
 * containing the IP blocked by the third thread. After the second thread has
 * finished, this thread makes sure that it gets at least one time URIs of the
 * IP that has been blocked by the second thread.
 * </p>
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class IpAddressBasedQueueIpBlockingTest {

    private static final long DELAY_BETWEEN_URI_GENERATIONS = 10;
    private static final long IP_BLOCKING_DURATION = 1000;
    private static final long DELAY_BETWEEN_WORKPACKAGE_REQUESTS = 60;

    protected Throwable t;
    protected IpAddressBasedQueue queue;

    @Test
    public void test() throws Exception {
        // This is a test of parallelization, thus, we should run it several
        // times
        for (int i = 0; i < 3; ++i) {
            queue = new InMemoryQueue();
            startTestRun(queue);
        }
    }

    private void startTestRun(IpAddressBasedQueue queue) throws Exception {
        UriAdder adder = new UriAdder(
                new InetAddress[] { InetAddress.getByName("192.168.100.1"), InetAddress.getByName("192.168.200.1") },
                this);
        RegularUriConsumer regConsumer = new RegularUriConsumer(this);
        LongDelayUriConsumer longConsumer = new LongDelayUriConsumer(this, adder, regConsumer);
        Thread threads[] = new Thread[] { new Thread(adder), new Thread(longConsumer), new Thread(regConsumer) };
        for (int i = 0; i < threads.length; ++i) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; ++i) {
            threads[i].join();
        }
        if (t != null) {
            throw new AssertionError("One of the threads reported a throwable.", t);
        }
    }

    /**
     * Simple threads that adds URIs with a delay until its run flag is set to
     * false.
     * 
     * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
     *
     */
    public static class UriAdder implements Runnable {

        private InetAddress ips[];
        private int uriCounter = 0;
        private int ipAddressCounter = 0;
        private CrawleableUriFactory4Tests factory = new CrawleableUriFactory4Tests();
        private IpAddressBasedQueueIpBlockingTest testClassInstance;
        private boolean run = true;

        public UriAdder(InetAddress[] ips, IpAddressBasedQueueIpBlockingTest testClassInstance) {
            this.ips = ips;
            this.testClassInstance = testClassInstance;
        }

        @Override
        public void run() {
            try {
                while (run) {
                    testClassInstance.queue.addUri(generateNextUri());
                    try {
                        Thread.sleep(DELAY_BETWEEN_URI_GENERATIONS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Throwable t) {
                testClassInstance.t = t;
            }
        }

        public void stop() {
            run = false;
        }

        protected CrawleableUri generateNextUri() {
            CrawleableUri uri;
            try {
                uri = factory.create(new URI("http://example.org/uri_" + (uriCounter++)), ips[ipAddressCounter],
                        UriType.UNKNOWN);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            ++ipAddressCounter;
            if (ipAddressCounter == ips.length) {
                ipAddressCounter = 0;
            }
            return uri;
        }
    }

    /**
     * This thread waits a short time, requests a chunk of URIs and waits for a
     * long time. After that it stops the {@link UriAdder} and informs the
     * {@link RegularUriConsumer} about its termination.
     * 
     * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
     *
     */
    public static class LongDelayUriConsumer implements Runnable {

        private IpAddressBasedQueueIpBlockingTest testClassInstance;
        private UriAdder adder;
        private RegularUriConsumer regConsumer;

        public LongDelayUriConsumer(IpAddressBasedQueueIpBlockingTest testClassInstance, UriAdder adder,
                RegularUriConsumer regConsumer) {
            this.testClassInstance = testClassInstance;
            this.adder = adder;
            this.regConsumer = regConsumer;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(DELAY_BETWEEN_WORKPACKAGE_REQUESTS);
                // request URI and set it inside the RegularUriConsumer
                List<CrawleableUri> uris = testClassInstance.queue.getNextUris();
                Assert.assertNotNull("Shouldn't get null.", uris);
                Assert.assertNotEquals("Shouldn't get an empty list.", 0, uris.size());
                regConsumer.blockedAddress = uris.get(0).getIpAddress();
                // sleep for a long time
                Thread.sleep(IP_BLOCKING_DURATION);
                testClassInstance.queue.markIpAddressAsAccessible(uris.get(0).getIpAddress());
            } catch (Throwable t) {
                testClassInstance.t = t;
            } finally {
                // Stop the adder and report the termination of this thread to
                // the RegularUriConsumer
                adder.stop();
                regConsumer.longDelayThreadIsDead = true;
            }
        }

    }

    /**
     * <p>
     * This thread waits for a short time. After that it requests chunks of uris
     * and makes sure, that they are not null and not empty (since the adding
     * thread is faster that this thread, this should never happen) and that it
     * does not get a URI with the IP blocked by the other
     * {@link LongDelayUriConsumer}.
     * </p>
     * 
     * <p>
     * After the {@link LongDelayUriConsumer} is dead, this thread waits for a
     * short time. After that it requests three more chunks. It makes sure that
     * at least one of the first two chunks contains the IP that has been
     * blocked by the other thread and that the third chunk is null.
     * </p>
     * 
     * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
     *
     */
    public static class RegularUriConsumer implements Runnable {

        private IpAddressBasedQueueIpBlockingTest testClassInstance;
        private InetAddress blockedAddress = null;
        private boolean longDelayThreadIsDead = false;

        public RegularUriConsumer(IpAddressBasedQueueIpBlockingTest testClassInstance) {
            this.testClassInstance = testClassInstance;
        }

        @Override
        public void run() {
            try {
                // give the other thread the time to block one of the addresses
                Thread.sleep(2 * DELAY_BETWEEN_WORKPACKAGE_REQUESTS);
                Assert.assertNotNull("The blocked IP address should have been set.", blockedAddress);
                List<CrawleableUri> uris;
                while (!longDelayThreadIsDead) {
                    uris = testClassInstance.queue.getNextUris();
                    Assert.assertNotNull("Shouldn't get null.", uris);
                    Assert.assertNotEquals("Shouldn't get an empty list.", 0, uris.size());
                    for (CrawleableUri uri : uris) {
                        Assert.assertNotEquals("The retrieved URI should not have the IP blocked by another thread.",
                                blockedAddress, uri.getIpAddress());
                    }
                    testClassInstance.queue.markIpAddressAsAccessible(uris.get(0).getIpAddress());
                    Thread.sleep(DELAY_BETWEEN_WORKPACKAGE_REQUESTS);
                }
                // wait a short time
                Thread.sleep(DELAY_BETWEEN_WORKPACKAGE_REQUESTS);
                // request two additional chunks
                Set<InetAddress> ips = new HashSet<InetAddress>();
                for (int i = 0; i < 2; ++i) {
                    uris = testClassInstance.queue.getNextUris();
                    if (uris != null) {
                        for (CrawleableUri uri : uris) {
                            ips.add(uri.getIpAddress());
                        }
                    }
                }
                Assert.assertTrue("The set " + ips.toString() + " should have contained the former blocked IP "
                        + blockedAddress.toString(), ips.contains(blockedAddress));
                Assert.assertNull("Expected the queue to be empty.", testClassInstance.queue.getNextUris());
            } catch (Throwable t) {
                testClassInstance.t = t;
            }
        }
    }
}