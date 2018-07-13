package org.aksw.simba.squirrel.queue;



import static com.mongodb.client.model.Updates.set;

import static com.mongodb.client.model.Filters.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.SnappyJavaUriSerializer;
import org.bson.Document;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.gridfs.GridFS;

@SuppressWarnings("deprecation")
public class MongoDBQueue extends AbstractIpAddressBasedQueue {
	
	private MongoClient client;
	private MongoDatabase mongoDB;
    private Serializer serializer;
    private final String DB_NAME ="squirrel";
    private final String COLLECTION_NAME = "queue";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBQueue.class);

	public MongoDBQueue(String hostName, Integer port) {
		client = new MongoClient(hostName,port);
		serializer = new SnappyJavaUriSerializer();
	}
	
	public MongoDBQueue(String hostName, Integer port, Serializer serializer) {
		client = new MongoClient(hostName,port);
		this.serializer = serializer;
	}
	
	public void purge() {
        mongoDB.getCollection(COLLECTION_NAME).drop();
    }
	
	 public long length() {
		 return mongoDB.getCollection(COLLECTION_NAME).count();
	    }
	
	public static void main(String[] args) throws URISyntaxException, UnknownHostException {
		String host = "localhost";
		Integer port = 27017;	
		
		MongoDBQueue queue = new MongoDBQueue(host, port);
		queue.open();
		
		URI uri = new URI("https://g1.globo.com/politica");
		CrawleableUri curi = new CrawleableUri(uri);
		curi.setIpAddress(InetAddress.getByName(uri.getHost()));
		
		queue.addToQueue(curi);
		
	}

	@Override
	public void close() {
		mongoDB.getCollection(COLLECTION_NAME).drop();
		client.close();
	}

	@Override
	public void open() {
		mongoDB = client.getDatabase(DB_NAME);
		if(!queueTableExists()) {
			mongoDB.createCollection(COLLECTION_NAME);
			MongoCollection<Document> mongoCollection =  mongoDB.getCollection(COLLECTION_NAME);
			mongoCollection.createIndex(Indexes.compoundIndex(Indexes.ascending("ipAddress"), Indexes.ascending("type")));
		}

	}
	
	 public boolean queueTableExists() {
		 for(String collection: mongoDB.listCollectionNames()) {
			 if(collection.toLowerCase().equals(COLLECTION_NAME.toLowerCase())) {
				return true; 
			 }
		 }
		 return false;
	    }

	@Override
	protected void addToQueue(CrawleableUri uri) {
		 List<?> ipAddressTypeKey = getIpAddressTypeKey(uri);
	        // if URI exists update the uris list
	        if(queueContainsIpAddressTypeKey(ipAddressTypeKey)) {
	            LOGGER.debug("TypeKey is in the queue already");
	            addCrawleableUri(uri, ipAddressTypeKey);
	        } else {
	            LOGGER.debug("TypeKey is not in the queue, creating a new one");
	            addCrawleableUri(uri);
	        }

	}

	@Override
	protected Iterator<IpUriTypePair> getIterator() {
		

		MongoCursor<Document> cursor = mongoDB.getCollection(COLLECTION_NAME).find().iterator();
		
		Iterator<IpUriTypePair> ipUriTypePairIterator = new Iterator<IpUriTypePair>() {
	          @Override
	          public boolean hasNext() {
	              return cursor.hasNext();
	          }

			@Override
			public IpUriTypePair next() {
				Document doc = (Document) cursor.next();
				try {
					InetAddress ipAddress = InetAddress.getByName(doc.get("ipAddress").toString());
					UriType uriType = UriType.valueOf(doc.get("type").toString());
					IpUriTypePair pair = new IpUriTypePair(ipAddress, uriType);
					return pair;
				}catch(UnknownHostException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		
		return ipUriTypePairIterator;
	}

	@Override
	protected List<CrawleableUri> getUris(IpUriTypePair pair) {
	        
	        Document doc = mongoDB.getCollection(COLLECTION_NAME).find(new Document("ipAddress", pair.ip.getHostAddress().toString())
    				.append("type", pair.type.toString())).first();
    		
    		@SuppressWarnings("unchecked")
			Set<Document> setUris = new LinkedHashSet<Document>((ArrayList <Document>) doc.get("uris"));
    		
    		List<CrawleableUri> listUris = new ArrayList<CrawleableUri>();
    		
    		try {
	    		for(Document document : setUris) {

	    			listUris.add( serializer.deserialize( ((Binary) document.get("uri")).getData()) );
	    		}
    		}catch (Exception e) {
    			LOGGER.error("Error while retrieving uri from MongoDBQueue",e);
			}
    		
    		mongoDB.getCollection(COLLECTION_NAME).deleteOne(new Document("ipAddress",pair.ip.getHostAddress()).append("type", pair.type.toString()));

	        return listUris;
	}
	
	 private List<CrawleableUri> createCrawleableUriList(ArrayList uris) {
	        List<CrawleableUri> resultUris = new ArrayList<CrawleableUri>();
	        
	        

	        for (Object uriString : uris) {
	        	try {
	            resultUris.add( serializer.deserialize((byte[]) uriString ));
	        	}catch (Exception e) {
	        		LOGGER.error("Couldn't deserialize uri", e);
				}
	        }

	        return resultUris;
	    }
	
	public boolean queueContainsIpAddressTypeKey(List<?> ipAddressTypeKey) {
		
		Iterator<Document>  iterator = mongoDB.getCollection(COLLECTION_NAME).find(new Document("ipAddress", ipAddressTypeKey.get(0)).
				append("type", ipAddressTypeKey.get(1))).iterator();
		
		if(iterator.hasNext()) {
			return true;
		}else {
			return false;
		}
		
    }
	
    @SuppressWarnings("unchecked")
	public void addCrawleableUri(CrawleableUri uri, List<?> ipAddressTypeKey) {

    	try {
    		
    		byte [] suri = serializer.serialize(uri);
    		
    		Document doc = mongoDB.getCollection(COLLECTION_NAME).find(new Document("ipAddress", ipAddressTypeKey.get(0))
    				.append("type", ipAddressTypeKey.get(1))).first();
    		
    		Set<Document> setUris = new LinkedHashSet<Document>((ArrayList <Document>) doc.get("uris"));
    		
    		
    		Document docUri = new Document();
    		docUri.put("_id", uri.getUri().hashCode());
    		docUri.put("uri", new Binary(suri));
    		
    		setUris.add(docUri);
    		
    		doc.put("uris", setUris);
    		
    		mongoDB.getCollection(COLLECTION_NAME).updateOne(new Document("ipAddress",ipAddressTypeKey.get(0).toString()).append("type",ipAddressTypeKey.get(1)),
    				set("uris", setUris));
    		
    		LOGGER.debug("Inserted existing UriTypePair");

    	} catch (Exception e) {
			LOGGER.error("Error while adding uri to MongoDBQueue",e);
		}
    }
	
    public void addCrawleableUri(CrawleableUri uri) {
    
    		mongoDB.getCollection(COLLECTION_NAME).insertOne(crawleableUriToMongoDocument(uri));
    	  
    	  LOGGER.debug("Inserted new UriTypePair");
    }
    
public Document crawleableUriToMongoDocument(CrawleableUri uri) {
    	
    	byte[] suri = null;
    	
		try {
			suri = serializer.serialize(uri);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        InetAddress ipAddress = uri.getIpAddress();
        UriType uriType = uri.getType();
        
        Document doc = new Document();
        doc.put("_id", uri.getUri().hashCode());
        doc.put("uri", new Binary(suri));
        
        Set<Document> set = new LinkedHashSet<Document>();
        set.add(doc);
        
        return new Document("uris", set)
        		.append("type", uriType.toString())
        		.append("ipAddress", ipAddress.getHostAddress());
        
    }
    



    public List<String> getIpAddressTypeKey(CrawleableUri uri) {
        return packTuple(uri.getIpAddress().getHostAddress(), uri.getType().toString());
    }

    public List<String> packTuple(String str_1, String str_2) {
    	List<String> pack = new ArrayList<String>();
    	pack.add(str_1);
    	pack.add(str_2);
        return pack;
    }
	
}
