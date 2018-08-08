package org.aksw.simba.squirrel.graph.impl;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.frontier.utils.SimpleDomainExtractor;
import org.aksw.simba.squirrel.graph.GraphLogger;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabSeparatedGraphLogger implements GraphLogger, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TabSeparatedGraphLogger.class);

    public static final char DEFAULT_SOURCE_TARGET_SEPARATOR = '\t';
    public static final char DEFAULT_URI_SEPARATOR = '|';
    public static final char DEFAULT_QUOTE_CHAR = '"';
    public static final char DEFAULT_ESCAPE_CHAR = '\\';

    private static final Charset CHARSET = Charset.forName("UTF-8");

    public static TabSeparatedGraphLogger create(File logFile) throws FileNotFoundException {
        return new TabSeparatedGraphLogger(new FileOutputStream(logFile));
    }

    protected OutputStream logStream;
    protected char sourceTargetSeperator = DEFAULT_SOURCE_TARGET_SEPARATOR;
    protected char uriSeparator = DEFAULT_URI_SEPARATOR;
    protected char quoteChar = DEFAULT_QUOTE_CHAR;
    protected char escapeChar = DEFAULT_ESCAPE_CHAR;
    protected String quoteString = new String(new char[] { quoteChar });
    protected String escapedQuote = new String(new char[] { escapeChar, quoteChar });
    protected String linebreak = String.format("%n");

    public TabSeparatedGraphLogger(OutputStream logStream) {
        this.logStream = logStream;
    }

    public void log(List<CrawleableUri> crawledUris, List<CrawleableUri> newUris) {
        StringBuilder builder = new StringBuilder();
        Set<String> uriSet = new HashSet<String>();
        for (CrawleableUri uri : crawledUris) {
            uriSet.add(SimpleDomainExtractor.extractDomainAndPath(uri.getUri().toString()));
        }
        addUris(uriSet, builder);
        builder.append(sourceTargetSeperator);
        uriSet.clear();
        for (CrawleableUri uri : newUris) {
            uriSet.add(SimpleDomainExtractor.extractDomainAndPath(uri.getUri().toString()));
        }
        addUris(uriSet, builder);
        builder.append(linebreak);
        try {
            logStream.write(builder.toString().getBytes(CHARSET));
            logStream.flush();
        } catch (IOException e) {
            LOGGER.error("Couldn't log graph data to output stream.", e);
        }
    }

    private void addUris(Set<String> uriSet, StringBuilder builder) {
        boolean first = true;
        builder.append(quoteString);
        for (String uri : uriSet) {
            if (first) {
                first = false;
            } else {
                builder.append(uriSeparator);
            }
            builder.append(uri.replaceAll(quoteString, escapedQuote));
        }
        builder.append(quoteString);
    }

    public char getSourceTargetSeperator() {
        return sourceTargetSeperator;
    }

    public void setSourceTargetSeperator(char sourceTargetSeperator) {
        this.sourceTargetSeperator = sourceTargetSeperator;
    }

    public char getUriSeparator() {
        return uriSeparator;
    }

    public void setUriSeparator(char uriSeparator) {
        this.uriSeparator = uriSeparator;
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(char quoteChar) {
        this.quoteChar = quoteChar;
        quoteString = new String(new char[] { quoteChar });
        escapedQuote = new String(new char[] { escapeChar, quoteChar });
    }

    public char getEscapeChar() {
        return escapeChar;
    }

    public void setEscapeChar(char escapeChar) {
        this.escapeChar = escapeChar;
        escapedQuote = new String(new char[] { escapeChar, quoteChar });
    }

    public String getLinebreak() {
        return linebreak;
    }

    public void setLinebreak(String linebreak) {
        this.linebreak = linebreak;
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(logStream);
    };
}
