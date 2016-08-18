package org.aksw.simba.squirrel.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ivan on 8/18/16.
 */
public class RedisModelTest {

    RedisModel model = null;

    @Before
    public void init() {
        String redisUri = "redis://localhost:6379/0";
        model = new RedisModel(redisUri);
        model.open();
    }

    @After
    public void teardown() {
        model.close();
    }

    @Test
    public void testSetGetUri() {
        String uri = "http://example.com/";
        long timestamp = System.currentTimeMillis();
        String response = model.set(uri, Long.toString(timestamp));
        assert(response == "OK");
        String temp = model.get(uri);
        long retrievedTimestamp = Long.valueOf(temp);
        assert(retrievedTimestamp == timestamp);
    }

    @Test
    public void testSetGetUriTimeout() throws InterruptedException {
        String uri = "http://example.com/";
        long timestamp = System.currentTimeMillis();

        // 1 second timeout here
        String response = model.setex(uri, 1, Long.toString(timestamp));
        Thread.sleep(2000);
        String retrievedTimestamp = model.get(uri);
        assert(retrievedTimestamp == null);
    }
}
