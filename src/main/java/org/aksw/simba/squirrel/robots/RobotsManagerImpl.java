package org.aksw.simba.squirrel.robots;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;

public class RobotsManagerImpl implements RobotsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RobotsManagerImpl.class);

    private static final String ROBOTS_FILE_NAME = "/robots.txt";

    private BaseHttpFetcher fetcher;
    private BaseRobotsParser parser;

    public RobotsManagerImpl(BaseHttpFetcher fetcher) {
        this(fetcher, new SimpleRobotRulesParser());
    }

    public RobotsManagerImpl(BaseHttpFetcher fetcher, BaseRobotsParser parser) {
        this.fetcher = fetcher;
        this.parser = parser;
    }

    protected BaseRobotRules getRules(URI uri) {
        try {
            return RobotUtils.getRobotRules(fetcher, parser,
                    new URL(uri.getScheme(), uri.getHost(), uri.getPort(), ROBOTS_FILE_NAME));
        } catch (MalformedURLException e) {
            LOGGER.error("URL of robots.txt file is malformed. Returning rules for HTTP 400.");
            return parser.failedFetch(400);
        }
    }

    @Override
    public boolean isUriCrawlable(URI uri) {
        BaseRobotRules rules = getRules(uri);
        return rules.isAllowed(uri.toString());
    }

    @Override
    public long getMinWaitingTime(URI uri) {
        BaseRobotRules rules = getRules(uri);
        long delay = rules.getCrawlDelay();
        if (delay <= 0) {
            return 0;
        } else {
            return delay;
        }
    }

}
