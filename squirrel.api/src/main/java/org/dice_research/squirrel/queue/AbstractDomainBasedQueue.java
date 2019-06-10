package org.dice_research.squirrel.queue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

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
    private Set<DomainUriTypePair> blockedDomains = new HashSet<DomainUriTypePair>();


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
        try {
            queueMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex. Throwing exception.", e);
            throw new IllegalStateException("Interrupted while waiting for mutex.", e);
        }
        DomainUriTypePair domain;
        try {
            Iterator<DomainUriTypePair> iterator = getIterator();
            do {
                if (!iterator.hasNext()) {
                    return null;
                }
                domain = iterator.next();
            } while (blockedDomains.contains(domain));
            blockedDomains.add(domain);
        } finally {
            queueMutex.release();
        }
        return getUris(domain);
    }
    
    @Override
        public int getNumberOfBlockedDomains() {
            // TODO Auto-generated method stub
            return blockedDomains.size();
        }
    
    @Override
    public void markDomainAsAccessible(String domainName) {
        blockedDomains.remove(domainName);        
    }
    
    protected abstract Iterator<DomainUriTypePair> getIterator();
    
    protected abstract List<CrawleableUri> getUris(DomainUriTypePair domain);


  

}
