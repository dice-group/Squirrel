package org.dice_research.squirrel.queue;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDBCleaner {
	
	private static final String hostName = "spark-hare-1.cs.uni-paderborn.de";
	private static final int port = 27017;
	private static final String COLLECTION_NAME = "queue";
	private static final String COLLECTION_URIS = "uris";
	
	public static void main(String[] args) {
		
		MongoClient client = new MongoClient(hostName, port);
		
	    MongoDatabase mongoDB = client.getDatabase("squirrel");
	    
		//mcloude.de
//		mongoDB.getCollection(COLLECTION_NAME).deleteOne(new Document("ipAddress","141.17.30.16").append("type", pair.type.toString()));
//		mongoDB.getCollection(COLLECTION_URIS).deleteMany(new Document("ipAddress","141.17.30.16").append("type", pair.type.toString()));
		
	}

}
