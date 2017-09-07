package org.aksw.simba.squirrel.components;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.rabbit.RabbitMQHelper;
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

    private static final String OUTPUT_FOLDER_KEY = "OUTPUT_FOLDER";

    private RabbitMQHelper rabbitHelper;
    private Worker worker;
    private DataSender sender;
    private RabbitRpcClient client;
    private byte[] uriSetRequest;

    @Override
    public void init() throws Exception {
        super.init();
        rabbitHelper = new RabbitMQHelper();
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

        uriSetRequest = rabbitHelper.writeObject(new UriSetRequest());

        Sink sink = new FileBasedSink(new File(outputFolder), true);
        worker = new WorkerImpl(this, sink, new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))),
                2000, outputFolder + File.separator + "log");
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
        UriSet set = (UriSet) rabbitHelper.parseObject(client.request(uriSetRequest));
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
            sender.sendData(rabbitHelper.writeObject(new UriSet(uris)));
        } catch (Exception e) {
            LOGGER.error("Exception while sending URIs to the frontier.", e);
        }
    }

    @Override
    public void crawlingDone(List<CrawleableUri> crawledUris, List<CrawleableUri> newUris) {
        try {
            sender.sendData(rabbitHelper.writeObject(new CrawlingResult(crawledUris, newUris)));
        } catch (Exception e) {
            LOGGER.error("Exception while sending crawl result to the frontier.", e);
        }
    }

    @Override
    public int getNumberOfPendingUris() {
        return 0;
    }

}
