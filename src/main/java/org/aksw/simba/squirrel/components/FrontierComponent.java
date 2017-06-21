package org.aksw.simba.squirrel.components;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.queue.RDBQueue;
import org.aksw.simba.squirrel.rabbit.RPCServer;
import org.aksw.simba.squirrel.rabbit.RabbitMQHelper;
import org.aksw.simba.squirrel.rabbit.RespondingDataHandler;
import org.aksw.simba.squirrel.rabbit.ResponseHandler;
import org.aksw.simba.squirrel.rabbit.msgs.CrawlingResult;
import org.aksw.simba.squirrel.rabbit.msgs.UriSet;
import org.aksw.simba.squirrel.rabbit.msgs.UriSetRequest;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.DataReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontierComponent extends AbstractComponent implements RespondingDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierComponent.class);

    public static final String FRONTIER_QUEUE_NAME = "squirrel.frontier";

    private static final String RDB_HOST_NAME_KEY = "RDB_HOST_NAME";
    private static final String RDB_PORT_KEY = "RDB_PORT";

    private IpAddressBasedQueue queue;
    private RDBKnownUriFilter knownUriFilter;
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
        if (env.containsKey(RDB_HOST_NAME_KEY)) {
            rdbHostName = env.get(RDB_HOST_NAME_KEY);
        } else {
            String msg = "Couldn't get " + RDB_HOST_NAME_KEY + " from the environment.";
            throw new Exception(msg);
        }
        int rdbPort;
        if (env.containsKey(RDB_PORT_KEY)) {
            rdbPort = Integer.parseInt(env.get(RDB_PORT_KEY));
        } else {
            String msg = "Couldn't get " + RDB_PORT_KEY + " from the environment.";
            throw new Exception(msg);
        }

        // Build frontier
        RDBQueue queue = new RDBQueue(rdbHostName, rdbPort);
        queue.open();
        RDBKnownUriFilter knownUriFilter = new RDBKnownUriFilter(rdbHostName, rdbPort);
        knownUriFilter.open();
        frontier = new FrontierImpl(knownUriFilter, queue);

        rabbitQueue = this.incomingDataQueueFactory.createDefaultRabbitQueue(FRONTIER_QUEUE_NAME);
        receiver = (new RPCServer.Builder()).responseQueueFactory(outgoingDataQueuefactory).dataHandler(this)
                .maxParallelProcessedMsgs(100).queue(rabbitQueue).build();
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
        knownUriFilter.close();
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
}
