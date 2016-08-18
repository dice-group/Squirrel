package org.aksw.simba.squirrel.model;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;

/**
 * Created by ivan on 8/18/16.
 */
public class RedisModel {
    private RedisURI connectionUri;
    private RedisClient client = null;
    private StatefulRedisConnection<String, String> connection = null;
    private RedisCommands<String, String> sync = null;

    public RedisModel(String connectionUri){
        System.out.println("Init redis model");
        this.connectionUri = RedisURI.create(connectionUri);
    }

    public void open() {
        this.client = new RedisClient(this.connectionUri);
        this.connection = this.client.connect();
        this.sync = this.connection.sync();
    }

    public void close() {
        this.connection.close();
        this.client.shutdown();
    }

    /**
     *
     * @param key
     * @param value
     * @return OK
     */
    public String set(String key, String value) {
        return this.sync.set(key, value);
    }

    /**
     *
     * @param key
     * @param timeout remove the key after timeout
     * @param value
     * @return OK
     */
    public String setex(String key, Integer timeout, String value) {
        return this.sync.setex(key, timeout, value);
    }

    public String get(String key) {
        return this.sync.get(key);
    }
}
