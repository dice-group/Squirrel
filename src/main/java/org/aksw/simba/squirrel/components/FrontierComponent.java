package org.aksw.simba.squirrel.components;

import org.aksw.simba.squirrel.configurator.RDBConfiguration;
import org.aksw.simba.squirrel.configurator.SeedConfiguration;
import org.aksw.simba.squirrel.configurator.WebConfiguration;
import org.aksw.simba.squirrel.configurator.WhiteListConfiguration;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RegexBasedWhiteListFilter;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.frontier.ExtendedFrontier;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.ExtendedFrontierImpl;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.frontier.impl.FrontierSenderToWebservice;
import org.aksw.simba.squirrel.frontier.impl.WorkerGuard;
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
import java.util.List;
import java.util.concurrent.Semaphore;

public class FrontierComponent extends AbstractComponent implements RespondingDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierComponent.class);

    static final String FRONTIER_QUEUE_NAME = "squirrel.frontier";

    protected IpAddressBasedQueue queue;
    private KnownUriFilter knownUriFilter;
    private Frontier frontier;
    private RabbitQueue rabbitQueue;
    private DataReceiver receiver;
    private Serializer serializer;
    private final Semaphore terminationMutex = new Semaphore(0);
    private final WorkerGuard workerGuard = new WorkerGuard(this);
    private final boolean doRecrawling = true;

    private final long startRunTime = System.currentTimeMillis();

    @Override
    public void init() throws Exception {
        super.init();
        serializer = new GzipJavaUriSerializer();
        RDBConfiguration rdbConfiguration = RDBConfiguration.getRDBConfiguration();
        if(rdbConfiguration != null) {
            String rdbHostName = rdbConfiguration.getRDBHostName();
            Integer rdbPort = rdbConfiguration.getRDBPort();
            queue = new RDBQueue(rdbHostName, rdbPort,serializer);
            queue.open();

            WhiteListConfiguration whiteListConfiguration = WhiteListConfiguration.getWhiteListConfiguration();
            if(whiteListConfiguration != null) {
                File whitelistFile = new File(whiteListConfiguration.getWhiteListURI());
                knownUriFilter = new RegexBasedWhiteListFilter(rdbConfiguration.getRDBHostName(),
                    rdbConfiguration.getRDBPort(), whitelistFile);
                knownUriFilter.open();
            } else {
            	knownUriFilter = new RDBKnownUriFilter(rdbHostName, rdbPort, doRecrawling);
                knownUriFilter.open();
            }
        } else {
            LOGGER.warn("Couldn't get RDBConfiguration. An in-memory queue will be used.");
            queue = new InMemoryQueue();
            knownUriFilter = new InMemoryKnownUriFilter(doRecrawling);
        }

        // Build frontier
        frontier = new ExtendedFrontierImpl(knownUriFilter, queue, doRecrawling);

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
        WebConfiguration webConfiguration = WebConfiguration.getWebConfiguration();
        if (webConfiguration.isCommunicationWithWebserviceEnabled()) {
            Thread sender = new Thread(new FrontierSenderToWebservice(outgoingDataQueuefactory, workerGuard, queue, knownUriFilter));
            sender.setName("Sender to the Webservice via RabbitMQ (current information from the Frontier)");
            sender.start();
            LOGGER.info("Started thread [" + sender.getName() + "] <ID " + sender.getId() + " in the state " + sender.getState() + " with the priority " + sender.getPriority() + ">");
        }
        // The main thread has nothing to do except waiting for its
        // termination...
        terminationMutex.acquire();
    }

    @Override
    public void close() throws IOException {
        receiver.closeWhenFinished();
        queue.close();
        if (knownUriFilter instanceof Closeable) {
            knownUriFilter.close();
        }
        workerGuard.shutdown();
        frontier.close();
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

        if (deserializedData != null) {
            LOGGER.trace("Got a message (\"{}\").", deserializedData.toString());
            if (deserializedData instanceof UriSetRequest) {
                responseToUriSetRequest(handler, responseQueueName, correlId, (UriSetRequest) deserializedData);
            } else if (deserializedData instanceof UriSet) {
                LOGGER.trace("Received a set of URIs (size={}).", ((UriSet) deserializedData).uris.size());
                frontier.addNewUris(((UriSet) deserializedData).uris);
            } else if (deserializedData instanceof CrawlingResult) {
                CrawlingResult crawlingResult = (CrawlingResult) deserializedData;
                LOGGER.trace("Received the message that the crawling for {} URIs is done.",
                        ((CrawlingResult) deserializedData).crawledUris);
                frontier.crawlingDone(((CrawlingResult) deserializedData).crawledUris, ((CrawlingResult) deserializedData).newUris);
                workerGuard.removeUrisForWorker(crawlingResult.idOfWorker, crawlingResult.crawledUris);
            } else if (deserializedData instanceof AliveMessage) {
                AliveMessage message = (AliveMessage) deserializedData;
                int idReceived = message.getIdOfWorker();
                LOGGER.trace("Received alive message from worker with id " + idReceived);
                workerGuard.putNewTimestamp(idReceived);

            } else {
                LOGGER.warn("Received an unknown object {}. It will be ignored.", deserializedData.toString());
            }
        }
    }

    private void responseToUriSetRequest(ResponseHandler handler, String responseQueueName, String correlId, UriSetRequest uriSetRequest) {
        if (handler != null) {
            // get next UriSet
            try {
                List<CrawleableUri> uris = frontier.getNextUris();
                LOGGER.trace("Responding with a list of {} uris.",
                    uris == null ? "null" : Integer.toString(uris.size()));
                handler.sendResponse(serializer.serialize(new UriSet(uris)), responseQueueName, correlId);
                if (uris != null && uris.size() > 0) {
                    workerGuard.putUrisForWorker(uriSetRequest.getIdOfWorker(), uriSetRequest.workerSendsAliveMessages(), uris);
                }
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

    public void informFrontierAboutDeadWorker(int idOfWorker, List<CrawleableUri> lstUrisToReassign) {
        if (frontier instanceof ExtendedFrontier) {
            ((ExtendedFrontier) frontier).informAboutDeadWorker(idOfWorker, lstUrisToReassign);
        }
    }

    public void setFrontier(FrontierImpl frontier) {
        this.frontier = frontier;
    }

    public WorkerGuard getWorkerGuard() {
        return workerGuard;
    }
}
