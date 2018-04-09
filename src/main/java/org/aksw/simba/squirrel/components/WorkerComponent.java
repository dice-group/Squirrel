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
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.sink.Sink;
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

    private Worker worker;
    private DataSender sender;
    private RabbitRpcClient client;
    private byte[] uriSetRequest;
    private Serializer serializer;

    @Override
    public void init() throws Exception {
        super.init();
        Map<String, String> env = System.getenv();
        String outputFolder = null;
        if (env.containsKey(OUTPUT_FOLDER_KEY)) {
            outputFolder = env.get(OUTPUT_FOLDER_KEY);
        } else {
            String msg = "Couldn't get " + OUTPUT_FOLDER_KEY + " from the environment.";
            throw new Exception(msg);
        }

        sender = DataSenderImpl.builder().queue(outgoingDataQueuefactory, FrontierComponent.FRONTIER_QUEUE_NAME)
            .build();
        client = RabbitRpcClient.create(outgoingDataQueuefactory.getConnection(),
            FrontierComponent.FRONTIER_QUEUE_NAME);

        serializer = new GzipJavaUriSerializer();
        Sink sink = new SparqlBasedSink();
        UriCollector collector = SqlBasedUriCollector.create(serializer);
        worker = new WorkerImpl(this, sink, new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))),
            serializer, collector, 2000, outputFolder + File.separator + "log");
        uriSetRequest = serializer.serialize(new UriSetRequest(worker.getId()));

        serializer = new GzipJavaUriSerializer();
        uriSetRequest = serializer.serialize(new UriSetRequest(worker.getId()));

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    sender.sendData(serializer.serialize(new AliveMessage(worker.getId())));
                } catch (IOException e) {
                    LOGGER.warn(e.toString());
                }
            }
        }, 0, TimeUnit.SECONDS.toMillis(WorkerGuard.TIME_WORKER_DEAD) / 2);

        LOGGER.info("Worker initialized.");
    }

    @Override
    public void run() throws Exception {
        worker.run();
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(sender);
        IOUtils.closeQuietly(client);
        super.close();
    }

    @Override
    public List<CrawleableUri> getNextUris() {
        UriSet set = null;
        try {
            byte[] response = client.request(uriSetRequest);
            if (response != null) {
                set = (UriSet) serializer.deserialize(response);
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
        addNewUris(Arrays.asList(uri));
    }

    @Override
    public void addNewUris(List<CrawleableUri> uris) {
        try {
            sender.sendData(serializer.serialize(new UriSet(uris)));
        } catch (Exception e) {
            LOGGER.error("Exception while sending URIs to the frontier.", e);
        }
    }

    @Override
    public void crawlingDone(List<CrawleableUri> crawledUris, List<CrawleableUri> newUris) {
        try {
            sender.sendData(serializer.serialize(new CrawlingResult(crawledUris, newUris, worker.getId())));
        } catch (Exception e) {
            LOGGER.error("Exception while sending crawl result to the frontier.", e);
        }
    }

    @Override
    public int getNumberOfPendingUris() {
        return 0;
    }

    /*
    The WorkerComponent does not have to implement this method.
     */
    @Override
    public void informAboutDeadWorker(int idOfWorker, List<CrawleableUri> lstUrisToReassign) {
    }

}
