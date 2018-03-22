package org.aksw.simba.squirrel.components;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.aksw.simba.squirrel.collect.SqlBasedUriCollector;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.configurator.RobotsManagerConfiguration;
import org.aksw.simba.squirrel.configurator.WorkerConfiguration;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.rabbit.msgs.CrawlingResult;
import org.aksw.simba.squirrel.rabbit.msgs.UriSet;
import org.aksw.simba.squirrel.rabbit.msgs.UriSetRequest;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;
import org.apache.commons.io.IOUtils;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.rabbit.DataSender;
import org.hobbit.core.rabbit.DataSenderImpl;
import org.hobbit.core.rabbit.RabbitRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

public class WorkerComponent extends AbstractComponent implements Frontier {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerComponent.class);

    private Worker worker;
    private DataSender sender;
    private RabbitRpcClient client;
    private byte[] uriSetRequest;
    private Serializer serializer;

    @Override
    public void init() throws Exception {
        super.init();

        WorkerConfiguration workerConfiguration = WorkerConfiguration.getWorkerConfiguration();
        String outputFolder = workerConfiguration.getOutputFolder();

        RobotsManagerImpl robotsmanager = new RobotsManagerImpl(
            new SimpleHttpFetcher(
                new UserAgent("Test", "", "")
            )
        );
        RobotsManagerConfiguration robotsManagerConfiguration = RobotsManagerConfiguration.getRobotsManagerConfiguration();
        if(robotsManagerConfiguration != null) {
            robotsmanager.setDefaultMinWaitingTime(robotsManagerConfiguration.getMinDelay());
        }

        sender = DataSenderImpl.builder()
            .queue(outgoingDataQueuefactory, FrontierComponent.FRONTIER_QUEUE_NAME)
            .build();
        client = RabbitRpcClient.create(outgoingDataQueuefactory.getConnection(),
                                        FrontierComponent.FRONTIER_QUEUE_NAME);

        serializer = new GzipJavaUriSerializer();
        uriSetRequest = serializer.serialize(new UriSetRequest());
        UriCollector collector = SqlBasedUriCollector.create(serializer);

        Sink sink = new FileBasedSink(new File(outputFolder), true);
        worker = new WorkerImpl(this, sink, robotsmanager, serializer, collector, 2000,
                outputFolder + File.separator + "log");
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
            sender.sendData(serializer.serialize(new CrawlingResult(crawledUris, newUris)));
        } catch (Exception e) {
            LOGGER.error("Exception while sending crawl result to the frontier.", e);
        }
    }

    @Override
    public int getNumberOfPendingUris() {
        return 0;
    }
}
