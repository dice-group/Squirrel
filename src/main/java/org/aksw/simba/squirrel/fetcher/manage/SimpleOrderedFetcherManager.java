package org.aksw.simba.squirrel.fetcher.manage;

import java.io.File;
import java.io.IOException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.fetcher.Fetcher;
import org.aksw.simba.squirrel.fetcher.ftp.FTPFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A very simple manager for {@link Fetcher} instances that is based on the
 * order of the given fetchers. If the manager has got the fetchers A and B, it
 * will first try to fetch data from the given URI using A. If this is
 * successful (i.e., {@code A.fetch(uri) != null}) the result of {@link Fetcher}
 * A is returned. Only if A returns {@code null} {@link Fetcher} B is used.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SimpleOrderedFetcherManager implements Fetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FTPFetcher.class);

    private Fetcher[] fetchers;

    public SimpleOrderedFetcherManager(Fetcher... fetchers) {
        this.fetchers = fetchers;
    }
    
    public void setFetchers(Fetcher... fetchers) {
        this.fetchers = fetchers;
    }
    
    public Fetcher[] getFetchers() {
        return fetchers;
    }

    @Override
    public File fetch(CrawleableUri uri) {
        File resultFile = null;
        int fetcherId = 0;
        while ((resultFile == null) && (fetcherId < fetchers.length)) {
            resultFile = fetchers[fetcherId].fetch(uri);
            ++fetcherId;
        }
        return resultFile;
    }

    @Override
    public void close() throws IOException {
        for(Fetcher fetcher : fetchers) {
            try {
                fetcher.close();
            }catch (IOException e) {
                LOGGER.info("Exception while closing fetcher.", e);
            }
        }
    }

}
