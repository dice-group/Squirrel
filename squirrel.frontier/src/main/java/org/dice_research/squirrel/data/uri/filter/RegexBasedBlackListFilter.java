package org.dice_research.squirrel.data.uri.filter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A black list implementation that is based on the
 * {@link AbstractRegexBasedFilter} class. Its
 * {@link #isUriGood(org.dice_research.squirrel.data.uri.CrawleableUri)} method
 * returns {@code false} if at least one of the given patterns matches the given
 * URI. Else, {@code true} is returned.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class RegexBasedBlackListFilter extends AbstractRegexBasedFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexBasedWhiteListFilter.class);

    public RegexBasedBlackListFilter(Set<Pattern> patterns) {
        super(patterns, true);
    }

    public static RegexBasedWhiteListFilter create(File whitelistfile) {
        try {
            List<String> whiteList = FileUtils.readLines(whitelistfile, StandardCharsets.UTF_8);
            return new RegexBasedWhiteListFilter(parsePatterns(whiteList));
        } catch (IOException e) {
            LOGGER.error("A problem was found when loading the WhiteList");
        }
        return null;
    }

}
