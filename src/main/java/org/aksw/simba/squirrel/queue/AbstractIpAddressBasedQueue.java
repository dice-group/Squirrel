package org.aksw.simba.squirrel.queue;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This abstract class manages two important aspects of an IpAddressBasedQueue.
 * It uses a mutex to access the queue and it manages a set containing IPs that
 * are currently blocked by one of the workers.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public abstract class AbstractIpAddressBasedQueue implements IpAddressBasedQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIpAddressBasedQueue.class);

    private Semaphore queueMutex = new Semaphore(1);
    private Set<InetAddress> blockedIps = new HashSet<InetAddress>();


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

    protected abstract void addToQueue(CrawleableUri uri);

    @Override
    public List<CrawleableUri> getNextUris() {
        try {
            queueMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex. Throwing exception.", e);
            throw new IllegalStateException("Interrupted while waiting for mutex.", e);
        }
        try {
            Iterator<IpUriTypePair> iterator = getIterator();
            IpUriTypePair pair;
            do {
                if (!iterator.hasNext()) {
                    return null;
                }
                pair = iterator.next();
            } while (blockedIps.contains(pair.ip));
            blockedIps.add(pair.ip);
            return getUris(pair);
        } finally {
            queueMutex.release();
        }
    }

    protected abstract Iterator<IpUriTypePair> getIterator();

    protected abstract List<CrawleableUri> getUris(IpUriTypePair pair);

    @Override
    public void markIpAddressAsAccessible(InetAddress ip) {
        blockedIps.remove(ip);
    }

    @Override
    public int getNumberOfBlockedIps() {
        return blockedIps.size();
    }
}
