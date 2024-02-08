package org.dice_research.squirrel.data.uri.filter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
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

/**
 * Abstract {@link UriFilter} implementation that is based on a set of regular
 * expression patterns. Each given URI is tested against the patterns in the
 * list within the {@link #isUriGood(CrawleableUri)} method. The first pattern
 * that matches will cause the filter to return the given
 * {@link #foundMatchReturnValue} value. If no pattern matches, the inverse of
 * the given value is returned. If no patterns are given, or the set of patterns
 * is empty, for all URIs {@code true} is returned.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public abstract class AbstractRegexBasedFilter implements UriFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRegexBasedFilter.class);

    private Set<Pattern> patterns;
    private boolean foundMatchReturnValue;
    private boolean lowerCaseComparison;
    private String foundMatchLogMsg;
    private String notFoundMatchLogMsg;
    protected Logger decisionLogger = LOGGER;

    public AbstractRegexBasedFilter(Set<Pattern> patterns, boolean foundMatchReturnValue, boolean lowerCaseComparison) {
        this.patterns = patterns;
        this.foundMatchReturnValue = foundMatchReturnValue;
        this.lowerCaseComparison = lowerCaseComparison;
        if (foundMatchReturnValue) {
            foundMatchLogMsg = "URI {} is good";
            notFoundMatchLogMsg = "URI {} is not good";
        } else {
            foundMatchLogMsg = "URI {} is not good";
            notFoundMatchLogMsg = "URI {} is good";
        }
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        if (patterns == null || patterns.isEmpty()) {
            decisionLogger.debug("Not correctly configured so all URIs are good.");
            return true;
        } else {
            String uriString = uri.getUri().toString();
            if (lowerCaseComparison) {
                uriString = uriString.toLowerCase();
            }
            for (Pattern p : patterns) {
                Matcher m = p.matcher(uriString);
                if (m.find()) {
                    decisionLogger.debug(foundMatchLogMsg, uri.toString());
                    return foundMatchReturnValue;
                }
            }
        }
        decisionLogger.debug(notFoundMatchLogMsg, uri.toString());
        return !foundMatchReturnValue;
    }

    /**
     * @param decisionLogger the decisionLogger to set
     */
    public void setDecisionLogger(Logger decisionLogger) {
        this.decisionLogger = decisionLogger;
    }

    public static Set<Pattern> parsePatterns(File patternFile) {
        try {
            return parsePatterns(FileUtils.readLines(patternFile, StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.error("A problem was found when loading patterns. Returning empty set.", e);
        }
        return Collections.emptySet();
    }

    public static Set<Pattern> parsePatterns(List<String> patternList) {
        Set<Pattern> patterns = new HashSet<Pattern>();
        if (patternList != null) {
            for (String s : patternList) {
                if ((s != null) && (!s.isEmpty())) {
                    try {
                        patterns.add(Pattern.compile(s));
                    } catch (PatternSyntaxException e) {
                        LOGGER.error("Got an incorrect regex pattern. It will be ignored.", e);
                    }
                }
            }
        }
        return patterns;
    }

}
