package org.aksw.simba.squirrel.components;

import java.io.File;
import java.util.Map;

import org.aksw.simba.squirrel.configurator.RDBConfiguration;
import org.aksw.simba.squirrel.configurator.SeedConfiguration;
import org.aksw.simba.squirrel.configurator.WhiteListConfiguration;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RegexBasedWhiteListFilter;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.RDBQueue;
import org.aksw.simba.squirrel.rabbit.RPCServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteListFrontierComponent extends FrontierComponent{

	private static final Logger LOGGER = LoggerFactory.getLogger(WhiteListFrontierComponent.class);

	private KnownUriFilter filter;

	 @Override
	    public void init() throws Exception {
	        super.init();
	        serializer = new GzipJavaUriSerializer();

            RDBConfiguration rdbConfiguration = RDBConfiguration.getRDBConfiguration();
            if(rdbConfiguration != null) {
                queue = new RDBQueue(rdbConfiguration.getRDBHostName(),
                    rdbConfiguration.getRDBPort(),serializer);
                queue.open();

                WhiteListConfiguration whiteListConfiguration = WhiteListConfiguration.getWhiteListConfiguration();
                if(whiteListConfiguration != null) {
                    File whitelistFile = new File(whiteListConfiguration.getWhiteListURI());
                    filter = new RegexBasedWhiteListFilter(rdbConfiguration.getRDBHostName(),
                        rdbConfiguration.getRDBPort(), whitelistFile);
                    filter.open();
                } else {

                    filter = new RDBKnownUriFilter(rdbConfiguration.getRDBHostName(),
                        rdbConfiguration.getRDBPort());
                    filter.open();
                }
            } else {
	            queue = new InMemoryQueue();
	            filter = new InMemoryKnownUriFilter(-1);
	        }

	        // Build frontier
	        frontier = new FrontierImpl(filter, queue);

	        rabbitQueue = this.incomingDataQueueFactory.createDefaultRabbitQueue(FRONTIER_QUEUE_NAME);
	        receiver = (new RPCServer.Builder()).responseQueueFactory(outgoingDataQueuefactory).dataHandler(this)
	                .maxParallelProcessedMsgs(100).queue(rabbitQueue).build();

            SeedConfiguration seedConfiguration = SeedConfiguration.getSeedConfiguration();
	        if (seedConfiguration != null) {
	            processSeedFile(seedConfiguration.getSeedFile());
	        }

	        LOGGER.info("Frontier initialized.");
	    }
}
