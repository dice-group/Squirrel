package org.aksw.simba.squirrel.cli;

import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontierClient;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

public class WorkerCli {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Usage: java -cp org.aksw.simba.ldspider.cli.WorkerCli ldspider.jar workerId frontierSocketUri sinkSocketUri");
            System.exit(1);
        }

        Integer WORKER_ID = Integer.parseInt(args[0]);
        String FRONTIER_ADDRESS = args[1];
        String SINK_ADDRESS = args[2];

        Worker worker = new WorkerImpl(ZeroMQBasedFrontierClient.create(FRONTIER_ADDRESS, WORKER_ID), null,
                new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))), 2000);
        System.out.println("Running worker...");
        worker.run();
    }
}
