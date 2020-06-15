package org.dice_research.squirrel.queue;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * An IP-based queue which holds its data in memory.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class InMemoryQueue extends AbstractIpAddressBasedQueue implements Comparator<InetAddress> {

    protected SortedMap<InetAddress, List<CrawleableUri>> queue;

    public InMemoryQueue() {
        queue = new TreeMap<InetAddress, List<CrawleableUri>>(this);
    }

    @Override
    protected void addUri(CrawleableUri uri, InetAddress address) {
        if (queue.containsKey(address)) {
            queue.get(address).add(uri);
        } else {
            List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
            uris.add(uri);
            queue.put(address, uris);
        }
    }

    @Override
    protected Iterator<InetAddress> getGroupIterator() {
        return queue.keySet().iterator();
    }

    @Override
    protected List<CrawleableUri> getUris(InetAddress address) {
        List<CrawleableUri> uris = null;
        if (queue.containsKey(address)) {
            // Create a new list to make sure that the internal list can not be changed from
            // outside and that internal changes do not take effect in the list that is
            // retrieved by this method.
            uris = new ArrayList<>(queue.get(address));
        }
        return uris;
    }

    @Override
    public void open() {
        // nothing to do
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    protected void deleteUris(InetAddress address, List<CrawleableUri> uris) {
        if (queue.containsKey(address)) {
            List<CrawleableUri> queuedUris = queue.get(address);
            queuedUris.removeAll(uris);
            if (queuedUris.isEmpty()) {
                queue.remove(address);
            }
        }
    }

    @Override
    public int compare(InetAddress a1, InetAddress a2) {
        byte[] ip1 = a1.getAddress();
        byte[] ip2 = a2.getAddress();
        for (int i = 0; i < ip1.length; ++i) {
            if (ip1[i] < ip2[i]) {
                return -1;
            } else if (ip1[i] > ip2[i]) {
                return 1;
            }
        }
        return 0;
    }

	@Override
	public boolean isDepthIncluded() {
		// TODO Auto-generated method stub
		return false;
	}

}
