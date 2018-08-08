package org.aksw.simba.squirrel.components;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.configurator.WorkerConfiguration;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.deduplication.hashing.HashValue;
import org.aksw.simba.squirrel.deduplication.hashing.TripleComparator;
import org.aksw.simba.squirrel.deduplication.hashing.TripleHashFunction;
import org.aksw.simba.squirrel.deduplication.hashing.UriHashCustodian;
import org.aksw.simba.squirrel.deduplication.hashing.impl.IntervalBasedMinHashFunction;
import org.aksw.simba.squirrel.deduplication.hashing.impl.SimpleTripleComparator;
import org.aksw.simba.squirrel.deduplication.hashing.impl.SimpleTripleHashFunction;
import org.aksw.simba.squirrel.rabbit.RespondingDataHandler;
import org.aksw.simba.squirrel.rabbit.ResponseHandler;
import org.aksw.simba.squirrel.rabbit.msgs.UriSet;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.sink.tripleBased.AdvancedTripleBasedSink;
import org.apache.jena.graph.Triple;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.DataReceiverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;

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
     * default value for {@link #deduplicationActive}
     */
    public static final boolean DEFAULT_DEDUPLICATION_ACTIVE = true;

    /**
     * Indicates whether deduplication is active. If it is not active, this component will not do anything. Also,
     * no processing of hash values will be done.
     */
    private static boolean deduplicationActive;

    /**
     * Key for the environments variable for {@link #deduplicationActive}
     */
    public static final String DEDUPLICATION_ACTIVE_KEY = "DEDUPLICATION_ACTIVE";

    /**
     * A queue for uris which have to be processed (hash values have to computed for them).
     */
    private final List<CrawleableUri> uriQueue = new ArrayList<>();

    /**
     * Needed to access the {@link Triple}s.
     */
    private AdvancedTripleBasedSink sink;

    private Serializer serializer;

    private DataReceiverImpl receiver;

    private TripleComparator tripleComparator = new SimpleTripleComparator();

    private UriHashCustodian uriHashCustodian;

    private TripleHashFunction tripleHashFunction = new SimpleTripleHashFunction();

    private final Semaphore terminationMutex = new Semaphore(0);

    @Override
    public void init() throws Exception {
        super.init();
        Map<String, String> env = System.getenv();
        if (env.containsKey(DEDUPLICATION_ACTIVE_KEY)) {
            deduplicationActive = Boolean.parseBoolean(env.get(DEDUPLICATION_ACTIVE_KEY));
        } else {
            LOGGER.warn("Couldn't get {} from the environment. The default value will be used.", DEDUPLICATION_ACTIVE_KEY);
            deduplicationActive = DEFAULT_DEDUPLICATION_ACTIVE;
        }
        if (deduplicationActive) {
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
            String sparqlHostName = null;
            String sparqlHostPort = null;
            if (env.containsKey(WorkerConfiguration.SPARQL_HOST_PORTS_KEY)) {
                sparqlHostName = env.get(WorkerConfiguration.SPARQL_HOST_CONTAINER_NAME_KEY);
                if (env.containsKey(WorkerConfiguration.SPARQL_HOST_PORTS_KEY)) {
                    sparqlHostPort = env.get(WorkerConfiguration.SPARQL_HOST_PORTS_KEY);
                } else {
                    LOGGER.warn("Couldn't get {} from the environment. An in-memory queue will be used.", WorkerConfiguration.SPARQL_HOST_PORTS_KEY);
                }
            } else {
                LOGGER.warn("Couldn't get {} from the environment. An in-memory queue will be used.", WorkerConfiguration.SPARQL_HOST_PORTS_KEY);
            }
            String httpPrefix = "http://" + sparqlHostName + ":" + sparqlHostPort + "/contentset/";
            sink = new SparqlBasedSink(sparqlHostName, sparqlHostPort, "contentset/update", "contentset/query", "MetaData/update", "MetaData/query");

            if ((rdbHostName != null) && (rdbPort > 0)) {
                RDBKnownUriFilter knownUriFilter = new RDBKnownUriFilter(rdbHostName, rdbPort, FrontierComponent.RECRAWLING_ACTIVE);
                uriHashCustodian = knownUriFilter;
            }

            // at the moment, RDBKnownUriFilter is the only implementation of UriHashCustodian, that might change in the future


            serializer = new GzipJavaUriSerializer();

            try {
                RabbitQueue rabbitQueue = this.incomingDataQueueFactory.createDefaultRabbitQueue(DEDUPLICATOR_QUEUE_NAME);
                receiver = DataReceiverImpl.builder().dataHandler(this)
                    .maxParallelProcessedMsgs(100).queue(rabbitQueue).build();
            } catch (IOException e) {
                LOGGER.error("Error while creating sender object.", e);
            }
            LOGGER.info("Deduplicator initialized.");
        }
    }

    private void handleNewUris(List<CrawleableUri> uris) {
        for (CrawleableUri nextUri : uris) {
            List<Triple> triples = sink.getTriplesForGraph(nextUri);
            HashValue value = (new IntervalBasedMinHashFunction(2, tripleHashFunction).hash(triples));
            nextUri.putData(Constants.URI_HASH_KEY, value);
        }

        compareNewUrisWithOldUris(uris);
        uriHashCustodian.addHashValuesForUris(uris);

    }

    @Override
    public void run() throws InterruptedException {
        terminationMutex.acquire();
    }

    /**
     * Compare the hash values of the uris in  with the hash values of all uris contained
     * in {@link #uriHashCustodian}.
     * @param uris
     */
    private void compareNewUrisWithOldUris(List<CrawleableUri> uris) {

        if (uriHashCustodian instanceof RDBKnownUriFilter) {
            ((RDBKnownUriFilter) uriHashCustodian).openConnector();
        }

        Set<HashValue> hashValuesOfNewUris = new HashSet<>();
        for (CrawleableUri uri : uris) {
            hashValuesOfNewUris.add((HashValue) uri.getData(Constants.URI_HASH_KEY));
        }
        Set<CrawleableUri> oldUrisForComparison = uriHashCustodian.getUrisWithSameHashValues(hashValuesOfNewUris);
        outer:
        for (CrawleableUri uriNew : uris) {
            for (CrawleableUri uriOld : oldUrisForComparison) {
                if (!uriOld.equals(uriNew)) {
                    // get triples from pair1 and pair2 and compare them
                    List<Triple> listOld = sink.getTriplesForGraph(uriOld);
                    List<Triple> listNew = sink.getTriplesForGraph(uriNew);

                    if (tripleComparator.triplesAreEqual(listOld, listNew)) {
                        // TODO: delete duplicate, this means Delete the triples from the new uris and
                        // replace them by a link to the old uris which has the same content
                        continue outer;
                    }

                }
            }
        }
    }

    @Override
    public void close() {
        receiver.closeWhenFinished();
        if (uriHashCustodian instanceof KnownUriFilter) {
            ((KnownUriFilter) uriHashCustodian).close();
        }
    }

    @Override
    public void handleData(byte[] data, ResponseHandler handler, String responseQueueName, String correlId) {
        if (!deduplicationActive) {
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
                handleNewUris(uriSet.uris);
            } else {
                LOGGER.info("Received an unknown object. It will be ignored.");
            }
        }
    }

    @Override
    public void handleData(byte[] bytes) {
        handleData(bytes, null, null, null);
    }
}
