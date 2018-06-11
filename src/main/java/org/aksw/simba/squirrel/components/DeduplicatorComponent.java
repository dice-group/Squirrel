package org.aksw.simba.squirrel.components;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.deduplication.hashing.HashValue;
import org.aksw.simba.squirrel.deduplication.hashing.TripleComparator;
import org.aksw.simba.squirrel.deduplication.hashing.UriHashCustodian;
import org.aksw.simba.squirrel.deduplication.hashing.impl.SimpleTripleComparator;
import org.aksw.simba.squirrel.deduplication.hashing.impl.SimpleTripleHashFunction;
import org.aksw.simba.squirrel.deduplication.hashing.impl.UriHashValueResult;
import org.aksw.simba.squirrel.postprocessing.impl.TripleHashPostProcessor;
import org.aksw.simba.squirrel.rabbit.RespondingDataHandler;
import org.aksw.simba.squirrel.rabbit.ResponseHandler;
import org.aksw.simba.squirrel.rabbit.msgs.UriSet;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlUtils;
import org.aksw.simba.squirrel.sink.tripleBased.AdvancedTripleBasedSink;
import org.apache.jena.graph.Triple;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.DataReceiverImpl;
import org.hobbit.core.rabbit.DataSender;
import org.hobbit.core.rabbit.DataSenderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * This component is responsible for deduplication.
 * If some new uris have been crawled by the {@link org.aksw.simba.squirrel.worker.Worker} the deduplicator computes
 * {@link org.aksw.simba.squirrel.deduplication.hashing.HashValue}s for the triples found in the new uris, stores those
 * hash values in the {@link KnownUriFilter} and compares the newly computed hash values with the hash values of all 'old'
 * triples. By doing that, duplicate data can be found and eliminated.
 * Note: The hash value behind a uri represents the triples behind the uris, it does not represent the uri itself.
 * If The hash values of two uris are equal, the deduplicator looks behind the triples of those two uris and compares them. If the
 * lists of triples are equal, one of the two lists of triples will be deleted as it is a duplicate.
 */
public class DeduplicatorComponent extends AbstractComponent implements RespondingDataHandler {

    public static final String DEDUPLICATOR_QUEUE_NAME = "squirrel.deduplicator";

    private static final Logger LOGGER = LoggerFactory.getLogger(DeduplicatorComponent.class);

    /**
     * Indicates whether deduplication is active. If it is not active, this component will not do anything. Also,
     * no processing of hash values will be done.
     */
    public static final boolean DEDUPLICATION_ACTIVE = true;

    /**
     * The maximal size for {@link #newUrisBufferSet}.
     */
    private static final int MAX_SIZE_NEW_URIS_BUFFER_LIST = 100;

    /**
     * A set of uris for which hash values have already been computed. If the size of the set exceeds {@link #MAX_SIZE_NEW_URIS_BUFFER_LIST}
     * the uris will be sent to the frontier and the set will be cleared.
     */
    private final Set<CrawleableUri> newUrisBufferSet = new HashSet<>(MAX_SIZE_NEW_URIS_BUFFER_LIST);

    /**
     * Needed to access the {@link org.aksw.simba.squirrel.deduplication.hashing.HashValue}s of the uris.
     */
    private KnownUriFilter knownUriFilter;

    /**
     * Needed to access the {@link Triple}s.
     */
    private AdvancedTripleBasedSink sink;

    private Serializer serializer;

    private DataSender senderFrontier;
    private DataReceiverImpl receiver;

    private TripleComparator tripleComparator = new SimpleTripleComparator();

    private UriHashCustodian uriHashCustodian;

    @Override
    public void init() throws Exception {
        super.init();
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
                knownUriFilter = new RDBKnownUriFilter(rdbHostName, rdbPort, FrontierComponent.RECRAWLING_ACTIVE);
                knownUriFilter.open();
            } else {
                knownUriFilter = new InMemoryKnownUriFilter(FrontierComponent.RECRAWLING_ACTIVE);
            }

            // at the moment, RDBKnownUriFilter is the only implementation of UriHashCustodian, that might change in the future
            if (knownUriFilter instanceof UriHashCustodian) {
                uriHashCustodian = (UriHashCustodian) knownUriFilter;
            } else {
                LOGGER.error("No custodian for hash values could be found. Deduplicator is not able to work in this situation.");
                return;
            }

            sink = new SparqlBasedSink(SparqlUtils.getDatasetUriForUpdate(), SparqlUtils.getDatasetUriForQuery());

            serializer = new GzipJavaUriSerializer();

            try {
                RabbitQueue rabbitQueue = this.incomingDataQueueFactory.createDefaultRabbitQueue(DEDUPLICATOR_QUEUE_NAME);
                receiver = DataReceiverImpl.builder().dataHandler(this)
                    .maxParallelProcessedMsgs(100).queue(rabbitQueue).build();

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
     * Compare the hash values of the uris in {@link #newUrisBufferSet} with the hash values of all uris contained
     * in {@link #knownUriFilter}.
     */
    private void compareNewUrisWithOldUris() {
        Set<HashValue> hashValuesOfNewUris = new HashSet<>();
        for (CrawleableUri uri : newUrisBufferSet) {
            hashValuesOfNewUris.add(uri.getHashValue());
        }
        Set<CrawleableUri> oldUrisForComparison = uriHashCustodian.getUrisWithSameHashValues(hashValuesOfNewUris);

        outer:
        for (CrawleableUri uriNew : newUrisBufferSet) {
            for (CrawleableUri uriOld : oldUrisForComparison) {
                if (!uriOld.equals(uriNew)) {
                    // get triples from pair1 and pair2 and compare them
                    Set<Triple> setOld = new HashSet<>(sink.getTriplesForGraph(uriOld));
                    Set<Triple> setNew = new HashSet<>(sink.getTriplesForGraph(uriNew));

                    if (tripleComparator.triplesAreEqual(setOld, setNew)) {
                        // TODO: delete duplicate
                        continue outer;
                    }

                }
            }
        }
    }

    @Override
    public void close() {
        receiver.closeWhenFinished();
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
        LOGGER.info("Deduplicator got a message (\"{}\").", object.toString());
        if (object != null) {
            if (object instanceof UriSet) {
                UriSet uriSet = (UriSet) object;
                for (CrawleableUri uri : uriSet.uris) {
                    LOGGER.info("dedup hat uri " + uri);
                    List<Triple> triples = new ArrayList<>();
                    TripleHashPostProcessor tripleHashPostProcessor = new TripleHashPostProcessor(this, triples, uri, new SimpleTripleHashFunction());
                    tripleHashPostProcessor.run();
                }
            } else {
                LOGGER.info("Received an unknown object. It will be ignored.");
            }
        }
    }

    @Override
    public void handleData(byte[] bytes) {
        handleData(bytes, null, null, null);
    }

    /**
     * Send an uri for which a {@link org.aksw.simba.squirrel.deduplication.hashing.HashValue} has been computed.
     * Also add uri to {@link #newUrisBufferSet} and call {@link #recognizeUriWithComputedHashValue(CrawleableUri)} if necessary.
     *
     * @param uri The new uri with the computed hash value.
     */
    public void recognizeUriWithComputedHashValue(CrawleableUri uri) {
        try {
            newUrisBufferSet.add(uri);
            if (newUrisBufferSet.size() > MAX_SIZE_NEW_URIS_BUFFER_LIST) {
                compareNewUrisWithOldUris();
                UriHashValueResult result = new UriHashValueResult(newUrisBufferSet);
                senderFrontier.sendData(serializer.serialize(result));
                newUrisBufferSet.clear();
            }
        } catch (IOException e) {
            LOGGER.error("Error while serializing uri " + uri, e);
        }
    }
}
