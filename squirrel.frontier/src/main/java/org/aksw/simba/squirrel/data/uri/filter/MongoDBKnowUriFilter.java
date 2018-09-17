package org.aksw.simba.squirrel.data.uri.filter;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

public class MongoDBKnowUriFilter implements KnownUriFilter, Cloneable, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBKnowUriFilter.class);

    private MongoClient client;
    private MongoDatabase mongoDB;
    private final String DB_NAME = "squirrel";
    private Integer recrawlEveryWeek = 60 * 60 * 24 * 7 * 1000; // in miiliseconds
    private final String COLLECTION_NAME = "knownurifilter";

    public MongoDBKnowUriFilter(String hostName, Integer port) {
        client = new MongoClient(hostName, port);
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        MongoCursor<Document> cursor = mongoDB.getCollection(COLLECTION_NAME)
                .find(new Document("uri", uri.getUri().toString())).iterator();

        if (cursor.hasNext()) {
            LOGGER.debug("URI {} is not good", uri.toString());
            Document doc = cursor.next();
            Long timestampRetrieved = Long.parseLong(doc.get("timestamp").toString());
            cursor.close();
            if ((System.currentTimeMillis() - timestampRetrieved) < recrawlEveryWeek) {
                return false;
            } else {
                return true;
            }
        } else {
            LOGGER.debug("URI {} is good", uri.toString());
            cursor.close();
            return true;
        }

    }

    @Override
    public void add(CrawleableUri uri, long timestamp) {
        mongoDB.getCollection(COLLECTION_NAME)
                .insertOne(crawleableUriToMongoDocument(uri).append("timestamp", timestamp));
        LOGGER.debug("Adding URI {} to the known uri filter list", uri.toString());
    }

    public Document crawleableUriToMongoDocument(CrawleableUri uri) {

        InetAddress ipAddress = uri.getIpAddress();
        @SuppressWarnings("deprecation")
        UriType uriType = uri.getType();

        return new Document("ipAddress", ipAddress.toString()).append("type", uriType.toString()).append("uri",
                uri.getUri().toString());

    }

    @Override
    public void close() throws IOException {
        mongoDB.getCollection(COLLECTION_NAME).drop();
        client.close();
    }

    public void open() {
        mongoDB = client.getDatabase(DB_NAME);
        if (!knowUriTableExists()) {
            mongoDB.createCollection(COLLECTION_NAME);
            MongoCollection<Document> mongoCollection = mongoDB.getCollection(COLLECTION_NAME);
            mongoCollection.createIndex(Indexes.compoundIndex(Indexes.ascending("uri")));
        }
    }

    public boolean knowUriTableExists() {
        for (String collection : mongoDB.listCollectionNames()) {
            if (collection.toLowerCase().equals(COLLECTION_NAME.toLowerCase())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void add(CrawleableUri uri, long lastCrawlTimestamp, long nextCrawlTimestamp) {
        // TODO Add recrawling support
        add(uri, System.currentTimeMillis());
    }

    @Override
    public List<CrawleableUri> getOutdatedUris() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long count() {
        // TODO Auto-generated method stub
        return 0;
    }

}
