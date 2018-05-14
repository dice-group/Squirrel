package org.aksw.simba.squirrel.components;

import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.deduplication.hashing.impl.HashValueUriPair;
import org.aksw.simba.squirrel.sink.TripleBasedSink;
import org.aksw.simba.squirrel.sink.impl.rdfSink.RDFSink;
import org.apache.jena.graph.Triple;
import org.hobbit.core.components.AbstractComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * This component is responsible for deduplication, which means it periodically compares all
 * {@link org.aksw.simba.squirrel.deduplication.hashing.HashValue}s behind all uris with each other.
 * Note: The hash value behind an uri represents the triples behind the uris, it does not represent the uri itself.
 * If The hash values of two uris are equal, it look behind the triples of those two uris and compares them. If the
 * lists of triples are equal, one of the two lists of triples will be deleted as it is a duplicate.
 */
public class DeduplicatorComponent extends AbstractComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeduplicatorComponent.class);

    /**
     * The time that will last between the different executions of deduplication.
     */
    private static final int SLEEP_TIME = 10000;

    /**
     * Needed to access the {@link org.aksw.simba.squirrel.deduplication.hashing.HashValue}s of the uris.
     */
    private KnownUriFilter knownUriFilter;

    /**
     * Needed to access the {@link Triple}s.
     */
    private TripleBasedSink sink;

    /**
     * Indicates whether deduplication is active. If it is not active, this component will not do anything. Also,
     * no processing of hash values will be done.
     */
    public static final boolean deduplicationActive = true;


    @Override
    public void init() {
        if (deduplicationActive) {
            Map<String, String> env = System.getenv();

            String rdbHostName = null;
            int rdbPort = -1;
            if (env.containsKey(FrontierComponent.RDB_HOST_NAME_KEY)) {
                rdbHostName = env.get(FrontierComponent.RDB_HOST_NAME_KEY);
                if (env.containsKey(FrontierComponent.RDB_PORT_KEY)) {
                    rdbPort = Integer.parseInt(env.get(FrontierComponent.RDB_PORT_KEY));
                } else {
                    LOGGER.warn("Couldn't get {} from the environment. An in-memory queue will be used.", FrontierComponent.RDB_PORT_KEY);
                }
            } else {
                LOGGER.warn("Couldn't get {} from the environment. An in-memory queue will be used.", FrontierComponent.RDB_HOST_NAME_KEY);
            }

            if ((rdbHostName != null) && (rdbPort > 0)) {
                knownUriFilter = new RDBKnownUriFilter(rdbHostName, rdbPort, FrontierComponent.doRecrawling);
                knownUriFilter.open();
            }

            // TODO: other kinds of sinks must be possible as well
            sink = new RDFSink();
        }
    }

    @Override
    public void run() {
        if (deduplicationActive) {
            while (true) {
                // periodically compare hash values for all uris
                List<HashValueUriPair> allUrisAndHashValues = knownUriFilter.getAllUrisAndHashValues();

                for (HashValueUriPair pair1 : allUrisAndHashValues) {
                    for (HashValueUriPair pair2 : allUrisAndHashValues) {
                        if (!pair1.uri.equals(pair2.uri)) {
                            if (pair1.hashValue.equals(pair2.hashValue)) {
                                // get triples from pair1 and pair2 and compare them
                                List<Triple> triples1 = sink.getTriplesForGraph(pair1.uri);
                                List<Triple> triples2 = sink.getTriplesForGraph(pair2.uri);
                                boolean equal = true;
                                for (Triple triple : triples1) {
                                    if (!triples2.contains(triple)) {
                                        equal = false;
                                        break;
                                    }
                                }

                                if (equal) {
                                    // TODO: delete duplicate
                                }
                            }
                        }
                    }
                }

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    LOGGER.error("Error while trying to sleep", e);
                }
            }
        }
    }

    @Override
    public void close() {
        knownUriFilter.close();
    }
}
