package org.aksw.simba.squirrel.components;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.queue.RDBQueue;
import org.aksw.simba.squirrel.rabbit.RPCServer;
import org.aksw.simba.squirrel.rabbit.RespondingDataHandler;
import org.aksw.simba.squirrel.rabbit.ResponseHandler;
import org.aksw.simba.squirrel.rabbit.msgs.CrawlingResult;
import org.aksw.simba.squirrel.rabbit.msgs.UriSet;
import org.aksw.simba.squirrel.rabbit.msgs.UriSetRequest;
import org.aksw.simba.squirrel.worker.impl.AliveMessage;
import org.apache.commons.io.FileUtils;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.DataReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class FrontierComponent extends AbstractComponent implements RespondingDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierComponent.class);

    public static final String FRONTIER_QUEUE_NAME = "squirrel.frontier";

    private static final String SEED_FILE_KEY = "SEED_FILE";
    private static final String RDB_HOST_NAME_KEY = "RDB_HOST_NAME";
    private static final String RDB_PORT_KEY = "RDB_PORT";

    private IpAddressBasedQueue queue;
    private KnownUriFilter knownUriFilter;
    private Frontier frontier;
    private RabbitQueue rabbitQueue;
    private DataReceiver receiver;
    private Serializer serializer;
    private final Semaphore terminationMutex = new Semaphore(0);

    /**
     * A map from {@link org.aksw.simba.squirrel.worker.Worker} id to a timestamp that
     * indicates when the {@link org.aksw.simba.squirrel.worker.Worker} has sent his last {@AliveMessage}.
     */
    private final Map<Integer, Date> mapWorkerTimestamps = new HashMap<>();

    /**
     * A map from {@link org.aksw.simba.squirrel.worker.Worker} id to a list of {@link CrawleableUri} which contains all URIs
     * that the worker has claimed to crawl, but has not yet sent a {@link CrawlingResult} for.
     */
    private final Map<Integer, List<CrawleableUri>> mapWorkerUris = new HashMap<>();

    /**
     * After this period of time (in seconds), a worker is considered to be dead if he has not sent an {@link AliveMessage} since.
     */
    public final static long TIME_WORKER_DEAD = 10;


    @Override
    public void init() throws Exception {
        super.init();
        serializer = new GzipJavaUriSerializer();
        Map<String, String> env = System.getenv();

        String rdbHostName = null;
        int rdbPort = -1;
        if (env.containsKey(RDB_HOST_NAME_KEY)) {
            rdbHostName = env.get(RDB_HOST_NAME_KEY);
            if (env.containsKey(RDB_PORT_KEY)) {
                rdbPort = Integer.parseInt(env.get(RDB_PORT_KEY));
            } else {
                LOGGER.warn("Couldn't get {} from the environment. An in-memory queue will be used.", RDB_PORT_KEY);
            }
        } else {
            LOGGER.warn("Couldn't get {} from the environment. An in-memory queue will be used.", RDB_HOST_NAME_KEY);
        }

        if ((rdbHostName != null) && (rdbPort > 0)) {
            queue = new RDBQueue(rdbHostName, rdbPort);
            ((RDBQueue) queue).open();
            knownUriFilter = new RDBKnownUriFilter(rdbHostName, rdbPort);
            ((RDBKnownUriFilter) knownUriFilter).open();
        } else {
            queue = new InMemoryQueue();
            knownUriFilter = new InMemoryKnownUriFilter(-1);
        }

        // Build frontier
        frontier = new FrontierImpl(knownUriFilter, queue);

        rabbitQueue = this.incomingDataQueueFactory.createDefaultRabbitQueue(FRONTIER_QUEUE_NAME);
        receiver = (new RPCServer.Builder()).responseQueueFactory(outgoingDataQueuefactory).dataHandler(this)
            .maxParallelProcessedMsgs(100).queue(rabbitQueue).build();
        if (env.containsKey(SEED_FILE_KEY)) {
            processSeedFile(env.get(SEED_FILE_KEY));
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (int id : mapWorkerTimestamps.keySet()) {
                    if (mapWorkerTimestamps.get(id) == null) {
                        continue;
                    }
                    long duration = new Date().getTime() - mapWorkerTimestamps.get(id).getTime();
                    if (TimeUnit.MILLISECONDS.toSeconds(duration) > TIME_WORKER_DEAD) {
                        // worker is dead
                        mapWorkerTimestamps.remove(id);

                        if (((FrontierImpl) frontier).getQueue() instanceof IpAddressBasedQueue) {

                            IpAddressBasedQueue ipQueue = (IpAddressBasedQueue) ((FrontierImpl) frontier).getQueue();

                            List<InetAddress> lstIPs = new ArrayList<>();
                            for (CrawleableUri uri : mapWorkerUris.get(id)) {
                                InetAddress ip = uri.getIpAddress();
                                if (!lstIPs.contains(ip)) {
                                    lstIPs.add(ip);
                                }
                            }

                            for (InetAddress ip : lstIPs) {
                                ipQueue.markIpAddressAsAccessible(ip);
                            }
                        }
                        mapWorkerUris.remove(id);
                    }
                }
            }
        }, 0, TIME_WORKER_DEAD / 2);

        LOGGER.info("Frontier initialized.");
    }

    @Override
    public void run() throws Exception {
        // The main thread has nothing to do except waiting for its
        // termination...
        terminationMutex.acquire();
    }

    @Override
    public void close() throws IOException {
        receiver.closeWhenFinished();
        queue.close();
        if (knownUriFilter instanceof Closeable) {
            ((Closeable) knownUriFilter).close();
        }
        super.close();
    }

    @Override
    public void handleData(byte[] data) {
        handleData(data, null, null, null);
    }

    @Override
    public void handleData(byte[] data, ResponseHandler handler, String responseQueueName, String correlId) {
        Object object = null;
        try {
            object = serializer.deserialize(data);
        } catch (Exception e) {
            LOGGER.error("Error whily trying to deserialize incoming data. It will be ignored.", e);
        }
        LOGGER.trace("Got a message (\"{}\").", object.toString());
        if (object != null) {
            if (object instanceof UriSetRequest) {
                if (handler != null) {
                    // get next UriSet
                    try {
                        List<CrawleableUri> uris = frontier.getNextUris();
                        LOGGER.info("Responding with a list of {} uris.",
                            uris == null ? "null" : Integer.toString(uris.size()));

                        if (uris == null || uris.size() == 0) {
                            return;
                        }

                        handler.sendResponse(serializer.serialize(new UriSet(uris)), responseQueueName, correlId);
                        UriSetRequest uriSetRequest = (UriSetRequest) object;
                        mapWorkerUris.put(uriSetRequest.getIdOfWorker(), uris);
                        LOGGER.info("Got Uriset request from worker " + uriSetRequest.getIdOfWorker() +
                            " and sent him " + uris.size() + " uris.");
                    } catch (IOException e) {
                        LOGGER.error("Couldn't serialize new URI set.", e);
                    }
                } else {
                    LOGGER.warn("Got a UriSetRequest object without a ResponseHandler. No response will be sent.");
                }
            } else if (object instanceof UriSet) {
                LOGGER.trace("Received a set of URIs (size={}).", ((UriSet) object).uris.size());
                frontier.addNewUris(((UriSet) object).uris);
            } else if (object instanceof CrawlingResult) {
                CrawlingResult crawlingResult = (CrawlingResult) object;
                LOGGER.trace("Received the message that the crawling for {} URIs is done.",
                    crawlingResult.crawledUris);
                frontier.crawlingDone(crawlingResult.crawledUris, ((CrawlingResult) object).newUris);
                if (mapWorkerUris.get(crawlingResult.idOfWorker) != null) {
                    mapWorkerUris.get(crawlingResult.idOfWorker).removeAll(crawlingResult.crawledUris);
                }
            } else if (object instanceof AliveMessage) {
                AliveMessage message = (AliveMessage) object;
                int idReceived = message.getIdOfWorker();
                LOGGER.trace("Received alive message from worker with id " + idReceived);
                mapWorkerTimestamps.put(idReceived, new Date());

            } else {
                LOGGER.warn("Received an unknown object {}. It will be ignored.", object.toString());
            }
        }
    }

    protected void processSeedFile(String seedFile) {
        try {
            List<String> lines = FileUtils.readLines(new File(seedFile));
            frontier.addNewUris(UriUtils.createCrawleableUriList(lines));
        } catch (Exception e) {
            LOGGER.error("Couldn't process seed file. It will be ignored.", e);
        }
    }
}
