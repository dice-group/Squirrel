package org.dice_research.squirrel.queue;

import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * This abstract class manages two important aspects of an DomainBasedQueue.
 * It uses a mutex to access the queue and it manages a set containing Domains that
 * are currently blocked by one of the workers.
 *
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *  */

public class AbstractDomainBasedQueue implements DomainBasedQueue {

    @Override
    public void addUri(CrawleableUri uri) {
        // TODO Auto-generated method stub
        
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
