package org.dice_research.squirrel.components;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.filter.KnownUriFilter;
import org.dice_research.squirrel.data.uri.filter.SPARQLKnownUriFilter;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.TripleComparator;
import org.dice_research.squirrel.deduplication.hashing.TripleHashFunction;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.deduplication.hashing.impl.IntervalBasedMinHashFunction;
import org.dice_research.squirrel.deduplication.hashing.impl.SimpleTripleComparator;
import org.dice_research.squirrel.deduplication.hashing.impl.SimpleTripleHashFunction;
import org.dice_research.squirrel.rabbit.RespondingDataHandler;
import org.dice_research.squirrel.rabbit.ResponseHandler;
import org.dice_research.squirrel.rabbit.msgs.UriSet;
import org.dice_research.squirrel.sink.SparqlBasedSinkDedup;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.DataReceiverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for deduplication.
 * If some new uris have been crawled by the {@link org.dice_research.squirrel.worker.Worker} the deduplicator computes
 * {@link org.dice_research.squirrel.deduplication.hashing.HashValue}s for the triples found in the new uris, stores those
 * hash values in the {@link KnownUriFilter} and compares the newly computed hash values with the hash values of all 'old'
 * triples. By doing that, duplicate data can be found and eliminated.
 * Note: The hash value behind a uri represents the triples behind the uris, it does not represent the uri itself.
 * If The hash values of two uris are equal, the deduplicator looks behind the triples of those two uris and compares them. If the
 * lists of triples are equal, one of the two lists of triples will be deleted as it is a duplicate.
 */

@Component
@Qualifier("deduplicatorComponent")
public class DeduplicatorComponent extends AbstractComponent implements RespondingDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeduplicatorComponent.class);

    /**
     * Indicates whether deduplication is active. If it is not active, this component will not do anything. Also,
     * no processing of hash values will be done.
     */
    private boolean deduplicationActive;

    /**
     * A queue for uris which have to be processed (hash values have to computed for them).
     */
    private final List<CrawleableUri> uriQueue = new ArrayList<>();

    /**
     * Needed to access the {@link Triple}s.
     */
    private SparqlBasedSinkDedup sink;

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
        if (env.containsKey(Constants.DEDUPLICATION_ACTIVE_KEY)) {
            deduplicationActive = Boolean.parseBoolean(env.get(Constants.DEDUPLICATION_ACTIVE_KEY));
        } else {
            LOGGER.warn("Couldn't get {} from the environment. The default value will be used.", Constants.DEDUPLICATION_ACTIVE_KEY);
            deduplicationActive = Constants.DEFAULT_DEDUPLICATION_ACTIVE;
        }

        if(deduplicationActive){
            SPARQLKnownUriFilter knownUriFilter = new SPARQLKnownUriFilter(System.getenv("SPARQL_QUERY_URL"),
                System.getenv("SPARQL_UPDATE_URL"),
                System.getenv("SPARQL_HOST_USER"),
                System.getenv("SPARQL_HOST_PASSWD"));
            uriHashCustodian = knownUriFilter;
            sink = ((SPARQLKnownUriFilter) uriHashCustodian).connector;
            serializer = new GzipJavaUriSerializer();
            try {
                RabbitQueue rabbitQueue = this.incomingDataQueueFactory.createDefaultRabbitQueue(Constants.DEDUPLICATOR_QUEUE_NAME);
                receiver = DataReceiverImpl.builder().dataHandler(this)
                        .maxParallelProcessedMsgs(100).queue(rabbitQueue).build();
            } catch (IOException e) {
                LOGGER.error("Error while creating sender object.", e);
            }
            LOGGER.info("Deduplicator initialized.");
        }
        /*
        if (deduplicationActive) {
            String rdbHostName = null;
            int rdbPort = -1;
            if (env.containsKey(Constants.MDB_HOST_NAME_KEY)) {
                rdbHostName = env.get(Constants.MDB_HOST_NAME_KEY);
                if (env.containsKey(Constants.MDB_PORT_KEY)) {
                    rdbPort = Integer.parseInt(env.get(Constants.MDB_PORT_KEY));
                } else {
                    LOGGER.warn("Couldn't get {} from the environment. An in-memory queue will be used.", Constants.MDB_PORT_KEY);
                }
            } else {
                LOGGER.warn("Couldn't get {} from the environment. An in-memory queue will be used.", Constants.MDB_HOST_NAME_KEY);
            }
            String sparqlHostName = null;
            String sparqlHostPort = null;
//            FIXME Fix the following code
//
//            if (env.containsKey(WorkerConfiguration.SPARQL_HOST_PORTS_KEY)) {
//                sparqlHostName = env.get(WorkerConfiguration.SPARQL_HOST_CONTAINER_NAME_KEY);
//                if (env.containsKey(WorkerConfiguration.SPARQL_HOST_PORTS_KEY)) {
//                    sparqlHostPort = env.get(WorkerConfiguration.SPARQL_HOST_PORTS_KEY);
//                } else {
//                    LOGGER.warn("Couldn't get {} from the environment. An in-memory queue will be used.", WorkerConfiguration.SPARQL_HOST_PORTS_KEY);
//                }
//            } else {
//                LOGGER.warn("Couldn't get {} from the environment. An in-memory queue will be used.", WorkerConfiguration.SPARQL_HOST_PORTS_KEY);
//            }

            //endpoint setup
//            sink = SparqlBasedSinkDedup.create("http://sparqlhost:3030/squirrel/query","admin","pw123");
//            SPARQLKnownUriFilter knownUriFilter = new SPARQLKnownUriFilter();
//            uriHashCustodian = knownUriFilter;

//  String httpPrefix = "http://" + sparqlHostName + ":" + sparqlHostPort + "/contentset/";
//            sink = new SparqlBasedSink(sparqlHostName, sparqlHostPort, "contentset/update", "contentset/query", "MetaData/update", "MetaData/query");
//
//            if ((rdbHostName != null) && (rdbPort > 0)) {
//                SPARQLKnownUriFilter knownUriFilter = new SPARQLKnownUriFilter(rdbHostName, rdbPort);
//                uriHashCustodian = knownUriFilter;
//            }
            // at the moment, RDBKnownUriFilter is the only implementation of UriHashCustodian, that might change in the future

            serializer = new GzipJavaUriSerializer();

            try {
                RabbitQueue rabbitQueue = this.incomingDataQueueFactory.createDefaultRabbitQueue(Constants.DEDUPLICATOR_QUEUE_NAME);
                receiver = DataReceiverImpl.builder().dataHandler(this)
                    .maxParallelProcessedMsgs(100).queue(rabbitQueue).build();
            } catch (IOException e) {
                LOGGER.error("Error while creating sender object.", e);
            }
            LOGGER.info("Deduplicator initialized.");

//            //query
//            String queryString = "SELECT ?subject ?predicate ?object\n" +
//                    "WHERE {\n" +
//                    "GRAPH ?g {?subject ?predicate ?object}\n" +
//                    "}\n" +
//                    "LIMIT 100";
//
//            QueryExecution qe = SparqlBasedSinkDedup.queryExecFactory.createQueryExecution(queryString);
//            System.out.println(qe);
//            ResultSet rs = qe.execSelect();
//            System.out.println("-------------------------------------------------------------------------------------------------------");
//
//            while (rs.hasNext()) {
//                QuerySolution sol = rs.nextSolution();
//                RDFNode subject = sol.get("Concept");
//                System.out.println(subject);
//            }
        }*/
    }

    private void handleNewUris(List<CrawleableUri> uris) {
        LOGGER.info("Dedup_Testing: inside handleNewUris: "+ uris.size());
        List<CrawleableUri> generatedUris = new ArrayList<>();

        for (CrawleableUri nextUri : uris) {
            // query to fetch metadata for respective uris
            List<CrawleableUri> newGeneratedUriList = sink.getGeneratedUrisFromMetadata(nextUri);
            LOGGER.info("Dedup_Testing: newGeneratedUrisList: "+ newGeneratedUriList.size());
            for (CrawleableUri genUri : newGeneratedUriList){
                List<Triple> gentriples = sink.getTriplesForGraph(genUri);
                HashValue value = (new IntervalBasedMinHashFunction(2, tripleHashFunction).hash(gentriples));
                genUri.addData(Constants.URI_HASH_KEY, value);
                LOGGER.info("Dedup_Testing: Calculated hash value for genUri: " + genUri.getUri().toString() + ": " + value.encodeToString());
            }
            List<Triple> triples = sink.getTriplesForGraph(nextUri);
            HashValue value = (new IntervalBasedMinHashFunction(2, tripleHashFunction).hash(triples));
            nextUri.addData(Constants.URI_HASH_KEY, value);
            generatedUris.addAll(newGeneratedUriList);
            LOGGER.info("Dedup_Testing: Calculated hash value for nextUri: " + nextUri.getUri().toString() + ": " + value.encodeToString());
        }
        uris.addAll(generatedUris);
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
                        sink.deleteTriplesWithGraphId(uriNew);
                        sink.updateGraphIdForActivity(uriNew, uriOld);
                        continue outer;
                    }

                }
            }
        }
    }

    @Override
    public void close() {
        receiver.closeWhenFinished();
        if (uriHashCustodian instanceof Closeable) {
            try {
                ((Closeable) uriHashCustodian).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleData(byte[] data, ResponseHandler handler, String responseQueueName, String correlId) {
        LOGGER.info("Deduplicator invoked!");
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
