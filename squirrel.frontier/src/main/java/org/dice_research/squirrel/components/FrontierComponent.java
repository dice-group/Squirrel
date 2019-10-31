package org.dice_research.squirrel.components;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.FileUtils;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.configurator.MongoConfiguration;
import org.dice_research.squirrel.configurator.SeedConfiguration;
import org.dice_research.squirrel.configurator.WebConfiguration;
import org.dice_research.squirrel.configurator.WhiteListConfiguration;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.UriSeedReader;
import org.dice_research.squirrel.data.uri.UriUtils;
import org.dice_research.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.dice_research.squirrel.data.uri.filter.KnownUriFilter;
import org.dice_research.squirrel.data.uri.filter.RegexBasedWhiteListFilter;
import org.dice_research.squirrel.data.uri.info.URIReferences;
import org.dice_research.squirrel.data.uri.norm.NormalizerImpl;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.frontier.ExtendedFrontier;
import org.dice_research.squirrel.frontier.Frontier;
import org.dice_research.squirrel.frontier.impl.ExtendedFrontierImpl;
import org.dice_research.squirrel.frontier.impl.FrontierImpl;
import org.dice_research.squirrel.frontier.impl.FrontierSenderToWebservice;
import org.dice_research.squirrel.frontier.impl.QueueBasedTerminationCheck;
import org.dice_research.squirrel.frontier.impl.TerminationCheck;
import org.dice_research.squirrel.frontier.impl.WorkerGuard;
import org.dice_research.squirrel.queue.InMemoryQueue;
import org.dice_research.squirrel.queue.IpAddressBasedQueue;
import org.dice_research.squirrel.queue.UriQueue;
import org.dice_research.squirrel.rabbit.RPCServer;
import org.dice_research.squirrel.rabbit.RespondingDataHandler;
import org.dice_research.squirrel.rabbit.ResponseHandler;
import org.dice_research.squirrel.rabbit.msgs.CrawlingResult;
import org.dice_research.squirrel.rabbit.msgs.UriSet;
import org.dice_research.squirrel.rabbit.msgs.UriSetRequest;
import org.dice_research.squirrel.worker.AliveMessage;
import org.dice_research.squirrel.worker.WorkerInfo;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.DataReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dice_research.squirrel.predictor.PredictorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("frontierComponent")
public class FrontierComponent extends AbstractComponent implements RespondingDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierComponent.class);

    @Qualifier("queueBean")
    @Autowired
    protected UriQueue queue;
    @Qualifier("knowUriFilterBean")
    @Autowired
    private KnownUriFilter knownUriFilter;
    private URIReferences uriReferences = null;
    private Frontier frontier;
    private RabbitQueue rabbitQueue;
    private DataReceiver receiver;
    @Qualifier("serializerBean")
    @Autowired
    private Serializer serializer;
    private final Semaphore terminationMutex = new Semaphore(0);
    private final WorkerGuard workerGuard = new WorkerGuard(this);
    private final boolean doRecrawling = true;
    private long recrawlingTime = 1000L * 60L * 60L * 24L * 30;


    private Timer timerTerminator;


    public static final boolean RECRAWLING_ACTIVE = true;
    public PredictorImpl pred = new PredictorImpl();

    @Override
    public void init() throws Exception {
        super.init();
        serializer = new GzipJavaUriSerializer();
        MongoConfiguration mongoConfiguration = MongoConfiguration.getMDBConfiguration();
        WebConfiguration webConfiguration = WebConfiguration.getWebConfiguration();
        if (mongoConfiguration != null) {

            queue.open();
            knownUriFilter.open();

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
            LOGGER.warn("Couldn't get MDBConfiguration. An in-memory queue will be used.");
            queue = new InMemoryQueue();
            knownUriFilter = new InMemoryKnownUriFilter(doRecrawling, recrawlingTime);
        }
        // Training the URI predictor model with a training dataset
        pred.train("https://hobbitdata.informatik.uni-leipzig.de/squirrel/lodstats-seeds.csv");
        // Build frontier
        frontier = new ExtendedFrontierImpl(new NormalizerImpl(), knownUriFilter, uriReferences, (IpAddressBasedQueue) queue, doRecrawling, pred);

        rabbitQueue = this.incomingDataQueueFactory.createDefaultRabbitQueue(Constants.FRONTIER_QUEUE_NAME);
        receiver = (new RPCServer.Builder()).responseQueueFactory(outgoingDataQueuefactory).dataHandler(this)
                .maxParallelProcessedMsgs(100).queue(rabbitQueue).build();

        SeedConfiguration seedConfiguration = SeedConfiguration.getSeedConfiguration();
        if (seedConfiguration != null) {
            processSeedFile(seedConfiguration.getSeedFile());
        }

        LOGGER.info("Frontier initialized.");

        if (webConfiguration.isCommunicationWithWebserviceEnabled()) {
            final FrontierSenderToWebservice sender = new FrontierSenderToWebservice(outgoingDataQueuefactory,
                    workerGuard, queue, knownUriFilter, uriReferences);
            LOGGER.trace("FrontierSenderToWebservice -> sendCrawledGraph is set to "
                    + webConfiguration.isVisualizationOfCrawledGraphEnabled());
            Thread senderThread = new Thread(sender);
            senderThread.setName("Sender to the Webservice via RabbitMQ (current information from the Frontier)");
            senderThread.start();
            LOGGER.info("Started thread [" + senderThread.getName() + "] <ID " + senderThread.getId() + " in the state "
                    + senderThread.getState() + " with the priority " + senderThread.getPriority() + ">");
        } else {
            LOGGER.info("webConfiguration.isCommunicationWithWebserviceEnabled is set to "
                    + webConfiguration.isCommunicationWithWebserviceEnabled() + "/"
                    + webConfiguration.isVisualizationOfCrawledGraphEnabled()
                    + ". No WebServiceSenderThread will be started!");
        }


    }

    @Override
    public void run() throws Exception {
        
        terminationMutex.acquire();
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("Closing Frontier Component.");
        timerTerminator.cancel();
        if (receiver != null)
            // Force the receiver to close
            receiver.close();
//         receiver.closeWhenFinished();

        if (queue != null)
            queue.close();
        if (uriReferences != null)
            uriReferences.close();
        if (knownUriFilter instanceof Closeable) {
            ((Closeable) knownUriFilter).close();
        }
        workerGuard.shutdown();
        if (frontier != null)
            frontier.close();
        super.close();
        LOGGER.info("Frontier Component Closed.");
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
            if (deserializedData instanceof UriSetRequest) {
                responseToUriSetRequest(handler, responseQueueName, correlId, (UriSetRequest) deserializedData);
            } else if (deserializedData instanceof UriSet) {
            	
            	if(timerTerminator == null) {
            		LOGGER.info("Initializing Terminator task...");
                	TimerTask terminatorTask = new TerminatorTask(queue, terminationMutex, this.workerGuard);
                    timerTerminator = new Timer();
                    timerTerminator.schedule(terminatorTask, 5000, 5000);
            	}
//                LOGGER.warn("Received a set of URIs (size={}).", ((UriSet) deserializedData).uris.size());
                frontier.addNewUris(((UriSet) deserializedData).uris);
            } else if (deserializedData instanceof CrawlingResult) {
                CrawlingResult crawlingResult = (CrawlingResult) deserializedData;
                LOGGER.warn("Received the message that the crawling for {} URIs is done.", crawlingResult.uris.size());

                frontier.crawlingDone(crawlingResult.uris);
                workerGuard.removeUrisForWorker(crawlingResult.idOfWorker, crawlingResult.uris);
            } else if (deserializedData instanceof AliveMessage) {
                AliveMessage message = (AliveMessage) deserializedData;
                String idReceived = message.getWorkerId();
                LOGGER.warn("Received alive message from worker with id " + idReceived);
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
                    workerGuard.putUrisForWorker(uriSetRequest.getWorkerId(), uriSetRequest.workerSendsAliveMessages(),
                            uris);
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
            List<CrawleableUri> listSeeds = new UriSeedReader(seedFile).getUris();
            if (!listSeeds.isEmpty())
                frontier.addNewUris(listSeeds);

            List<String> lines = FileUtils.readLines(new File(seedFile), StandardCharsets.UTF_8);
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

    private static class TerminatorTask extends TimerTask {

        private UriQueue queue;
        private TerminationCheck terminationCheck = new QueueBasedTerminationCheck();
        private Semaphore terminationMutex;
        private WorkerGuard workerGuard;

        public TerminatorTask(UriQueue queue, Semaphore terminationMutex, WorkerGuard workerGuard) {
            this.queue = queue;
            this.terminationMutex = terminationMutex;
            this.workerGuard = workerGuard;
        }

        @Override
        public void run() {

            Map<String, WorkerInfo> mapWorkers = this.workerGuard.getMapWorkerInfo();

            boolean stillHasUris = false;
            for (Entry<String, WorkerInfo> entry : mapWorkers.entrySet()) {
                if (entry.getValue().getUrisCrawling().size() > 0) {
                    stillHasUris = true;
                    break;
                }
            }
            
            LOGGER.info("Still has Uris: " + stillHasUris);

			if(!stillHasUris && terminationCheck.shouldFrontierTerminate(queue)) {
	        	terminationMutex.release();
	        }			
        }

    }
}
