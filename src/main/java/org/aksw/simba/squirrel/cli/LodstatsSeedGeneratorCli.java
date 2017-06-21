package org.aksw.simba.squirrel.cli;

import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontierClient;
import org.aksw.simba.squirrel.seed.generator.impl.LodstatsSeedGeneratorImpl;

/**
 * Created by ivan on 03.03.16.
 */
@Deprecated
public class LodstatsSeedGeneratorCli {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Usage: java -cp org.aksw.simba.squirrel.cli.LodstatsSeedGeneratorCli squirrel.jar frontierSocketUri");
            System.exit(1);
        }

        String FRONTIER_ADDRESS = args[0];
        Integer WORKER_ID = 1000002;

        LodstatsSeedGeneratorImpl seedGenerator = new LodstatsSeedGeneratorImpl(ZeroMQBasedFrontierClient.create(FRONTIER_ADDRESS, WORKER_ID));
        System.out.println("Running Lodstats Seed Generator...");
        seedGenerator.run();
    }
}