package org.aksw.simba.squirrel.queue;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.net.InetAddress;
import java.util.*;

public class InMemoryQueue extends AbstractIpAddressBasedQueue {

    protected SortedMap<IpUriTypePair, List<CrawleableUri>> queue;
    private static final int LIMITFORITERATOR = 50;

    public InMemoryQueue() {
        queue = new TreeMap<>();
    }

    public InMemoryQueue(Comparator<IpUriTypePair> comparator) {
        queue = new TreeMap<>(comparator);
    }

    @Override
    protected void addToQueue(CrawleableUri uri) {
        IpUriTypePair pair = new IpUriTypePair(uri.getIpAddress(), uri.getType());
        if (queue.containsKey(pair)) {
            queue.get(pair).add(uri);
        } else {
            List<CrawleableUri> uris = new ArrayList<>();
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
    public Iterator<AbstractMap.SimpleEntry<InetAddress, List<CrawleableUri>>> getIPURIIterator() {
        return queue.entrySet().stream().limit(LIMITFORITERATOR).map(e -> new AbstractMap.SimpleEntry<>(e.getKey().ip, e.getValue())).iterator();
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

}
