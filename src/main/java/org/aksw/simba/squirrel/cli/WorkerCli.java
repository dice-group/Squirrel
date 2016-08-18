package org.aksw.simba.squirrel.cli;

import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontierClient;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

import java.io.File;
import java.io.IOException;

public class WorkerCli {
    private static File tempDirectory = null;

    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.out.println("Usage: java -cp org.aksw.simba.ldspider.cli.WorkerCli squirrel.jar workerId frontierSocketUri sinkSocketUri");
            System.exit(1);
        }

        Integer WORKER_ID = Integer.parseInt(args[0]);
        String FRONTIER_ADDRESS = args[1];
        String SINK_FOLDER = args[2];

        File tempFile = File.createTempFile("FileBasedSinkTest", ".tmp");
        tempDirectory = tempFile.getAbsoluteFile().getParentFile();

        Sink sink = createSink(false);

        Worker worker = new WorkerImpl(ZeroMQBasedFrontierClient.create(FRONTIER_ADDRESS, WORKER_ID), sink,
                new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))), 2000);
        System.out.println("Running worker...");
        worker.run();
    }

    protected static Sink createSink(boolean useCompression) {
        return new FileBasedSink(tempDirectory, useCompression);
    }
}
