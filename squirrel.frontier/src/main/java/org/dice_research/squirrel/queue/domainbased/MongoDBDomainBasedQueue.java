package org.dice_research.squirrel.queue.domainbased;

import java.io.IOException;
import java.util.*;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.bson.Document;
import org.bson.types.Binary;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.configurator.MongoConfiguration;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.SnappyJavaUriSerializer;
import org.dice_research.squirrel.queue.AbstractDomainBasedQueue;
import org.dice_research.squirrel.queue.scorecalculator.IURIScoreCalculator;
import org.dice_research.squirrel.queue.scorecalculator.URIGraphSizeBasedScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;

/**
 * DomainBasedQueue implementation for use with MongoDB
 * <p>
 * * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 */
public class MongoDBDomainBasedQueue extends AbstractDomainBasedQueue {

    private MongoClient client;
    private MongoDatabase mongoDB;
    private Serializer serializer;
    private final String DB_NAME = "squirrel";
    private final String COLLECTION_QUEUE = "queue";
    private final String COLLECTION_URIS = "uris";
    private IURIScoreCalculator graphSizeBasedQueue;
    @Deprecated
    private final String DEFAULT_TYPE = "default";
    private static final boolean PERSIST = System.getenv("QUEUE_FILTER_PERSIST") == null ? false
        : Boolean.parseBoolean(System.getenv("QUEUE_FILTER_PERSIST"));
    private static final float CRITICAL_SCORE = .2f;
    private static final int MIN_NUMBER_OF_URIS_TO_CHECK = 5;

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBDomainBasedQueue.class);

    public MongoDBDomainBasedQueue(String hostName, Integer port, Serializer serializer, boolean includeDepth) {
        this.serializer = serializer;

        this.includeDepth = includeDepth;
        if (this.includeDepth)
            LOGGER.info("Depth Persistance Enabled.");

        LOGGER.info("Queue Persistance: " + PERSIST);

        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        MongoConfiguration mongoConfiguration = MongoConfiguration.getMDBConfiguration();

        if (mongoConfiguration != null && (mongoConfiguration.getConnectionTimeout() != null && mongoConfiguration.getSocketTimeout() != null
            && mongoConfiguration.getServerTimeout() != null)) {
            optionsBuilder.connectTimeout(mongoConfiguration.getConnectionTimeout());
            optionsBuilder.socketTimeout(mongoConfiguration.getSocketTimeout());
            optionsBuilder.serverSelectionTimeout(mongoConfiguration.getServerTimeout());

            MongoClientOptions options = optionsBuilder.build();

            client = new MongoClient(new ServerAddress(hostName, port), options);

        } else {
            client = new MongoClient(hostName, port);
        }
    }

    public MongoDBDomainBasedQueue(String hostName, Integer port, Serializer serializer, boolean includeDepth, QueryExecutionFactory queryExecFactory) {
        this(hostName, port, serializer, includeDepth);
        this.graphSizeBasedQueue = new URIGraphSizeBasedScoreCalculator(queryExecFactory);
    }

    public MongoDBDomainBasedQueue(String hostName, Integer port, boolean includeDepth) {
        this(hostName, port, new SnappyJavaUriSerializer(), includeDepth);
    }

    public MongoDBDomainBasedQueue(String hostName, Integer port,boolean includeDepth,QueryExecutionFactory queryExecFactory) {
        this(hostName,port, new SnappyJavaUriSerializer(),includeDepth);
        this.graphSizeBasedQueue = new URIGraphSizeBasedScoreCalculator(queryExecFactory);
    }

    public void purge() {
        mongoDB.getCollection(COLLECTION_QUEUE).drop();
        mongoDB.getCollection(COLLECTION_URIS).drop();
    }

    public long length() {
        return mongoDB.getCollection(COLLECTION_QUEUE).count();
    }

    @Override
    public void close() {
        if (!PERSIST) {
            mongoDB.getCollection(COLLECTION_QUEUE).drop();
            mongoDB.getCollection(COLLECTION_URIS).drop();
        }
        client.close();
    }

    @Override
    protected void addUri(CrawleableUri uri, String domain) {
        addDomain(domain);
        addCrawleableUri(uri, domain);
    }

    protected float addCrawleableUri(CrawleableUri uri, String domain) {
        float score = 0;
        try {
            Document uriDoc = getUriDocument(uri, domain);
            // If the document does not already exist, add it
            if (mongoDB.getCollection(COLLECTION_URIS).find(uriDoc).first() == null) {
                mongoDB.getCollection(COLLECTION_URIS).insertOne(uriDoc);
            }
            score = (float) uriDoc.get(Constants.URI_SCORE);
        } catch (Exception e) {
            LOGGER.error("Error while adding uri to MongoDBQueue", e);
        }
        return score;
    }


    /**
     * Return the score of the duplicity score of the {@link CrawleableUri}
     *
     * @param uri the {@link CrawleableUri} whose duplicity score has to be returned
     * @return duplicity score of the {@link CrawleableUri}
     */
    public float getURIScore(CrawleableUri uri) {
        return graphSizeBasedQueue.getURIScore(uri);
    }

    protected void addDomain(String domain) {
        try {
            Document domainDoc = getDomainDocument(domain);
            if (!containsDomain(domainDoc)) {
                LOGGER.debug("Domain is not in the queue, creating a new one for {}", domain);
                mongoDB.getCollection(COLLECTION_QUEUE).insertOne(domainDoc);
            } else {
                LOGGER.debug("Domain is already in the queue: {}", domain);
            }
        } catch (MongoWriteException e) {
            LOGGER.error("Domain: " + domain + " couldn't be added to the queue. Ignoring...");
        }
    }

    public Document getUriDocument(CrawleableUri uri, String domain) {
        byte[] suri = null;

        try {
            suri = serializer.serialize(uri);
        } catch (IOException e) {
            LOGGER.error("Couldn't serialize URI. Returning null.", e);
            return null;
        }

        Document docUri = new Document();
        docUri.put("_id", uri.getUri().hashCode());
        docUri.put(Constants.URI_DOMAIN, domain);
        docUri.put("type", DEFAULT_TYPE);
        docUri.put("uri", new Binary(suri));
        if (graphSizeBasedQueue != null) {
            float score = getURIScore(uri);
            docUri.put(Constants.URI_SCORE, score);
        }
        return docUri;
    }

    public Document getDomainDocument(String domain) {
        Document docIp = new Document();
        docIp.put(Constants.URI_DOMAIN, domain);
        docIp.put("type", DEFAULT_TYPE);
        return docIp;
    }

    @Override
    public boolean isEmpty() {
        return length() == 0L;
    }

    @Override
    public void open() {
        mongoDB = client.getDatabase(DB_NAME);
        if (!queueTableExists()) {
            mongoDB.createCollection(COLLECTION_QUEUE);
            mongoDB.createCollection(COLLECTION_URIS);
            MongoCollection<Document> mongoCollection = mongoDB.getCollection(COLLECTION_QUEUE);
            MongoCollection<Document> mongoCollectionUris = mongoDB.getCollection(COLLECTION_URIS);
            mongoCollection.createIndex(Indexes.compoundIndex(Indexes.ascending(Constants.URI_DOMAIN), Indexes.ascending("type")));
            mongoCollectionUris.createIndex(Indexes.compoundIndex(Indexes.ascending("uri"), Indexes.ascending(Constants.URI_DOMAIN),
                Indexes.ascending("type")));
        }
    }

    @Override
    public float addKeywiseUri(CrawleableUri uri) {
        addDomain(uri.getUri().getHost());
        return addCrawleableUri(uri, uri.getUri().getHost());
    }

    @Override
    protected String getKey(CrawleableUri uri) {
        return uri.getUri().getHost();
    }

    @Override
    protected float getCriticalScore() {
        return CRITICAL_SCORE;
    }

    @Override
    protected int getMinNumOfUrisToCheck() {
        return MIN_NUMBER_OF_URIS_TO_CHECK;
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
    public Iterator<String> getGroupIterator() {

        MongoCursor<Document> cursor = mongoDB.getCollection(COLLECTION_QUEUE).find().iterator();

        Iterator<String> domainIterator = new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            @Override
            public String next() {
                return cursor.next().get(Constants.URI_DOMAIN).toString();
            }
        };

        return domainIterator;
    }

    @Override
    public List<CrawleableUri> getUris(String domain) {

        Iterator<Document> uriDocs = mongoDB.getCollection(COLLECTION_URIS)
            .find(new Document(Constants.URI_DOMAIN, domain).append("type", DEFAULT_TYPE))
            .sort(Sorts.descending(Constants.URI_SCORE)).iterator();

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

    @Override
    protected void deleteUris(String domain, List<CrawleableUri> uris) {
        // remove all URIs from the list
        Document query = new Document();
        query.put(Constants.URI_DOMAIN, domain);
        query.put("type", DEFAULT_TYPE);
        for (CrawleableUri uri : uris) {
            // replace the old ID with the current ID
            query.put("_id", uri.getUri().hashCode());
            mongoDB.getCollection(COLLECTION_URIS).deleteMany(query);
        }
        // remove the ID field
        query.remove("_id");
        // if there are no more URIs left of the given domain
        if (mongoDB.getCollection(COLLECTION_URIS).find(query).first() == null) {
            // remove the domain from the queue
            mongoDB.getCollection(COLLECTION_QUEUE).deleteMany(query);
        }
    }

    protected boolean containsDomain(String domain) {
        return containsDomain(getDomainDocument(domain));
    }

    protected boolean containsDomain(Document domainDoc) {
        return mongoDB.getCollection(COLLECTION_QUEUE).find(domainDoc).first() != null;
    }
}
