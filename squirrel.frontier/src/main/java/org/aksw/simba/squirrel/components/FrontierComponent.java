package org.aksw.simba.squirrel.components;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.configurator.RDBConfiguration;
import org.aksw.simba.squirrel.configurator.SeedConfiguration;
import org.aksw.simba.squirrel.configurator.WhiteListConfiguration;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RegexBasedWhiteListFilter;
import org.aksw.simba.squirrel.data.uri.info.URIReferences;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.frontier.ExtendedFrontier;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.ExtendedFrontierImpl;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
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
import org.aksw.simba.squirrel.worker.AliveMessage;
import org.apache.commons.io.FileUtils;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.DataReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontierComponent extends AbstractComponent implements RespondingDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierComponent.class);

    protected IpAddressBasedQueue queue;
    private KnownUriFilter knownUriFilter;
    private URIReferences uriReferences = null;
    private Frontier frontier;
    private RabbitQueue rabbitQueue;
    private DataReceiver receiver;
    private Serializer serializer;
    private final Semaphore terminationMutex = new Semaphore(0);
    private final WorkerGuard workerGuard = new WorkerGuard(this);
    private final boolean doRecrawling = true;
    private long recrawlingTime = 1000L * 60L * 60L * 24L * 30;

    public static final boolean RECRAWLING_ACTIVE = true;

    @Override
    public void init() throws Exception {
        super.init();
        serializer = new GzipJavaUriSerializer();
        RDBConfiguration rdbConfiguration = RDBConfiguration.getRDBConfiguration();
        // WebConfiguration webConfiguration = WebConfiguration.getWebConfiguration();
        if (rdbConfiguration != null) {
            String rdbHostName = rdbConfiguration.getRDBHostName();
            Integer rdbPort = rdbConfiguration.getRDBPort();
            queue = new RDBQueue(rdbHostName, rdbPort, serializer);
            queue.open();

            knownUriFilter = new RDBKnownUriFilter(rdbHostName, rdbPort, doRecrawling);
            ((RDBKnownUriFilter)knownUriFilter).open();
            
            WhiteListConfiguration whiteListConfiguration = WhiteListConfiguration.getWhiteListConfiguration();
            if (whiteListConfiguration != null) {
                File whitelistFile = new File(whiteListConfiguration.getWhiteListURI());
                knownUriFilter = RegexBasedWhiteListFilter.create(knownUriFilter, whitelistFile);
            }

            // TODO Reactivate me but with a different configuration
            // if (webConfiguration.isVisualizationOfCrawledGraphEnabled()) {
            // uriReferences = new RDBURIReferences(rdbHostName, rdbPort);
            // uriReferences.open();
            // }
        } else {
            LOGGER.warn("Couldn't get RDBConfiguration. An in-memory queue will be used.");
            queue = new InMemoryQueue();
            knownUriFilter = new InMemoryKnownUriFilter(doRecrawling, recrawlingTime);
        }

        // Build frontier
        frontier = new ExtendedFrontierImpl(knownUriFilter, uriReferences, queue, doRecrawling);

        rabbitQueue = this.incomingDataQueueFactory.createDefaultRabbitQueue(Constants.FRONTIER_QUEUE_NAME);
        receiver = (new RPCServer.Builder()).responseQueueFactory(outgoingDataQueuefactory).dataHandler(this)
                .maxParallelProcessedMsgs(100).queue(rabbitQueue).build();

        SeedConfiguration seedConfiguration = SeedConfiguration.getSeedConfiguration();
        if (seedConfiguration != null) {
            processSeedFile(seedConfiguration.getSeedFile());
        }

        LOGGER.info("Frontier initialized.");

//        if (webConfiguration.isCommunicationWithWebserviceEnabled()) {
//            final FrontierSenderToWebservice sender = new FrontierSenderToWebservice(outgoingDataQueuefactory,
//                    workerGuard, queue, knownUriFilter, uriReferences);
//            LOGGER.trace("FrontierSenderToWebservice -> sendCrawledGraph is set to "
//                    + webConfiguration.isVisualizationOfCrawledGraphEnabled());
//            Thread senderThread = new Thread(sender);
//            senderThread.setName("Sender to the Webservice via RabbitMQ (current information from the Frontier)");
//            senderThread.start();
//            LOGGER.info("Started thread [" + senderThread.getName() + "] <ID " + senderThread.getId() + " in the state "
//                    + senderThread.getState() + " with the priority " + senderThread.getPriority() + ">");
//        } else {
//            LOGGER.info("webConfiguration.isCommunicationWithWebserviceEnabled is set to "
//                    + webConfiguration.isCommunicationWithWebserviceEnabled() + "/"
//                    + webConfiguration.isVisualizationOfCrawledGraphEnabled()
//                    + ". No WebServiceSenderThread will be started!");
//        }
    }

    @Override
    public void run() throws Exception {
        // The main thread has nothing to do except waiting for its
        // termination...
        terminationMutex.acquire();
    }

    @Override
    public void close() throws IOException {
        if (receiver != null)
            receiver.closeWhenFinished();
        if (queue != null)
            queue.close();
        if (uriReferences != null)
            uriReferences.close();
        if (knownUriFilter instanceof Closeable) {
            ((Closeable)knownUriFilter).close();
        }
        workerGuard.shutdown();
        if (frontier != null)
            frontier.close();
        super.close();
    }

    @Override
    public void handleData(byte[] data) {
        handleData(data, null, null, null);
    }

    @Override
    public void handleData(byte[] data, ResponseHandler handler, String responseQueueName, String correlId) {
        Object deserializedData;
        try {
            deserializedData = serializer.deserialize(data);
        } catch (IOException e) {
            // try to convert the string into a single URI, that maybe comes from the
            // WebService
            // CrawleableUri uri = new CrawleableUriFactoryImpl().create(new String(data));
            // if (uri != null) {
            // LOGGER.warn("Received a single URI " + uri.getUri() + " without a wrapping of
            // \"org.aksw.simba.squirrel.rabbit.frontier\". We converted it into a
            // UriSet.");
            // deserializedData = new UriSet(Collections.singletonList(uri));
            // } else {
            LOGGER.error("Error while trying to deserialize incoming data. It will be ignored.", e);
            return;
            // }
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
                        crawlingResult.uris.size());
                frontier.crawlingDone(crawlingResult.uris);
                workerGuard.removeUrisForWorker(crawlingResult.idOfWorker,
                        crawlingResult.uris);
            } else if (deserializedData instanceof AliveMessage) {
                AliveMessage message = (AliveMessage) deserializedData;
                String idReceived = message.getIdOfWorker();
                LOGGER.trace("Received alive message from worker with id " + idReceived);
                workerGuard.putNewTimestamp(idReceived);
            } else {
                LOGGER.warn("Received an unknown object {}. It will be ignored.", deserializedData.toString());
            }
        }
    }

    private void responseToUriSetRequest(ResponseHandler handler, String responseQueueName, String correlId,
            UriSetRequest uriSetRequest) {
        if (handler != null) {
            // get next UriSet
            try {
                List<CrawleableUri> uris = frontier.getNextUris();
                LOGGER.trace("Responding with a list of {} uris.",
                        uris == null ? "null" : Integer.toString(uris.size()));
                handler.sendResponse(serializer.serialize(new UriSet(uris)), responseQueueName, correlId);
                if (uris != null && uris.size() > 0) {
                    workerGuard.putUrisForWorker(uriSetRequest.getIdOfWorker(),
                            uriSetRequest.workerSendsAliveMessages(), uris);
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

    public void informFrontierAboutDeadWorker(String idOfWorker, List<CrawleableUri> lstUrisToReassign) {
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