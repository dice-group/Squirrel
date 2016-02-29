package org.aksw.simba.squirrel.cli;

import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontierClient;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

import java.io.File;

public class WorkerCli {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Usage: java -cp org.aksw.simba.ldspider.cli.WorkerCli squirrel.jar workerId frontierSocketUri sinkSocketUri");
            System.exit(1);
        }

        Integer WORKER_ID = Integer.parseInt(args[0]);
        String FRONTIER_ADDRESS = args[1];
        String SINK_FOLDER = args[2];

        FileBasedSink sink = new FileBasedSink(new File(SINK_FOLDER), false);

        Worker worker = new WorkerImpl(ZeroMQBasedFrontierClient.create(FRONTIER_ADDRESS, WORKER_ID), sink,
                new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))), 2000);
        System.out.println("Running worker...");
        worker.run();
    }
}
