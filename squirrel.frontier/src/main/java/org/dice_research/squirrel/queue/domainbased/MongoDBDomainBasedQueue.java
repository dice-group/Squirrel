package org.dice_research.squirrel.queue.domainbased;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.Binary;
import org.dice_research.squirrel.configurator.MongoConfiguration;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.UriType;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.SnappyJavaUriSerializer;
import org.dice_research.squirrel.queue.AbstractDomainBasedQueue;
import org.dice_research.squirrel.queue.DomainUriTypePair;
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

/**
 * 
 * DomainBasedQueue implementation for use with MongoDB
 * 
 * * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */
@SuppressWarnings("deprecation")
public class MongoDBDomainBasedQueue extends AbstractDomainBasedQueue {

    private MongoClient client;
    private MongoDatabase mongoDB;
    private Serializer serializer;
    private final String DB_NAME = "squirrel";
    private final String COLLECTION_QUEUE = "queue";
    private final String COLLECTION_URIS = "uris";
    private final String DEFAULT_DOMAIN = "default";
    private static final boolean PERSIST = System.getenv("QUEUE_FILTER_PERSIST") == null ? false : Boolean.parseBoolean(System.getenv("QUEUE_FILTER_PERSIST"));


    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBDomainBasedQueue.class);

    public MongoDBDomainBasedQueue(String hostName, Integer port, Serializer serializer) {
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

    public MongoDBDomainBasedQueue(String hostName, Integer port) {
        client = new MongoClient(hostName, port);
        serializer = new SnappyJavaUriSerializer();
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
        if(!PERSIST) {
            mongoDB.getCollection(COLLECTION_QUEUE).drop();
            mongoDB.getCollection(COLLECTION_URIS).drop();
        }
    
        client.close();
    }

    @Override
    public Iterator<SimpleEntry<String, List<CrawleableUri>>> getDomainIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addToQueue(CrawleableUri uri) {
        List<?> domainTypeKey = getDomainTypeKey(uri);
        // if URI exists update the uris list
        if (domainTypeKey != null) {

            if (queueContainsDomainTypeKey(uri, domainTypeKey)) {
                LOGGER.debug("TypeKey is in the queue already");
                addCrawleableUri(uri, domainTypeKey);
            } else {
                LOGGER.debug("TypeKey is not in the queue, creating a new one");
                addCrawleableUri(uri);
            }
        } else {
            LOGGER.warn("DomainTypeKey is null, nothing to add in the queue.");
        }

    }

    public void addCrawleableUri(CrawleableUri uri) {

        try {
            mongoDB.getCollection(COLLECTION_QUEUE).insertOne(crawleableUriToMongoDocument(uri)[0]);
            mongoDB.getCollection(COLLECTION_URIS).insertOne(crawleableUriToMongoDocument(uri)[1]);
            LOGGER.warn("Added " + uri.getUri().toString() + " to the queue");
        } catch (Exception e) {
            if (e instanceof MongoWriteException)
                LOGGER.info("Uri: " + uri.getUri().toString() + " already in queue. Ignoring...");
            else
                LOGGER.error("Error while adding uri to MongoDBQueue", e);
        }

        LOGGER.debug("Inserted new UriTypePair");
    }

    public void addCrawleableUri(CrawleableUri uri, List<?> domainTypeKey) {

        try {

            byte[] suri = serializer.serialize(uri);

            Document doc = mongoDB.getCollection(COLLECTION_URIS).find(new Document("domain", domainTypeKey.get(0))
                    .append("type", domainTypeKey.get(1)).append("uri", new Binary(suri))).first();

            if (doc == null) {
                mongoDB.getCollection(COLLECTION_URIS).insertOne(crawleableUriToMongoDocument(uri)[1]);
            }

        } catch (Exception e) {
            if (e instanceof MongoWriteException)
                LOGGER.info("Uri: " + uri.getUri().toString() + " already in queue. Ignoring...");
            else
                LOGGER.error("Error while adding uri to MongoDBQueue", e);
        }
    }

    public Document[] crawleableUriToMongoDocument(CrawleableUri uri) {

        byte[] suri = null;
        String domain = null;

        try {
            suri = serializer.serialize(uri);
            domain = getDomainName(uri.getUri());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            LOGGER.error("Could not determine the domain for the URI: " + uri.getUri().toString() + ". Using default");
            domain = DEFAULT_DOMAIN;
        }
        UriType uriType = uri.getType();

        Document docUri = new Document();
        docUri.put("_id", uri.getUri().hashCode());
        docUri.put("domain", domain);
        docUri.put("type", uriType.toString());
        docUri.put("uri", new Binary(suri));

        Document docDomain = new Document();
        docDomain.put("domain", domain);
        docDomain.put("type", uriType.toString());

        Document[] docs = new Document[2];
        docs[0] = docDomain;
        docs[1] = docUri;

        return docs;

    }

    public boolean queueContainsDomainTypeKey(CrawleableUri curi, List<?> domainTypeKey) {

        Iterator<Document> iterator = mongoDB.getCollection(COLLECTION_QUEUE)
                .find(new Document("domain", domainTypeKey.get(0)).append("type", domainTypeKey.get(1))).iterator();

        if (iterator.hasNext()) {
            return true;
        } else {
            return false;
        }

    }

    public List<String> getDomainTypeKey(CrawleableUri uri) {
        String domain = null;
        try {
            domain = getDomainName(uri.getUri());
        } catch (URISyntaxException e) {
            LOGGER.error("Could not obtain domain from URI: " + uri.getUri().toString() + ". Using Default");
            domain = DEFAULT_DOMAIN;
        }
        return packTuple(domain, uri.getType().toString());
    }

    public List<String> packTuple(String str_1, String str_2) {
        List<String> pack = new ArrayList<String>();
        pack.add(str_1);
        pack.add(str_2);
        return pack;
    }

    public static String getDomainName(URI uri) throws URISyntaxException {
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    @Override
    public boolean isEmpty() {
        if (length() == 0L)
            return true;
        else
            return false;
    }

    @Override
    public void open() {
        mongoDB = client.getDatabase(DB_NAME);
        if (!queueTableExists()) {
            mongoDB.createCollection(COLLECTION_QUEUE);
            mongoDB.createCollection(COLLECTION_URIS);
            MongoCollection<Document> mongoCollection = mongoDB.getCollection(COLLECTION_QUEUE);
            MongoCollection<Document> mongoCollectionUris = mongoDB.getCollection(COLLECTION_URIS);
            mongoCollection
                    .createIndex(Indexes.compoundIndex(Indexes.ascending("domain"), Indexes.ascending("type")));
            mongoCollectionUris.createIndex(Indexes.compoundIndex(Indexes.ascending("uri"),
                    Indexes.ascending("domain"), Indexes.ascending("type")));
        }
    }
    
    public boolean queueTableExists() {
        for (String collection : mongoDB.listCollectionNames()) {
            if (collection.toLowerCase().equals(COLLECTION_QUEUE.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<DomainUriTypePair> getIterator() {

        MongoCursor<Document> cursor = mongoDB.getCollection(COLLECTION_QUEUE).find().iterator();

        Iterator<DomainUriTypePair> domainTypePairIterator = new Iterator<DomainUriTypePair>() {
            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            @Override
            public DomainUriTypePair next() {
                Document doc = (Document) cursor.next();
                    String domain = doc.get("domain").toString();
                    UriType uriType = UriType.valueOf(doc.get("type").toString());
                    DomainUriTypePair pair = new DomainUriTypePair(domain, uriType);
                    return pair;
                }
        };

        return domainTypePairIterator;
    }

    @Override
    public List<CrawleableUri> getUris(DomainUriTypePair pair) {

        Iterator<Document> uriDocs = mongoDB.getCollection(COLLECTION_URIS).find(
                new Document("domain", pair.getDomain()).append("type", pair.getType().toString()))
                .iterator();

        List<CrawleableUri> listUris = new ArrayList<CrawleableUri>();

        try {
            while (uriDocs.hasNext()) {

                Document doc = uriDocs.next();

                listUris.add(serializer.deserialize(((Binary) doc.get("uri")).getData()));

            }

        } catch (Exception e) {
            LOGGER.error("Error while retrieving uri from MongoDBQueue", e);
        }

        mongoDB.getCollection(COLLECTION_QUEUE)
                .deleteOne(new Document("ipAddress", pair.getDomain()).append("type", pair.getType().toString()));
        mongoDB.getCollection(COLLECTION_URIS)
                .deleteMany(new Document("ipAddress", pair.getDomain()).append("type", pair.getType().toString()));

        return listUris;
    }

}
