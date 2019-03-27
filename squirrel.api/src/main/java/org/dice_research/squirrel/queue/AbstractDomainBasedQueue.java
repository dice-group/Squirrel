package org.dice_research.squirrel.queue;

import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.Semaphore;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class manages two important aspects of an DomainBasedQueue.
 * It uses a mutex to access the queue and it manages a set containing Domains that
 * are currently blocked by one of the workers.
 *
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *  */

public abstract class AbstractDomainBasedQueue implements DomainBasedQueue {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDomainBasedQueue.class);
    
    private Semaphore queueMutex = new Semaphore(1);
    private Set<String> blockedDomains = new HashSet<String>();


    @Override
    public void addUri(CrawleableUri uri) {
        try {
            queueMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex. Throwing exception.", e);
            throw new IllegalStateException("Interrupted while waiting for mutex.", e);
        }
        try {
            addToQueue(uri);
        } finally {
            queueMutex.release();
        }
        
    }
    
    @Override
    public List<CrawleableUri> getNextUris() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void open() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void markIpAddressAsAccessible(String domainName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getNumberOfBlockedDomains() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Iterator<SimpleEntry<String, List<CrawleableUri>>> getDomainIterator() {
        // TODO Auto-generated method stub
        return null;
    }

}
