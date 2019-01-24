package org.dice_research.squirrel.queue;

import java.net.InetAddress;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.dice_research.squirrel.data.uri.CrawleableUri;

public class InMemoryQueue extends AbstractIpAddressBasedQueue {

    protected SortedMap<IpUriTypePair, List<CrawleableUri>> queue;
    private static final int LIMITFORITERATOR = 50;

    public InMemoryQueue() {
        queue = new TreeMap<IpUriTypePair, List<CrawleableUri>>();
    }

    public InMemoryQueue(Comparator<IpUriTypePair> comparator) {
        queue = new TreeMap<IpUriTypePair, List<CrawleableUri>>(comparator);
    }

    @Override
    protected void addToQueue(CrawleableUri uri) {
        IpUriTypePair pair = new IpUriTypePair(uri.getIpAddress(), uri.getType());
        if (queue.containsKey(pair)) {
            queue.get(pair).add(uri);
        } else {
            List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
            uris.add(uri);
            queue.put(pair, uris);
        }
    }

    @Override
    protected Iterator<IpUriTypePair> getIterator() {
        return queue.keySet().iterator();
    }

    @Override
    protected List<CrawleableUri> getUris(IpUriTypePair pair) {
        List<CrawleableUri> uris = null;
        if (queue.containsKey(pair)) {
            uris = queue.remove(pair);
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
    public Iterator<AbstractMap.SimpleEntry<InetAddress, List<CrawleableUri>>> getIPURIIterator() {
        return queue.entrySet().stream().limit(LIMITFORITERATOR).map(e -> new AbstractMap.SimpleEntry<>(e.getKey().ip, e.getValue())).iterator();
    }

}
