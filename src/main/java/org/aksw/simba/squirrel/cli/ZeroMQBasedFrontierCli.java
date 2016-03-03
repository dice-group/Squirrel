package org.aksw.simba.squirrel.cli;

import java.io.File;
import java.io.FileNotFoundException;

import org.aksw.simba.squirrel.data.uri.filter.BlacklistUriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontier;
import org.aksw.simba.squirrel.graph.GraphLogger;
import org.aksw.simba.squirrel.graph.impl.TabSeparatedGraphLogger;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeroMQBasedFrontierCli {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroMQBasedFrontierCli.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(
                    "Usage: java -cp org.aksw.simba.ldspider.cli.ZeroMQBasedFrontierCli squirrel.jar frontierSocketUri");
            System.exit(1);
        }

        String FRONTIER_ADDRESS = args[0];

        GraphLogger graphLogger = null;
        try {
            graphLogger = TabSeparatedGraphLogger.create(new File("graph.log"));
        } catch (FileNotFoundException e) {
            LOGGER.error(
                    "Exception while creating output file for graph data. The crawler will be started without this output.",
                    e);
        }

        IpAddressBasedQueue queue = new InMemoryQueue();
        Frontier frontier = new FrontierImpl(new BlacklistUriFilter(), queue, graphLogger);
        ZeroMQBasedFrontier frontierWrapper = ZeroMQBasedFrontier.create(frontier, FRONTIER_ADDRESS);
        System.out.println("Running frontier...");
        frontierWrapper.run();
    }
}
