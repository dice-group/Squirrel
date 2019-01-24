package org.dice_research.squirrel.data.uri.filter;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.UriType;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.frontier.impl.FrontierImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;

public class MongoDBKnowUriFilter implements KnownUriFilter, Cloneable, Closeable,UriHashCustodian {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBKnowUriFilter.class);

    private MongoClient client;
    private MongoDatabase mongoDB;
    public static final String DB_NAME = "squirrel";
    private Integer recrawlEveryWeek = 60 * 60 * 24 * 7 * 1000; // in miiliseconds
    public static final String COLLECTION_NAME = "knownurifilter";
    
    public static final String COLUMN_TIMESTAMP_LAST_CRAWL = "timestampLastCrawl";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_CRAWLING_IN_PROCESS = "crawlingInProcess";
    public static final String COLUMN_TIMESTAMP_NEXT_CRAWL = "timestampNextCrawl";
    public static final String COLUMN_IP = "ipAddress";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_HASH_VALUE = "hashValue";
    /**
     * Used as a default hash value for URIS, will be replaced by real hash value as soon as it has been computed.
     */
    private static final String DUMMY_HASH_VALUE = "dummyValue";

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
            Long timestampRetrieved = Long.parseLong(doc.get(COLUMN_TIMESTAMP_LAST_CRAWL).toString());
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
    public void add(CrawleableUri uri, long nextCrawlTimestamp) {
    	add(uri, System.currentTimeMillis(), nextCrawlTimestamp);
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
    	 mongoDB.getCollection(COLLECTION_NAME)
         .insertOne(crawleableUriToMongoDocument(uri)
        		 .append(COLUMN_TIMESTAMP_LAST_CRAWL, lastCrawlTimestamp)
        		 .append(COLUMN_TIMESTAMP_NEXT_CRAWL, nextCrawlTimestamp)
        		 .append(COLUMN_CRAWLING_IN_PROCESS, false)
        		 .append(COLUMN_HASH_VALUE, DUMMY_HASH_VALUE)
        		 );
    	 LOGGER.debug("Adding URI {} to the known uri filter list", uri.toString());
    }
    
    @Override
    public void addHashValuesForUris(List<CrawleableUri> uris) {
        for (CrawleableUri uri : uris) {
//            r.db(DATABASE_NAME).table(TABLE_NAME).filter(doc -> doc.getField(COLUMN_URI).eq(uri.getUri().toString())).
//                update(r.hashMap(COLUMN_HASH_VALUE, ((HashValue) uri.getData(Constants.URI_HASH_KEY)).encodeToString())).run(connector.connection);
        }
    }
    
    
    public void purge() {
    	mongoDB.getCollection(COLLECTION_NAME).drop();
    }

    @Override
    public List<CrawleableUri> getOutdatedUris() {
    	// get all uris with the following property:
        // (nextCrawlTimestamp has passed) AND (crawlingInProcess==false OR lastCrawlTimestamp is 3 times older than generalRecrawlTime)
    	
    	long generalRecrawlTime = Math.max(FrontierImpl.DEFAULT_GENERAL_RECRAWL_TIME, FrontierImpl.getGeneralRecrawlTime());

    	Bson filter = Filters.and(Filters.eq("COLUMN_TIMESTAMP_NEXT_CRAWL", System.currentTimeMillis()),
    			Filters.or(
	    			Filters.eq("COLUMN_CRAWLING_IN_PROCESS", false),
	    			Filters.eq("COLUMN_TIMESTAMP_LAST_CRAWL", System.currentTimeMillis() - generalRecrawlTime * 3)
    			));

        Iterator<Document> uriDocs = mongoDB.getCollection(COLLECTION_NAME).find(filter).iterator();

        List<CrawleableUri> urisToRecrawl = new ArrayList<>();
        while (uriDocs.hasNext()) {
            try {
                Document doc = uriDocs.next();
                String ipString = (String) doc.get(COLUMN_IP);
                if (ipString.contains("/")) {
                    ipString = ipString.split("/")[1];
                }
                urisToRecrawl.add(new CrawleableUri(new URI((String) doc.get(COLUMN_URI)), InetAddress.getByName(ipString)));
            } catch (URISyntaxException | UnknownHostException e) {
                LOGGER.warn(e.toString());
            }
        }

        // mark that the uris are in process now
        for (CrawleableUri uri : urisToRecrawl) {
        	
        	BasicDBObject newDocument = new BasicDBObject();
        	newDocument.append("$set", new BasicDBObject().append(COLUMN_CRAWLING_IN_PROCESS, true));
        	
        	BasicDBObject searchQuery = new BasicDBObject().append(COLUMN_URI,  uri.getUri().toString());
        	
        	 mongoDB.getCollection(COLLECTION_NAME).updateMany(searchQuery, newDocument);
        	
        }

//        cursor.close();
        return urisToRecrawl;
    }

    @Override
    public long count() {
        // TODO Auto-generated method stub
        return 0;
    }

	@Override
	public Set<CrawleableUri> getUrisWithSameHashValues(Set<HashValue> hashValuesForComparison) {
		// TODO Auto-generated method stub
		return null;
	}

}
