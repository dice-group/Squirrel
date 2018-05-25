package org.aksw.simba.squirrel.components;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.deduplication.hashing.impl.UriHashValueResult;
import org.aksw.simba.squirrel.postprocessing.impl.TripleHashPostProcessor;
import org.aksw.simba.squirrel.rabbit.RespondingDataHandler;
import org.aksw.simba.squirrel.rabbit.ResponseHandler;
import org.aksw.simba.squirrel.rabbit.msgs.UriSet;
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
     * Indicates whether deduplication is active. If it is not active, this component will not do anything. Also,
     * no processing of hash values will be done.
     */
    public static final boolean DEDUPLICATION_ACTIVE = true;

    /**
     * The maximal size for {@link #newUrisBufferList}.
     */
    private static final int MAX_SIZE_NEW_URIS_BUFFER_LIST = 10;

    /**
     * A list of uris for which hash values have already been computed. If size of the list exceeds {@link #MAX_SIZE_NEW_URIS_BUFFER_LIST}
     * send the uris to the frontier.
     */
    private final List<CrawleableUri> newUrisBufferList = new ArrayList<>();

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

    }

    /**
     * Compare the hash values of the uris in {@link #newUrisBufferList} with the hash values of all uris contained
     * in {@link #knownUriFilter}.
     */
    private void compareNewUrisWithOldUris() {
        List<CrawleableUri> allUris = knownUriFilter.getAllUris();
        Set<CrawleableUri> set = new HashSet<>(allUris);
        set.addAll(newUrisBufferList);

        outer:
        for (CrawleableUri uriNew : newUrisBufferList) {
            for (CrawleableUri uriOld : set) {
                if (!uriOld.equals(uriNew)) {
                    if (uriOld.getHashValue().equals(uriNew.getHashValue())) {
                        // get triples from pair1 and pair2 and compare them
                        Set<Triple> set1 = new HashSet<>(sink.getTriplesForGraph(uriOld));
                        Set<Triple> set2 = new HashSet<>(sink.getTriplesForGraph(uriNew));

                        boolean equal = true;
                        for (Triple triple : set1) {
                            if (!set2.contains(triple)) {
                                equal = false;
                                break;
                            }
                        }

                        if (equal) {
                            // TODO: delete duplicate
                            continue outer;
                        }
                    }
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

        if (!DEDUPLICATION_ACTIVE) {
            return;
        }

        Object object = null;
        try {
            object = serializer.deserialize(data);
        } catch (IOException e) {
            LOGGER.error("Error while trying to deserialize incoming data. It will be ignored.", e);
        }
        LOGGER.trace("Got a message (\"{}\").", object.toString());
        if (object != null) {
            if (object instanceof UriSet) {
                UriSet uriSet = (UriSet) object;
                for (CrawleableUri uri : uriSet.uris) {
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
     * Also add uri to {@link #newUrisBufferList} and call {@link #recognizeUriWithComputedHashValue(CrawleableUri)} if necessary.
     *
     * @param uri The new uri with the computed hash value.
     */
    public void recognizeUriWithComputedHashValue(CrawleableUri uri) {
        try {
            newUrisBufferList.add(uri);
            if (newUrisBufferList.size() > MAX_SIZE_NEW_URIS_BUFFER_LIST) {
                compareNewUrisWithOldUris();
                UriHashValueResult result = new UriHashValueResult(newUrisBufferList);
                senderFrontier.sendData(serializer.serialize(result));
            }
            newUrisBufferList.clear();
        } catch (IOException e) {
            LOGGER.error("Error while serializing uri " + uri, e);
        }
    }
}
