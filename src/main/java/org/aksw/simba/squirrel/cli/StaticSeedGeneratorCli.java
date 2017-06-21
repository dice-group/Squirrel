package org.aksw.simba.squirrel.cli;

import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontierClient;
import org.aksw.simba.squirrel.seed.generator.impl.StaticSeedGeneratorImpl;

/**
 * Created by ivan on 11.02.16.
 */
@Deprecated
public class StaticSeedGeneratorCli {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Usage: java -cp org.aksw.simba.squirrel.cli.StaticSeedGeneratorCli squirrel.jar frontierSocketUri");
            System.exit(1);
        }

        String FRONTIER_ADDRESS = args[0];
        Integer WORKER_ID = 1000001;

        StaticSeedGeneratorImpl seedGenerator = new StaticSeedGeneratorImpl(ZeroMQBasedFrontierClient.create(FRONTIER_ADDRESS, WORKER_ID));
        System.out.println("Running Seed Generator...");
        seedGenerator.run();
    }
}
