package org.dice_research.squirrel.data.uri.filter;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

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

    public RegexBasedBlackListFilter(File blacklistfile) {
        this(parsePatterns(blacklistfile));
    }

    public RegexBasedBlackListFilter(Set<Pattern> patterns) {
        super(patterns, false);
    }

}
