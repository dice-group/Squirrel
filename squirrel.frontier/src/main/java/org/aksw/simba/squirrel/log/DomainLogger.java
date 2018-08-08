package org.aksw.simba.squirrel.log;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainLogger implements Iterator<String>, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainLogger.class);

    public static Iterator<String> createIfPossible(CrawleableUri uri, String logFile, Iterator<String> iterator) {
        if(logFile == null) {
        	return iterator;
        }
    	DomainLogger logger = create(uri, logFile, iterator);
        return logger == null ? iterator : logger;
    }

    public static DomainLogger create(CrawleableUri uri, String logFile, Iterator<String> iterator) {
        try {
            File file = new File(logFile);
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter writer = new FileWriter(file, true);
            return new DomainLogger(uri, writer, iterator);
        } catch (IOException e) {
            LOGGER.error("Couldn't create log file. Returning null.", e);
        }
        return null;
    }

    protected String domain;
    protected FileWriter writer;
    protected Set<String> domainCache = new HashSet<String>();
    protected Iterator<String> iterator;

    public DomainLogger(CrawleableUri uri, FileWriter writer, Iterator<String> iterator) {
        this.domain = uri.getUri().getAuthority();
        domainCache.add(domain);
        this.writer = writer;
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public String next() {
        String next = iterator.next();
        String nextDomain = URI.create(next).getAuthority();
        if (!domainCache.contains(nextDomain)) {
            try {
                writer.write(domain);
                writer.write(" --> ");
                writer.write(nextDomain);
                writer.write('\n');
            } catch (IOException e) {
                LOGGER.error("Error while logging.", e);
            }
            domainCache.add(nextDomain);
        }
        return next;
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(writer);
    }
}
