package org.dice_research.squirrel.mongodb;

import org.dice_research.squirrel.configurator.MongoConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * A class that returns a connection with mongodb
 * 
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */

public class MongodbConnectionFactory {
	
	/**
	 * Returns a MongoClient based on host and port
	 * 
	 * @param hostName the mongodb host
	 * @param port the mongodb post
	 * @return a new MongoClient
	 */
	public static MongoClient getConnection(String hostName, Integer port) {
		MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        MongoConfiguration mongoConfiguration = MongoConfiguration.getMDBConfiguration();

        if (mongoConfiguration != null && (mongoConfiguration.getConnectionTimeout() != null && mongoConfiguration.getSocketTimeout() != null
                && mongoConfiguration.getServerTimeout() != null)) {
            optionsBuilder.connectTimeout(mongoConfiguration.getConnectionTimeout());
            optionsBuilder.socketTimeout(mongoConfiguration.getSocketTimeout());
            optionsBuilder.serverSelectionTimeout(mongoConfiguration.getServerTimeout());

            MongoClientOptions options = optionsBuilder.build();

            return new MongoClient(new ServerAddress(hostName, port), options);

        } else {
        	return new MongoClient(hostName, port);
        }
	}

}
