package org.aksw.simba.squirrel.components;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import org.aksw.simba.squirrel.collect.SqlBasedUriCollector;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.WorkerGuard;
import org.aksw.simba.squirrel.rabbit.msgs.CrawlingResult;
import org.aksw.simba.squirrel.rabbit.msgs.UriSet;
import org.aksw.simba.squirrel.rabbit.msgs.UriSetRequest;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlUtils;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.AliveMessage;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;
import org.apache.commons.io.IOUtils;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.rabbit.DataSender;
import org.hobbit.core.rabbit.DataSenderImpl;
import org.hobbit.core.rabbit.RabbitRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WorkerComponent extends AbstractComponent implements Frontier, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerComponent.class);

    public static final String OUTPUT_FOLDER_KEY = "OUTPUT_FOLDER";

    /**
     * Indicates whether deduplication is active. If it is not active, this component will send data to the deduplicator.
     */
    private static boolean deduplicationActive;

    private Worker worker;
    private DataSender senderFrontier, senderDeduplicator;
    private RabbitRpcClient clientFrontier;
    private byte[] uriSetRequest;
    private Serializer serializer;
    private Timer timerAliveMessages;

    @Override
    public void init() throws Exception {
        super.init();
        Map<String, String> env = System.getenv();
        String outputFolder;
        if (env.containsKey(OUTPUT_FOLDER_KEY)) {
            outputFolder = env.get(OUTPUT_FOLDER_KEY);
        } else {
            String msg = "Couldn't get " + OUTPUT_FOLDER_KEY + " from the environment.";
            throw new Exception(msg);
        }
        if (env.containsKey(DeduplicatorComponent.DEDUPLICATION_ACTIVE_KEY)) {
            deduplicationActive = Boolean.parseBoolean(env.get(DeduplicatorComponent.DEDUPLICATION_ACTIVE_KEY));
        } else {
            LOGGER.warn("Couldn't get {} from the environment. The default value will be used.", DeduplicatorComponent.DEDUPLICATION_ACTIVE_KEY);
            deduplicationActive = DeduplicatorComponent.DEFAULT_DEDUPLICATION_ACTIVE;
        }

        senderFrontier = DataSenderImpl.builder().queue(outgoingDataQueuefactory, FrontierComponent.FRONTIER_QUEUE_NAME)
            .build();

        if (deduplicationActive) {
            senderDeduplicator = DataSenderImpl.builder().queue(outgoingDataQueuefactory, DeduplicatorComponent.DEDUPLICATOR_QUEUE_NAME)
                .build();
        }
        clientFrontier = RabbitRpcClient.create(outgoingDataQueuefactory.getConnection(),
            FrontierComponent.FRONTIER_QUEUE_NAME);

        serializer = new GzipJavaUriSerializer();
        Sink sink = new SparqlBasedSink(SparqlUtils.getDatasetUriForUpdate(), SparqlUtils.getDatasetUriForQuery());
        UriCollector collector = SqlBasedUriCollector.create(serializer);
        worker = new WorkerImpl(this, sink, new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))),
            serializer, collector, 2000, outputFolder + File.separator + "log", true);
        uriSetRequest = serializer.serialize(new UriSetRequest(worker.getId(), worker.sendsAliveMessages()));

        serializer = new GzipJavaUriSerializer();
        uriSetRequest = serializer.serialize(new UriSetRequest(worker.getId(), worker.sendsAliveMessages()));

        if (worker.sendsAliveMessages()) {
            timerAliveMessages = new Timer();
            timerAliveMessages.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        senderFrontier.sendData(serializer.serialize(new AliveMessage(worker.getId())));
                    } catch (IOException e) {
                        LOGGER.warn(e.toString());
                    }
                }
            }, 0, TimeUnit.SECONDS.toMillis(WorkerGuard.TIME_WORKER_DEAD) / 2);

        }
        LOGGER.info("Worker initialized.");
    }

    @Override
    public void run() {
        worker.run();
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(senderFrontier);
        if (deduplicationActive) {
            IOUtils.closeQuietly(senderDeduplicator);
        }
        IOUtils.closeQuietly(clientFrontier);
        timerAliveMessages.cancel();
        super.close();
    }

    @Override
    public List<CrawleableUri> getNextUris() {
        UriSet set = null;
        try {
            byte[] response = clientFrontier.request(uriSetRequest);
            if (response != null) {
                set = serializer.deserialize(response);
            }
        } catch (IOException e) {
            LOGGER.error("Error while requesting the next set of URIs.", e);
        }
        if ((set == null) || (set.uris == null) || (set.uris.size() == 0)) {
            return null;
        } else {
            return set.uris;
        }
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }


    @Override
    public void addNewUri(CrawleableUri uri) {
        addNewUris(Collections.singletonList(uri));
    }

    @Override
    public void addNewUris(List<CrawleableUri> uris) {
        try {
            senderFrontier.sendData(serializer.serialize(new UriSet(uris)));
        } catch (Exception e) {
            LOGGER.error("Exception while sending URIs to the frontier.", e);
        }
    }

    @Override
    public void crawlingDone(List<CrawleableUri> crawledUris, List<CrawleableUri> newUris) {
        try {
            senderFrontier.sendData(serializer.serialize(new CrawlingResult(crawledUris, newUris, worker.getId())));

            if (deduplicationActive) {
                UriSet uriSet = new UriSet();
                uriSet.uris = crawledUris;
                senderDeduplicator.sendData(serializer.serialize(uriSet));
            }
        } catch (Exception e) {
            LOGGER.error("Exception while sending crawl result.", e);
        }
    }

    @Override
    public int getNumberOfPendingUris() {
        return 0;
    }

    @Override
    public boolean doesRecrawling() {
        return false;
    }

}
