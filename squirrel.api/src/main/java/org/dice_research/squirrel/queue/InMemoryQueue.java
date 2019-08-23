package org.dice_research.squirrel.queue;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.dice_research.squirrel.data.uri.CrawleableUri;

public class InMemoryQueue extends AbstractIpAddressBasedQueue {

    protected SortedMap<InetAddress, List<CrawleableUri>> queue;

    public InMemoryQueue() {
        queue = new TreeMap<InetAddress, List<CrawleableUri>>();
    }

    public InMemoryQueue(Comparator<InetAddress> comparator) {
        queue = new TreeMap<InetAddress, List<CrawleableUri>>(comparator);
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
            uris = queue.get(address);
        }
        return uris;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
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
            if(queuedUris.isEmpty()) {
                queue.remove(address);
            }
        }
    }

}
