package org.dice_research.squirrel.data.uri.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.filter.AbstractKnownUriFilterDecorator;
import org.dice_research.squirrel.data.uri.filter.KnownUriFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexBasedWhiteListFilter extends AbstractKnownUriFilterDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexBasedWhiteListFilter.class);

    public static RegexBasedWhiteListFilter create(KnownUriFilter decorated, File whitelistfile) {
        try {
            Set<String> whiteList = loadWhiteList(whitelistfile);
            Set<Pattern> whitePatterns = new HashSet<Pattern>();
            if (whiteList != null) {
                for (String s : whiteList) {
                    try {
                        whitePatterns.add(Pattern.compile(s));
                    } catch (PatternSyntaxException e) {
                        LOGGER.error("Got an incorrect regex pattern. It will be ignored.", e);
                    }
                }
            }
            return new RegexBasedWhiteListFilter(decorated, whitePatterns);
        } catch (IOException e) {
            LOGGER.error("A problem was found when loading the WhiteList");
        }
        return null;
    }

    private Set<Pattern> whiteList;

    public RegexBasedWhiteListFilter(KnownUriFilter decorated, Set<Pattern> whiteList) {
        super(decorated);
        this.whiteList = whiteList;
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        if (!super.isUriGood(uri)) {
            return false;
        }
        if (whiteList == null || whiteList.isEmpty()) {
            return true;
        } else {
            for (Pattern p : whiteList) {
                Matcher m = p.matcher(uri.getUri().toString().toLowerCase());
                if (m.find()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static Set<String> loadWhiteList(File whiteListFile) throws IOException {
        Set<String> list = new LinkedHashSet<String>();

        FileReader fr = new FileReader(whiteListFile);
        BufferedReader br = new BufferedReader(fr);

        String line;

        while ((line = br.readLine()) != null) {
            list.add(line);
        }

        br.close();

        return list;
    }

    @Override
    public void add(CrawleableUri uri, long timestamp) {
        super.add(uri, timestamp);

    }

    @Override
    public void open() {
        // nothing to do
    }

}
