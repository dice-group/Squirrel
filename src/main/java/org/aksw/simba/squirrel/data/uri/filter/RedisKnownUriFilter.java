package org.aksw.simba.squirrel.data.uri.filter;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.model.RedisModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ivan on 8/18/16.
 */
public class RedisKnownUriFilter implements KnownUriFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisKnownUriFilter.class);

    private RedisModel model = null;
    private Integer recrawlEveryWeek = 60 * 60 * 24 * 7;

    public RedisKnownUriFilter(String redisUri) {
        this.model = new RedisModel(redisUri);
    }

    @Override
    public void add(CrawleableUri uri) {
        add(uri, System.currentTimeMillis());
    }

    @Override
    public void add(CrawleableUri uri, long timestamp) {
        String response = this.model.setex(uri.toString(), this.recrawlEveryWeek, Long.toString(timestamp));
        LOGGER.debug(uri.toString());
        LOGGER.debug(this.model.get(uri.toString()));
        assert(response == "OK");
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        String retrievedTimestamp = model.get(uri.toString());
        if(retrievedTimestamp == null || retrievedTimestamp.isEmpty()) {
            //URI is not in redis;
            return true;
        }
        //URI is in redis;
        return false;
    }

    public void open() {
        this.model.open();
    }

    public void close() {
        this.model.close();
    }
}
