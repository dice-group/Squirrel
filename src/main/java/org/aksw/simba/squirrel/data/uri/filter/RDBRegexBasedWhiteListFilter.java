package org.aksw.simba.squirrel.data.uri.filter;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RDBRegexBasedWhiteListFilter extends RDBKnownUriFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDBRegexBasedWhiteListFilter.class);

    private Set<String> whiteList;

    public RDBRegexBasedWhiteListFilter(String hostname, Integer port) {
        super(hostname, port);
    }

    public RDBRegexBasedWhiteListFilter(String hostname, Integer port, File whiteListFile) {
        this(hostname, port, false, whiteListFile);
    }

    public RDBRegexBasedWhiteListFilter(String hostname, Integer port, boolean frontierDoesRecrawling, File whiteListFile) {
        super(hostname, port, frontierDoesRecrawling);
        try {
            whiteList = loadWhiteList(whiteListFile);
        } catch (IOException e) {
            LOGGER.error("A problem was found when loading the WhiteList");
        }
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        if (super.isUriGood(uri) && whiteList != null && !whiteList.isEmpty()) {

            for (String s : whiteList) {

                Pattern p = Pattern.compile(s.toLowerCase());
                Matcher m = p.matcher(uri.getUri().toString().toLowerCase());

                if (m.find()) {
                    LOGGER.trace("The URI {} fits to the pattern " + p.pattern() + " of the whitelist", uri.getUri().toString());
                    return true;
                }
            }
            LOGGER.warn("The URI {} is itself a good URI, but no of the " + whiteList.size() + " patterns of the whitelist matches! (in " + this + ")", uri.getUri().toString());
        }
        return false;
    }

    private Set<String> loadWhiteList(File whiteListFile) throws IOException {
        Set<String> list = new LinkedHashSet<>();

        FileReader fr = new FileReader(whiteListFile);
        BufferedReader br = new BufferedReader(fr);

        String line;

        while ((line = br.readLine()) != null) {
            list.add(line);
        }

        br.close();

        return list;
    }

    public void add(CrawleableUri uri) {
        super.add(uri, System.currentTimeMillis() + 60 * 60 * 1000);

    }

    @Override
    public void add(CrawleableUri uri, long timestamp) {
        super.add(uri, timestamp);

    }

    @Override
    public void add(CrawleableUri uri, long timestamp, long nextCrawlTimestamp) {
        super.add(uri, timestamp, nextCrawlTimestamp);

    }

    @Override
    public void close() {
        super.close();

    }

    @Override
    public void open() {
        super.open();

    }

}