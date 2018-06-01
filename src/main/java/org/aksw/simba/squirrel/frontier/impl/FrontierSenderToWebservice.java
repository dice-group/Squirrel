package org.aksw.simba.squirrel.frontier.impl;

import com.SquirrelWebObject;
import com.rabbitmq.client.Channel;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.apache.commons.io.IOUtils;
import org.hobbit.core.rabbit.RabbitQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;

public class FrontierSenderToWebservice implements Runnable, Closeable {

    private final long startRunTime = System.currentTimeMillis();
    private WorkerGuard workerGuard;
    private IpAddressBasedQueue queue;
    private KnownUriFilter knownUriFilter;
    private final static String WEB_QUEUE_NAME = "squirrel.web";
    private RabbitQueueFactory factory;
    private Channel webQueue = null;
    private boolean run;

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierSenderToWebservice.class);

    /**
     * Constructor of the Thread
     *
     * @param factory        the {@link RabbitQueueFactory}, the connection framework
     * @param workerGuard    has information about the workers
     * @param queue          has information about the pending URIs
     * @param knownUriFilter has information about the crawled URIs
     */
    public FrontierSenderToWebservice(RabbitQueueFactory factory, WorkerGuard workerGuard, IpAddressBasedQueue queue, KnownUriFilter knownUriFilter) {
        this.factory = factory;
        this.workerGuard = workerGuard;
        this.queue = queue;
        this.knownUriFilter = knownUriFilter;
    }

    /**
     * First operation in this Thread. Init the communication to the rabbit.
     *
     * @return {@code true}, if and only if there was the possibility to establish the connection the rabbitMQ
     */
    private boolean init() {
        //Build rabbit queue to the web
        if (establishChannel(5)) {
            LOGGER.debug("Created successfully " + webQueue);
        } else {
            LOGGER.error("Finally ERROR while creating queue" + WEB_QUEUE_NAME + ". Returning false.");
            return false;
        }
        return true;
    }

    private boolean establishChannel(int triesLeft) {
        try {
            webQueue = factory.createChannel();
            return true;
        } catch (IOException e) {
            LOGGER.warn("Connection to rabbit is stable, but there was an error while creating a channel/ queue: " + e.getMessage() + ". There are " + triesLeft + " tries left, try it again in 3s!");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e2) {
                LOGGER.error("Failed to set up the queue " + WEB_QUEUE_NAME + ", there were " + triesLeft + " tries left", e2);
                return false;
            }
            if (triesLeft > 0) {
                return establishChannel(triesLeft - 1);
            } else {
                LOGGER.error("Failed to set up the queue " + WEB_QUEUE_NAME + ", ran out of tries", e);
                return false;
            }
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        run = init();
        SquirrelWebObject lastSentObject = null;
        try {
            while (run) {
                SquirrelWebObject newObject = generateSquirrelWebObject();
                if (!newObject.equals(lastSentObject)) {
                    webQueue.basicPublish("", WEB_QUEUE_NAME, null, newObject.convertToByteStream());
                    LOGGER.info("Putted a new SquirrelWebObject into the queue " + WEB_QUEUE_NAME);
                    lastSentObject = newObject;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            // If we are interrupted it is fine. No special handling needed.
            LOGGER.debug("End this thread " + Thread.currentThread().getName(), e);
        } catch (Exception e) {
            // If anything else is thrown
            LOGGER.error("Caught an exception while sending information to the web front end. Aborting... ", e);
        } finally {
            // Whatever happens, make sure that this instance is closed
            IOUtils.closeQuietly(this);
        }
    }

    private SquirrelWebObject generateSquirrelWebObject() throws IllegalAccessException {
        SquirrelWebObject newObject = new SquirrelWebObject();
        newObject.setRuntimeInSeconds(Math.round((System.currentTimeMillis() - startRunTime) / 1000d));
        newObject.setCountOfWorker(workerGuard.getNumberOfLiveWorkers());
        newObject.setCountOfDeadWorker(workerGuard.getNumberOfDeadWorker());

        LinkedHashMap<InetAddress, List<CrawleableUri>> currentQueue = new LinkedHashMap<>(50);
        Iterator<AbstractMap.SimpleEntry<InetAddress, List<CrawleableUri>>> i;
        for (i = queue.getIPURIIterator(); i.hasNext() && currentQueue.size() < 50; ) {
            AbstractMap.SimpleEntry<InetAddress, List<CrawleableUri>> entry = i.next();
            currentQueue.put(entry.getKey(), entry.getValue());
        }
        if (currentQueue.isEmpty()) {
            newObject.setIPMapPendingURis(EMPTY_MAP);
            newObject.setPendingURIs(EMPTY_LIST);
            newObject.setNextCrawledURIs(EMPTY_LIST);
        } else {
            newObject.setIPMapPendingURis(currentQueue.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey().getHostAddress(), e.getValue().stream().map(uri -> uri.getUri().getPath()).collect(Collectors.toList())))
                .collect(HashMap::new, (m, entry) -> m.put(entry.getKey(), entry.getValue()), HashMap::putAll));
            List<String> pendingURIs = new ArrayList<>(currentQueue.size());
            currentQueue.forEach((key, value) -> value.forEach(uri -> pendingURIs.add(uri.getUri().getPath())));
            newObject.setPendingURIs(pendingURIs);
            newObject.setNextCrawledURIs(currentQueue.entrySet().iterator().next().getValue().stream().map(e -> e.getUri().getRawPath()).collect(Collectors.toList()));
        }

        //Michael remarks, that's not a good idea to pass all crawled URIs, because that takes to much time...
        //newObject.setCrawledURIs(Collections.EMPTY_LIST);
        newObject.setCountOfCrawledURIs((int) knownUriFilter.count());

        return newObject;
    }

    /**
     * Stops the instance ({@link Thread}) of the {@link FrontierSenderToWebservice}.
     * ATTENTION: it's not a force stop! Maybe the {@link Thread} will run for some further milliseconds...
     */
    public void stop() {
        run = false;
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     * <p>
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        webQueue.queueDelete(WEB_QUEUE_NAME);
        try {
            webQueue.close();
        } catch (TimeoutException e) {
            LOGGER.debug("Failed to close the [" + webQueue + "]-Channel in " + Thread.currentThread().getName(), e);
        }
    }
}
