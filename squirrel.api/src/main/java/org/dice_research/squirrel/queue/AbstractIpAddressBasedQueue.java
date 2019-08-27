package org.dice_research.squirrel.queue;

import java.net.InetAddress;
import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.group.UriGroupByOperator;

/**
 * This abstract class manages two important aspects of an IpAddressBasedQueue.
 * It uses a mutex to access the queue and it manages a set containing IPs that
 * are currently blocked by one of the workers.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
@SuppressWarnings("deprecation")
public abstract class AbstractIpAddressBasedQueue extends AbstractGroupingQueue<InetAddress> implements IpAddressBasedQueue {
}
    public AbstractIpAddressBasedQueue() {
        super(new UriGroupByOperator<InetAddress>() {
            @Override
            public InetAddress retrieveKey(CrawleableUri uri) {
                return uri.getIpAddress();
            }
        });
    }

    @Override
    public int getNumberOfBlockedIps() {
        return getNumberOfBlockedKeys();
    }
  
    public List<CrawleableUri> getNextUris() {
        try {
            queueMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex. Throwing exception.", e);
            throw new IllegalStateException("Interrupted while waiting for mutex.", e);
        }
        IpUriTypePair pair;
        try {
            Iterator<IpUriTypePair> iterator = getIterator();
            do {
                if (!iterator.hasNext()) {
                    return null;
                }
                pair = iterator.next();
                
            } while (blockedIps.contains(pair.getIp()));
            blockedIps.add(pair.getIp());
        } finally {
            queueMutex.release();
        }
        return getUris(pair);

    }
    
    @Override
    public void markIpAddressAsAccessible(InetAddress ip) {
        throw new IllegalAccessError("This method is not supported, anymore.");
    }
    
    @Override
    public Iterator<SimpleEntry<InetAddress, List<CrawleableUri>>> getIPURIIterator() {
        return getIterator();
    }
}
