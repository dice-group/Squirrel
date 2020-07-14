package org.dice_research.squirrel.data.uri.filter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.FileUtils;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexBasedWhiteListFilter extends AbstractKnownUriFilterDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexBasedWhiteListFilter.class);
    private Set<Pattern> whiteList;


    public static RegexBasedWhiteListFilter create(KnownUriFilter decorated, File whitelistfile) {
        try {
            List<String> whiteList = FileUtils.readLines(whitelistfile, StandardCharsets.UTF_8);
            Set<Pattern> whitePatterns = new HashSet<Pattern>();
            if (whiteList != null) {
                for (String s : whiteList) {
                    if ((s != null) && (!s.isEmpty())) {
                        try {
                            whitePatterns.add(Pattern.compile(s));
                        } catch (PatternSyntaxException e) {
                            LOGGER.error("Got an incorrect regex pattern. It will be ignored.", e);
                        }
                    }
                }
            }
            return new RegexBasedWhiteListFilter(decorated, whitePatterns);
        } catch (IOException e) {
            LOGGER.error("A problem was found when loading the WhiteList");
        }
        return null;
    }


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

    @Override
    public void add(CrawleableUri uri, long timestamp) {
        super.add(uri, timestamp);
    }

    @Override
    public void open() {
        // nothing to do
    }

}
