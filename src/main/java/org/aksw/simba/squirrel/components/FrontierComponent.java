package org.aksw.simba.squirrel.components;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.aksw.simba.squirrel.configurator.RDBConfiguration;
import org.aksw.simba.squirrel.configurator.SeedConfiguration;
import org.aksw.simba.squirrel.configurator.WhiteListConfiguration;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RegexBasedWhiteListFilter;
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
import org.apache.commons.io.FileUtils;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.DataReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontierComponent extends AbstractComponent implements RespondingDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierComponent.class);

    public static final String FRONTIER_QUEUE_NAME = "squirrel.frontier";

    protected IpAddressBasedQueue queue;
    private KnownUriFilter knownUriFilter;
    protected Frontier frontier;
    protected RabbitQueue rabbitQueue;
    protected DataReceiver receiver;
    protected Serializer serializer;
    protected final Semaphore terminationMutex = new Semaphore(0);

    @Override
    public void init() throws Exception {
        super.init();
        serializer = new GzipJavaUriSerializer();

        RDBConfiguration rdbConfiguration = RDBConfiguration.getRDBConfiguration();
        if(rdbConfiguration != null) {
            String rdbHostName = rdbConfiguration.getRDBHostName();
            Integer rdbPort = rdbConfiguration.getRDBPort();
            queue = new RDBQueue(rdbHostName, rdbPort,serializer);
            ((RDBQueue) queue).open();
            
            WhiteListConfiguration whiteListConfiguration = WhiteListConfiguration.getWhiteListConfiguration();
            if(whiteListConfiguration != null) {
                File whitelistFile = new File(whiteListConfiguration.getWhiteListURI());
                knownUriFilter = new RegexBasedWhiteListFilter(rdbConfiguration.getRDBHostName(),
                    rdbConfiguration.getRDBPort(), whitelistFile);
                knownUriFilter.open();
            }else {
            	knownUriFilter = new RDBKnownUriFilter(rdbHostName, rdbPort);
                ((RDBKnownUriFilter) knownUriFilter).open();	
            }
        } else {
            LOGGER.warn("Couldn't get RDBConfiguration. An in-memory queue will be used.");
            queue = new InMemoryQueue();
            knownUriFilter = new InMemoryKnownUriFilter(-1);
        }

        // Build frontier
        frontier = new FrontierImpl(knownUriFilter, queue);

        rabbitQueue = this.incomingDataQueueFactory.createDefaultRabbitQueue(FRONTIER_QUEUE_NAME);
        receiver = (new RPCServer.Builder()).responseQueueFactory(outgoingDataQueuefactory).dataHandler(this)
                .maxParallelProcessedMsgs(100).queue(rabbitQueue).build();

        SeedConfiguration seedConfiguration = SeedConfiguration.getSeedConfiguration();
        if (seedConfiguration != null) {
            processSeedFile(seedConfiguration.getSeedFile());
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
        Object deserializedData = null;
        try {
            deserializedData = serializer.deserialize(data);
        } catch (IOException e) {
            LOGGER.error("Error while trying to deserialize incoming data. It will be ignored.", e);
        }
        LOGGER.trace("Got a message (\"{}\").", deserializedData.toString());

        if (deserializedData != null) {
            if (deserializedData instanceof UriSetRequest) {
                responseToUriSetRequest(handler, responseQueueName, correlId);
            } else if (deserializedData instanceof UriSet) {
                LOGGER.trace("Received a set of URIs (size={}).", ((UriSet) deserializedData).uris.size());
                frontier.addNewUris(((UriSet) deserializedData).uris);
            } else if (deserializedData instanceof CrawlingResult) {
                LOGGER.trace("Received the message that the crawling for {} URIs is done.",
                        ((CrawlingResult) deserializedData).crawledUris);
                frontier.crawlingDone(((CrawlingResult) deserializedData).crawledUris, ((CrawlingResult) deserializedData).newUris);
            } else {
                LOGGER.warn("Received an unknown object {}. It will be ignored.", deserializedData.toString());
            }
        }
    }

    private void responseToUriSetRequest(ResponseHandler handler, String responseQueueName, String correlId) {
        if (handler != null) {
            try {
                List<CrawleableUri> uris = frontier.getNextUris();
                LOGGER.trace("Responding with a list of {} uris.",
                    uris == null ? "null" : Integer.toString(uris.size()));
                handler.sendResponse(serializer.serialize(new UriSet(uris)), responseQueueName, correlId);
            } catch (IOException e) {
                LOGGER.error("Couldn't serialize new URI set.", e);
            }
        } else {
            LOGGER.warn("Got a UriSetRequest object without a ResponseHandler. No response will be sent.");
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
