package org.dice_research.squirrel.queue.scorebased;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mongodb.client.model.Sorts;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.bson.Document;
import org.bson.types.Binary;
import org.dice_research.squirrel.configurator.MongoConfiguration;
import org.dice_research.squirrel.data.uri.CrawleableUri;

import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.SnappyJavaUriSerializer;
import org.dice_research.squirrel.queue.AbstractURIScoreBasedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

/**
 *
 * URI Score based implementation for use with MongoDB
 *
 */
public class MongoDBURIScoreBasedQueue extends AbstractURIScoreBasedQueue {

    private MongoClient client;
    private MongoDatabase mongoDB;
    private Serializer serializer;
    private final String DB_NAME = "squirrel";
    private final String COLLECTION_QUEUE = "queue";
    private final String COLLECTION_URIS = "uris";
    @Deprecated
    private final String DEFAULT_TYPE = "default";
    private static final boolean PERSIST = System.getenv("QUEUE_FILTER_PERSIST") == null ? false
            : Boolean.parseBoolean(System.getenv("QUEUE_FILTER_PERSIST"));

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBURIScoreBasedQueue.class);
    private URIGraphSizeBasedQueue graphSizeBasedQueue = new URIGraphSizeBasedQueue("","","");
    public MongoDBURIScoreBasedQueue(String hostName, Integer port, Serializer serializer) {

        this.serializer = serializer;

        LOGGER.info("Queue Persistance: " + PERSIST);

        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        MongoConfiguration mongoConfiguration = MongoConfiguration.getMDBConfiguration();

        if (mongoConfiguration.getConnectionTimeout() != null && mongoConfiguration.getSocketTimeout() != null
                && mongoConfiguration.getServerTimeout() != null) {
            optionsBuilder.connectTimeout(mongoConfiguration.getConnectionTimeout());
            optionsBuilder.socketTimeout(mongoConfiguration.getSocketTimeout());
            optionsBuilder.serverSelectionTimeout(mongoConfiguration.getServerTimeout());

            MongoClientOptions options = optionsBuilder.build();

            client = new MongoClient(new ServerAddress(hostName, port), options);

        } else {
            client = new MongoClient(hostName, port);
        }

    }

    public MongoDBURIScoreBasedQueue(String hostName, Integer port) {
        client = new MongoClient(hostName, port);
        serializer = new SnappyJavaUriSerializer();
    }

    public MongoDBURIScoreBasedQueue(String hostName, Integer port, QueryExecutionFactory queryExecFactory) {
        this(hostName,port);
        this.graphSizeBasedQueue = new URIGraphSizeBasedQueue(queryExecFactory);
    }

    public void purge() {
        mongoDB.getCollection(COLLECTION_URIS).drop();
    }

    public long length() {
        return mongoDB.getCollection(COLLECTION_URIS).count();
    }

    @Override
    public void close() {
        if (!PERSIST) {
//            mongoDB.getCollection(COLLECTION_QUEUE).drop();
            mongoDB.getCollection(COLLECTION_URIS).drop();
        }
        client.close();
    }

    @Override
    public void addUri(CrawleableUri uri)  {
        addCrawleableUri(uri,getURIScore(uri));
    }

    private void addCrawleableUri(CrawleableUri uri, float score) {
        try {
            Document uriDoc = getUriDocument(uri, score);
            // If the document does not already exist, add it
            if (mongoDB.getCollection(COLLECTION_URIS).find(uriDoc).first() == null) {
                mongoDB.getCollection(COLLECTION_URIS).insertOne(uriDoc);
            }
        } catch (Exception e) {
            LOGGER.error("Error while adding uri to MongoDBQueue", e);
        }
    }

    private Document getUriDocument(CrawleableUri uri, float score) {
        byte[] suri = null;

        try {
            suri = serializer.serialize(uri);
        } catch (IOException e) {
            LOGGER.error("Couldn't serialize URI. Returning null.", e);
            return null;
        }

        Document docUri = new Document();
        docUri.put("_id", uri.getUri().hashCode());
        docUri.put("type", DEFAULT_TYPE);
        docUri.put("uri", new Binary(suri));
        docUri.put("score", score);
        return docUri;
    }

    @Override
    public boolean isEmpty() {
        return length() == 0L;
    }

    @Override
    public float getURIScore(CrawleableUri uri) {

        return graphSizeBasedQueue.getURIScore(uri);
    }

    @Override
    public void open() {
        mongoDB = client.getDatabase(DB_NAME);
        if (!queueTableExists()) {
            mongoDB.createCollection(COLLECTION_URIS);
            MongoCollection<Document> mongoCollectionUris = mongoDB.getCollection(COLLECTION_URIS);
            mongoCollectionUris.createIndex(Indexes.compoundIndex(Indexes.ascending("uri"), Indexes.ascending("domain"),
                    Indexes.ascending("type")));
        }
    }

    public boolean queueTableExists() {
        for (String collection : mongoDB.listCollectionNames()) {
            if (collection.equalsIgnoreCase(COLLECTION_QUEUE.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<CrawleableUri> getNextUris() {

        Iterator<Document> uriDocs = mongoDB.getCollection(COLLECTION_URIS)
                .find(new Document("type", DEFAULT_TYPE)).sort(Sorts.ascending("score")).iterator();

        List<CrawleableUri> listUris = new ArrayList<CrawleableUri>();

        try {
            while (uriDocs.hasNext()) {

                Document doc = uriDocs.next();

                listUris.add(serializer.deserialize(((Binary) doc.get("uri")).getData()));

            }

        } catch (Exception e) {
            LOGGER.error("Error while retrieving uri from MongoDBQueue", e);
        }

        return listUris;
    }

//    protected void deleteUris(List<CrawleableUri> uris) {
//        // remove all URIs from the list
//        Document query = new Document();
//        query.put("type", DEFAULT_TYPE);
//        for (CrawleableUri uri : uris) {
//            // replace the old ID with the current ID
//            query.put("_id", uri.getUri().hashCode());
//            mongoDB.getCollection(COLLECTION_URIS).deleteMany(query);
//        }
//        // remove the ID field
//        query.remove("_id");
//        // if there are no more URIs left of the given domain
//        if (mongoDB.getCollection(COLLECTION_URIS).find(query).first() == null) {
//            // remove the domain from the queue
//            mongoDB.getCollection(COLLECTION_QUEUE).deleteMany(query);
//        }
//    }
}
