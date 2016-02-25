package org.aksw.simba.squirrel.cli;

import org.aksw.simba.squirrel.data.uri.filter.BlacklistUriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontier;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;

public class ZeroMQBasedFrontierCli {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Usage: java -cp org.aksw.simba.ldspider.cli.ZeroMQBasedFrontierCli squirrel.jar frontierSocketUri");
            System.exit(1);
        }

        String FRONTIER_ADDRESS = args[0];

        IpAddressBasedQueue queue = new InMemoryQueue();
        Frontier frontier = new FrontierImpl(new BlacklistUriFilter(), queue);
        ZeroMQBasedFrontier frontierWrapper = ZeroMQBasedFrontier.create(frontier, FRONTIER_ADDRESS);
        System.out.println("Running frontier...");
        frontierWrapper.run();
    }
}
