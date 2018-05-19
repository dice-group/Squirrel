package org.aksw.simba.squirrel.components;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.postprocessing.impl.TripleHashPostProcessor;
import org.aksw.simba.squirrel.rabbit.RespondingDataHandler;
import org.aksw.simba.squirrel.rabbit.ResponseHandler;
import org.aksw.simba.squirrel.sink.TripleBasedSink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.apache.jena.graph.Triple;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.rabbit.DataSender;
import org.hobbit.core.rabbit.DataSenderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * This component is responsible for deduplication, which means it periodically compares all
 * {@link org.aksw.simba.squirrel.deduplication.hashing.HashValue}s behind all uris with each other.
 * Note: The hash value behind an uri represents the triples behind the uris, it does not represent the uri itself.
 * If The hash values of two uris are equal, it look behind the triples of those two uris and compares them. If the
 * lists of triples are equal, one of the two lists of triples will be deleted as it is a duplicate.
 */
public class DeduplicatorComponent extends AbstractComponent implements RespondingDataHandler {

    public static final String DEDUPLICATOR_QUEUE_NAME = "deduplicatorQueue";

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

    private Serializer serializer;

    private DataSender senderFrontier;

    /**
     * Indicates whether deduplication is active. If it is not active, this component will not do anything. Also,
     * no processing of hash values will be done.
     */
    public static final boolean DEDUPLICATION_ACTIVE = true;


    @Override
    public void init() {
        if (DEDUPLICATION_ACTIVE) {
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
            sink = new SparqlBasedSink(null, null);

            serializer = new GzipJavaUriSerializer();

            try {
                senderFrontier = DataSenderImpl.builder().queue(outgoingDataQueuefactory, FrontierComponent.FRONTIER_QUEUE_NAME)
                    .build();
            } catch (IOException e) {
                LOGGER.error("Error while creating sender object.", e);
            }
        }
    }

    @Override
    public void run() {
        if (DEDUPLICATION_ACTIVE) {
            while (true) {
                // periodically compare hash values for all uris
                List<CrawleableUri> allUris = knownUriFilter.getAllUris();

                for (CrawleableUri uri1 : allUris) {
                    for (CrawleableUri uri2 : allUris) {
                        if (!uri1.equals(uri2)) {
                            if (uri1.getHashValue().equals(uri2.getHashValue())) {
                                // get triples from pair1 and pair2 and compare them
                                Set<Triple> set1 = new HashSet<>(sink.getTriplesForGraph(uri1));
                                Set<Triple> set2 = new HashSet<>(sink.getTriplesForGraph(uri2));

                                boolean equal = true;
                                for (Triple triple : set1) {
                                    if (!set2.contains(triple)) {
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

    @Override
    public void handleData(byte[] data, ResponseHandler handler, String responseQueueName, String correlId) {
        Object object = null;
        try {
            object = serializer.deserialize(data);
        } catch (IOException e) {
            LOGGER.error("Error while trying to deserialize incoming data. It will be ignored.", e);
        }
        LOGGER.trace("Got a message (\"{}\").", object.toString());
        if (object != null) {
            if (object instanceof List) {
                List<CrawleableUri> uris = (List<CrawleableUri>) object;
                for (CrawleableUri uri : uris) {
                    List<Triple> triples = new ArrayList<>();
                    TripleHashPostProcessor tripleHashPostProcessor = new TripleHashPostProcessor(this, triples, uri);
                    tripleHashPostProcessor.run();
                }
            }
        }
    }

    @Override
    public void handleData(byte[] bytes) {
        handleData(bytes, null, null, null);
    }

    /**
     * Send an uri for which a {@link org.aksw.simba.squirrel.deduplication.hashing.HashValue} has been computed.
     *
     * @param uri
     */
    public void sendUriWithComputedHashValue(CrawleableUri uri) {
        try {
            senderFrontier.sendData(serializer.serialize(uri));
        } catch (IOException e) {
            LOGGER.error("Error while serializing uri " + uri, e);
        }
    }
}
