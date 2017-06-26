package org.aksw.simba.squirrel.components;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.queue.RDBQueue;
import org.aksw.simba.squirrel.rabbit.RPCServer;
import org.aksw.simba.squirrel.rabbit.RabbitMQHelper;
import org.aksw.simba.squirrel.rabbit.RespondingDataHandler;
import org.aksw.simba.squirrel.rabbit.ResponseHandler;
import org.aksw.simba.squirrel.rabbit.msgs.CrawlingResult;
import org.aksw.simba.squirrel.rabbit.msgs.UriSet;
import org.aksw.simba.squirrel.rabbit.msgs.UriSetRequest;
import org.apache.commons.io.FileUtils;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.DataReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private RabbitMQHelper rabbitHelper;
    private final Semaphore terminationMutex = new Semaphore(0);

    @Override
    public void init() throws Exception {
        super.init();
        rabbitHelper = new RabbitMQHelper();
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
            ((RDBKnownUriFilter)knownUriFilter).open();
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
        Object object = rabbitHelper.parseObject(data);
        if (object != null) {
            if (object instanceof UriSetRequest) {
                if (handler != null) {
                    // get next UriSet
                    handler.sendResponse(rabbitHelper.writeObject(new UriSet(frontier.getNextUris())),
                            responseQueueName, correlId);
                }
            } else if (object instanceof UriSet) {
                frontier.addNewUris(((UriSet) object).uris);
            } else if (object instanceof CrawlingResult) {
                frontier.crawlingDone(((CrawlingResult) object).crawledUris, ((CrawlingResult) object).newUris);
            } else {
                LOGGER.warn("Received an unknown object {}. It will be ignored.", object.toString());
            }
        }
    }

    protected void processSeedFile(String seedFile) {
        try {
            List<String> lines = FileUtils.readLines(new File(seedFile));
            frontier.addNewUris(UriUtils.createCrawleableUriList(lines.toArray(new String[lines.size()])));
        } catch (Exception e) {
            LOGGER.error("Couldn't process seed file. It will be ignored.", e);
        }
    }
}
