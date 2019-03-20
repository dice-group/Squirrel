package org.dice_research.squirrel.queue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.AbstractMap;
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
 * Queue implementation for use with MongoDB
 * 
 * * @author Geralod Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */

@SuppressWarnings("deprecation")
public class MongoDBQueue extends AbstractIpAddressBasedQueue {

	private MongoClient client;
	private MongoDatabase mongoDB;
	private Serializer serializer;
	private final String DB_NAME = "squirrel";
	private final String COLLECTION_QUEUE = "queue";
	private final String COLLECTION_URIS = "uris";

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBQueue.class);

	public MongoDBQueue(String hostName, Integer port,Serializer serializer) {
		this.serializer = serializer;

		
		MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        MongoConfiguration mongoConfiguration = MongoConfiguration.getMDBConfiguration();

		
		if(mongoConfiguration.getConnectionTimeout() != null && mongoConfiguration.getSocketTimeout() != null && mongoConfiguration.getServerTimeout() != null) {
			optionsBuilder.connectTimeout(mongoConfiguration.getConnectionTimeout());
			optionsBuilder.socketTimeout(mongoConfiguration.getSocketTimeout());
			optionsBuilder.serverSelectionTimeout(mongoConfiguration.getServerTimeout());
			
			MongoClientOptions options = optionsBuilder.build();

			client = new MongoClient(new ServerAddress(hostName, port),options);
			
		}else {
			client = new MongoClient(hostName, port);
		}

	}

	public MongoDBQueue(String hostName, Integer port) {
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
		mongoDB.getCollection(COLLECTION_QUEUE).drop();
		mongoDB.getCollection(COLLECTION_URIS).drop();
		client.close();
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
					.createIndex(Indexes.compoundIndex(Indexes.ascending("ipAddress"), Indexes.ascending("type")));
			mongoCollectionUris.createIndex(Indexes.compoundIndex(Indexes.ascending("uri"),
					Indexes.ascending("ipAddress"), Indexes.ascending("type")));
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
	protected void addToQueue(CrawleableUri uri) {
		List<?> ipAddressTypeKey = getIpAddressTypeKey(uri);
		// if URI exists update the uris list
		if (queueContainsIpAddressTypeKey(uri, ipAddressTypeKey)) {
			LOGGER.debug("TypeKey is in the queue already");
			addCrawleableUri(uri, ipAddressTypeKey);
		} else {
			LOGGER.debug("TypeKey is not in the queue, creating a new one");
			addCrawleableUri(uri);
		}

	}

	@Override
	protected Iterator<IpUriTypePair> getIterator() {

		MongoCursor<Document> cursor = mongoDB.getCollection(COLLECTION_QUEUE).find().iterator();

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
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		return ipUriTypePairIterator;
	}

	@Override
	protected List<CrawleableUri> getUris(IpUriTypePair pair) {

		Iterator<Document> uriDocs = mongoDB.getCollection(COLLECTION_URIS).find(
				new Document("ipAddress", pair.ip.getHostAddress().toString()).append("type", pair.type.toString()))
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
				.deleteOne(new Document("ipAddress", pair.ip.getHostAddress()).append("type", pair.type.toString()));
		mongoDB.getCollection(COLLECTION_URIS)
				.deleteMany(new Document("ipAddress", pair.ip.getHostAddress()).append("type", pair.type.toString()));

		return listUris;
	}

	public boolean queueContainsIpAddressTypeKey(CrawleableUri curi, List<?> ipAddressTypeKey) {

		Iterator<Document> iterator = mongoDB.getCollection(COLLECTION_QUEUE)
				.find(new Document("ipAddress", ipAddressTypeKey.get(0)).append("type", ipAddressTypeKey.get(1)))
				.iterator();

		if (iterator.hasNext()) {
			return true;
		} else {
			return false;
		}

	}

	public void addCrawleableUri(CrawleableUri uri, List<?> ipAddressTypeKey) {

		try {

			byte[] suri = serializer.serialize(uri);

			Document doc = mongoDB.getCollection(COLLECTION_URIS)
					.find(new Document("ipAddress", ipAddressTypeKey.get(0)).append("type", ipAddressTypeKey.get(1))
							.append("uri", new Binary(suri)))
					.first();

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

	public Document[] crawleableUriToMongoDocument(CrawleableUri uri) {

		byte[] suri = null;

		try {
			suri = serializer.serialize(uri);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InetAddress ipAddress = uri.getIpAddress();
		UriType uriType = uri.getType();

		Document docUri = new Document();
		docUri.put("_id", uri.getUri().hashCode());
		docUri.put("ipAddress", ipAddress.getHostAddress());
		docUri.put("type", uriType.toString());
		docUri.put("uri", new Binary(suri));

		Document docIp = new Document();
		docIp.put("ipAddress", ipAddress.getHostAddress());
		docIp.put("type", uriType.toString());

		Document[] docs = new Document[2];
		docs[0] = docIp;
		docs[1] = docUri;

		return docs;

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

	private List<CrawleableUri> createCrawleableUriList(List<Object> uris) {
		List<CrawleableUri> resultUris = new ArrayList<>();

		for (Object uriString : uris) {
			try {
				resultUris.add(serializer.deserialize((byte[]) uriString));
			} catch (Exception e) {
				LOGGER.error("Couldn't deserialize uri", e);
			}
		}

		return resultUris;
	}

	@Override
	public Iterator<SimpleEntry<InetAddress, List<CrawleableUri>>> getIPURIIterator() {
		// TODO Auto-generated method stub
		return new Iterator<AbstractMap.SimpleEntry<InetAddress, List<CrawleableUri>>>() {

			Iterator<IpUriTypePair> cursor = getIterator();

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return cursor.hasNext();
			}

			@Override
			public SimpleEntry<InetAddress, List<CrawleableUri>> next() {
				IpUriTypePair pair = cursor.next();

				Iterator<Document> uriDocs = mongoDB.getCollection(COLLECTION_URIS)
						.find(new Document("ipAddress", pair.ip.getHostAddress().toString()).append("type",
								pair.type.toString()))
						.iterator();
				List<CrawleableUri> value = new ArrayList<CrawleableUri>();
				while (uriDocs.hasNext()) {
					Document doc = uriDocs.next();
					try {
						value.add(serializer.deserialize(((Binary) doc.get("uri")).getData()));
					} catch (IOException e) {
						LOGGER.error("Was not able to read the field from the MDBQueue \"uris\"");
						value.clear();
					}
				}

				return new AbstractMap.SimpleEntry<>(pair.ip, value);

			}

		};
	}

	@Override
	public boolean isEmpty() {
		if (length() == 0L)
			return true;
		else
			return false;
	}

}
