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
 * A white list implementation that is based on the
 * {@link AbstractRegexBasedFilter} class. Its
 * {@link #isUriGood(org.dice_research.squirrel.data.uri.CrawleableUri)} method
 * returns {@code true} if at least one of the given patterns matches the given
 * URI. Else, {@code false} is returned.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class RegexBasedWhiteListFilter extends AbstractRegexBasedFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexBasedWhiteListFilter.class);

    public RegexBasedWhiteListFilter(Set<Pattern> patterns) {
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
