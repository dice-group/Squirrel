package org.aksw.simba.squirrel.queue;

import java.net.InetAddress;
import java.util.*;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public class InMemoryQueue extends AbstractIpAddressBasedQueue {

    protected SortedMap<IpUriTypePair, List<CrawleableUri>> queue;

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
    public LinkedHashMap<InetAddress, List<CrawleableUri>> getContent() {
        LinkedHashMap<InetAddress, List<CrawleableUri>> ret = new LinkedHashMap<>();
        queue.entrySet().forEach(e -> ret.put(e.getKey().ip, e.getValue()));

        return ret;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

}
