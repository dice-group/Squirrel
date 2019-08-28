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
    
    @Override
    public void markIpAddressAsAccessible(InetAddress ip) {
        throw new IllegalAccessError("This method is not supported, anymore.");
    }
    
    @Override
    public Iterator<SimpleEntry<InetAddress, List<CrawleableUri>>> getIPURIIterator() {
        return getIterator();
    }
}
