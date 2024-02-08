package org.dice_research.squirrel.data.uri.filter;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

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

    public RegexBasedWhiteListFilter(File whitelistfile, boolean lowerCaseComparison) {
        this(parsePatterns(whitelistfile), lowerCaseComparison);
    }

    public RegexBasedWhiteListFilter(Set<Pattern> patterns, boolean lowerCaseComparison) {
        super(patterns, true, lowerCaseComparison);
        setDecisionLogger(LoggerFactory.getLogger(RegexBasedWhiteListFilter.class));
    }

}
