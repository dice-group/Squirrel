package org.aksw.simba.squirrel.frontier.impl;

import com.SquirrelWebObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.*;

public class FrontierSenderToWebservice implements Runnable, Closeable {

    private final long startRunTime = System.currentTimeMillis();
    private WorkerGuard workerGuard;
    private IpAddressBasedQueue queue;
    private KnownUriFilter knownUriFilter;
    private final static String WEB_QUEUE_NAME = "squirrel.web";
    private Connection connection = null;
    private Channel webqueuechannel;

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierSenderToWebservice.class);

    /**
     * Constructor of the Thread
     *
     * @param workerGuard    has information about the workers
     * @param queue          has information about the pending URIs
     * @param knownUriFilter has information about the crawled URIs
     */
    public FrontierSenderToWebservice(WorkerGuard workerGuard, IpAddressBasedQueue queue, KnownUriFilter knownUriFilter) {
        this.workerGuard = workerGuard;
        this.queue = queue;
        this.knownUriFilter = knownUriFilter;
    }

    /**
     * First operation in this Thread. Inits the communication to the rabbit.
     *
     * @return {@code true}, if and only if there was the possibility to establish the connection the rabbitMQ
     */
    private boolean init() {
        //Build rabbit queue to the web
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbit");
        factory.setUsername("guest");
        factory.setPassword("guest");
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            LOGGER.error("ERROR while connecting to the rabbitMQ: " + e.getMessage(), e);
            return false;
        } catch (TimeoutException e) {
            LOGGER.warn("rabbitMQ is not ready until yet: " + e.getMessage() + ". Since " + (System.currentTimeMillis() - startRunTime) + " ms");
            if (System.currentTimeMillis() - startRunTime < 100000) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    return false;
                }
                LOGGER.debug("Start next try...");
                return init();
            } else {
                LOGGER.error("rabbitMQ doesn't answer - give up :( >>" + e.getLocalizedMessage() + "<<");
                return false;

            }
        }
        try {
            webqueuechannel = connection.createChannel();
            webqueuechannel.queueDeclare(WEB_QUEUE_NAME, false, false, true, null);
        } catch (IOException e) {
            LOGGER.error("Connection to rabbit is stable, but there was an error while creating a channel/ queue: " + e.getMessage());
            return false;
        }
        return true;
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
        boolean run = init();
        SquirrelWebObject lastSentObject = null;
        while (run) {
            SquirrelWebObject newObject = new SquirrelWebObject();
            try {
                newObject.setRuntimeInSeconds(Math.round((System.currentTimeMillis() - startRunTime) / 1000d));
                newObject.setCountOfWorker(workerGuard.getNumberOfLiveWorkers());
                newObject.setCountOfDeadWorker(workerGuard.getNumberOfDeadWorker());

                LinkedHashMap<InetAddress, List<CrawleableUri>> currentQueue = queue.getContent();
                if (currentQueue == null || currentQueue.isEmpty()) {
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
            } catch (IllegalAccessException e) {
                LOGGER.error("Logical implementation bug!", e);
            }
            if (lastSentObject == null || !newObject.equals(lastSentObject)) {
                try {
                    webqueuechannel.basicPublish("", WEB_QUEUE_NAME, null, newObject.convertToByteStream());
                    LOGGER.info("Putted a new SquirrelWebObject into the queue " + WEB_QUEUE_NAME);
                    lastSentObject = newObject;
                } catch (IOException e) {
                    LOGGER.warn("Cannot push the lastest SquirrelWebObject into the queue - try it the next time again...", e);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOGGER.debug("End this thread " + Thread.currentThread().getName(), e);
                try {
                    close();
                } catch (IOException e1) {
                    LOGGER.warn("Cannot close the rabbitMQ-connections!", e1);
                }
                return;
            }
        }
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
        try {
            webqueuechannel.close();
            connection.close();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
