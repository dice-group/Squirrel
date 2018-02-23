package org.aksw.simba.squirrel.components;

import java.io.File;
import java.util.Map;

import org.aksw.simba.squirrel.configurator.RDBConfiguration;
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
	private static final String URI_WHITELIST_FILE = "URI_WHITELIST_FILE";

	private KnownUriFilter filter;

	 @Override
	    public void init() throws Exception {
	        super.init();
	        serializer = new GzipJavaUriSerializer();
	        Map<String, String> env = System.getenv();

	        String rdbHostName = null;
	        int rdbPort = -1;
            RDBConfiguration rdbConfiguration = RDBConfiguration.getRDBConfiguration();
            if(rdbConfiguration != null) {
                queue = new RDBQueue(rdbConfiguration.getRDBHostName(),
                    rdbConfiguration.getRDBPort());
                queue.open();

                if(System.getenv(URI_WHITELIST_FILE) != null) {
                    LOGGER.warn("{} found, loading it...", URI_WHITELIST_FILE);
                    File whitelistFile = new File(System.getenv(URI_WHITELIST_FILE));
                    filter = new RegexBasedWhiteListFilter(rdbHostName, rdbPort, whitelistFile);
                    ((RDBKnownUriFilter) filter).open();
                } else {
                    LOGGER.warn("Couldn't get {} from the environment. Ignoring...", URI_WHITELIST_FILE);
                    filter = new RDBKnownUriFilter(rdbHostName, rdbPort);
                    ((RDBKnownUriFilter) filter).open();
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
	        if (env.containsKey(SEED_FILE_KEY)) {
	            processSeedFile(env.get(SEED_FILE_KEY));
	        }
	        LOGGER.info("Frontier initialized.");
	    }
}
