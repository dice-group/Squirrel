package org.dice_research.squirrel.robots;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;

import org.dice_research.squirrel.data.uri.CrawleableUri;
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
    private static final long DEFAULT_MIN_WAITING_TIME = 2000;

    private long defaultMinWaitingTime = DEFAULT_MIN_WAITING_TIME;
    private BaseHttpFetcher fetcher;
    private BaseRobotsParser parser;
    private InetAddress lastIpAddress;
    private BaseRobotRules robotRules;

    public RobotsManagerImpl(BaseHttpFetcher fetcher) {
        this(fetcher, new SimpleRobotRulesParser());
    }

    public RobotsManagerImpl(BaseHttpFetcher fetcher, BaseRobotsParser parser) {
        this.fetcher = fetcher;
        this.parser = parser;
    }

    protected BaseRobotRules getRules(CrawleableUri curi) {
        try {

            if (lastIpAddress == null || !lastIpAddress.getHostAddress().equals(curi.getIpAddress().getHostAddress())) {
                this.lastIpAddress = curi.getIpAddress();
                robotRules = RobotUtils.getRobotRules(fetcher, parser, new URL(curi.getUri().getScheme(),
                        curi.getUri().getHost(), curi.getUri().getPort(), ROBOTS_FILE_NAME));
            }
            return robotRules;

        } catch (MalformedURLException e) {
            LOGGER.error("URL of robots.txt file is malformed. Returning rules for HTTP 400.");
            return parser.failedFetch(400);
        }
    }

    @Override
    public boolean isUriCrawlable(CrawleableUri curi) {
        BaseRobotRules rules = getRules(curi);
        return rules.isAllowed(curi.getUri().toString());
    }

    @Override
    public long getMinWaitingTime(CrawleableUri curi) {
        BaseRobotRules rules = getRules(curi);
        return Math.max(rules.getCrawlDelay(), defaultMinWaitingTime);
    }

    public void setDefaultMinWaitingTime(long defaultMinWaitingTime) {
        this.defaultMinWaitingTime = defaultMinWaitingTime;
    }

}
