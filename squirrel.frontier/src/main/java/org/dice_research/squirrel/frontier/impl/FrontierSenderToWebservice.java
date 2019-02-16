package org.dice_research.squirrel.frontier.impl;


import com.SquirrelWebObject;
import com.graph.VisualisationGraph;
import com.graph.VisualisationNode;
import com.rabbitmq.client.Channel;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.filter.KnownUriFilter;
import org.dice_research.squirrel.data.uri.info.URIReferences;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.queue.IpAddressBasedQueue;
import org.apache.commons.io.IOUtils;
import org.hobbit.core.rabbit.DataSender;
import org.hobbit.core.rabbit.DataSenderImpl;
import org.hobbit.core.rabbit.RabbitQueueFactory;
import org.hobbit.core.rabbit.RabbitQueueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;

public class FrontierSenderToWebservice implements Runnable, Closeable {

    private final long startRunTime = System.currentTimeMillis();
    private WorkerGuard workerGuard;
    private IpAddressBasedQueue queue;
    private KnownUriFilter knownUriFilter;
    private URIReferences uriReferences;
    private final static String WEB_QUEUE_GENERAL_NAME = "squirrel.web.in";
    private RabbitQueueFactory factory;
    private Channel webQueue = null;
    private DataSender sender;
    private boolean run;

    private Serializer serializer = new GzipJavaUriSerializer();

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierSenderToWebservice.class);

    /**
     * Constructor of the Thread
     *
     * @param factory        the {@link RabbitQueueFactory}, the connection framework
     * @param workerGuard    has information about the workers
     * @param queue          has information about the pending URIs
     * @param knownUriFilter has information about the crawled URIs
     * @param uriReferences  has information for the crawled graph. if it is {@code null}, the feature of creating a crawled graph is disabled
     */
    public FrontierSenderToWebservice(RabbitQueueFactory factory, WorkerGuard workerGuard, IpAddressBasedQueue queue, KnownUriFilter knownUriFilter, URIReferences uriReferences) {
        this.factory = factory;
        this.workerGuard = workerGuard;
        this.queue = queue;
        this.knownUriFilter = knownUriFilter;
        this.uriReferences = uriReferences;
    }

    /**
     * First operation in this Thread. Init the communication to the rabbit.
     *
     * @return {@code true}, if and only if there was the possibility to establish the connection the rabbitMQ
     */
    private boolean init() {
        //Build rabbit queue to the web
        try {
            sender = DataSenderImpl.builder().queue(new RabbitQueueFactoryImpl(factory.getConnection()), WEB_QUEUE_GENERAL_NAME).build();
            LOGGER.debug("Created successfully the sender " + sender);
        } catch (IOException e) {
            LOGGER.error("Finally ERROR while creating queue" + WEB_QUEUE_GENERAL_NAME + ". Returning false.");
            return false;
        }
        return true;
    }

    @Deprecated
    @SuppressWarnings("unused")
    private boolean establishChannel(int triesLeft) {
        try {
            webQueue = factory.createChannel();
            return true;
        } catch (IOException e) {
            LOGGER.warn("Connection to rabbit is stable, but there was an error while creating a channel/ queue: " + e.getMessage() + ". There are " + triesLeft + " tries left, try it again in 3s!");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e2) {
                LOGGER.error("Failed to set up the queue " + WEB_QUEUE_GENERAL_NAME + ", there were " + triesLeft + " tries left", e2);
                return false;
            }
            if (triesLeft > 0) {
                return establishChannel(triesLeft - 1);
            } else {
                LOGGER.error("Failed to set up the queue " + WEB_QUEUE_GENERAL_NAME + ", ran out of tries", e);
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
                if (uriReferences != null) {
                    VisualisationGraph graph = generateVisualisationGraph();
                    newObject.setGraph(graph);
                    LOGGER.info("Added a new crawled graph to the SquirrelWebObject " + newObject + " with " + graph.getNodes().length + " nodes and " + graph.getEdges().length + " edges!");
                }
                if (!newObject.equals(lastSentObject)) {
                    sender.sendData(serializer.serialize(newObject));
                    //webQueue.basicPublish("", WEB_QUEUE_GENERAL_NAME, null, serializer.serialize(newObject));
                    LOGGER.info("Putted a new SquirrelWebObject into the queue " + WEB_QUEUE_GENERAL_NAME);
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

    /**
     * Generates a {@link SquirrelWebObject} from the current data
     *
     * @return a instance of a {@link SquirrelWebObject}
     * @throws IllegalAccessException if this is thrown, then there is programming bug!
     */
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
            currentQueue.forEach((key, value) -> value.forEach(uri -> pendingURIs.add(uri.getUri().toString())));
            newObject.setPendingURIs(pendingURIs);
            newObject.setNextCrawledURIs(currentQueue.entrySet().iterator().next().getValue().stream().map(e -> e.getUri().toString()).collect(Collectors.toList()));
        }

        //Michael remarks, that's not a good idea to pass all crawled URIs, because that takes to much time...
        //newObject.setCrawledURIs(Collections.EMPTY_LIST);
        newObject.setCountOfCrawledURIs((int) knownUriFilter.count());

        return newObject;
    }

    /**
     * Collects all crawled URIs from knownUriFilter (assumes a {@link org.dice_research.squirrel.data.uri.filter.RDBKnownUriFilter} object) for generating zu crawled graph
     *
     * @return a instance (crawled graph) of {@link VisualisationGraph}
     */
    private VisualisationGraph generateVisualisationGraph() {
        if (uriReferences == null) {
            throw new IllegalAccessError(this + " doesn't saves a uriReferences list, that is necessary to build the graph!");
        }

        VisualisationGraph graph = new VisualisationGraph();
        Iterator<AbstractMap.SimpleEntry<String, List<String>>> iterator = uriReferences.walkThroughCrawledGraph(25, true, true);

        int counter = 0;
        while (iterator.hasNext() && counter < 25) {
            AbstractMap.SimpleEntry<String, List<String>> nextNode = iterator.next();
            int ipDivider = nextNode.getKey().lastIndexOf('|');
            String uri = (ipDivider == -1) ? nextNode.getKey() : nextNode.getKey().substring(0, ipDivider);
            VisualisationNode g = (ipDivider == -1) ? graph.addNode(uri) : graph.addNode(uri, nextNode.getKey().substring(ipDivider + 1));
            if (g != null)
                g.setColor((counter == 0) ? Color.ORANGE : ((counter <= 20) ? Color.GRAY : Color.GREEN));
            nextNode.getValue().forEach(v -> graph.addEdge(uri, v));
            ////////
            LOGGER.debug("Retrieves a node from the crawled graph with the uriReferences-Iterator: " + graph.getNode(nextNode.getKey()) + ", including " + graph.getEdges(graph.getNode(nextNode.getKey())).length + " edges, counter is " + counter);
            ////////

            counter++;
        }

        graph.optimizeArrays();
        return graph;
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
        sender.close();
//        webQueue.queueDelete(WEB_QUEUE_GENERAL_NAME);
//        try {
//            webQueue.close();
//        } catch (TimeoutException e) {
//            LOGGER.debug("Failed to close the [" + webQueue + "]-Channel in " + Thread.currentThread().getName(), e);
//        }
    }
}