package org.aksw.simba.squirrel.frontier.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.frontier.ExtendedFrontier;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.queue.UriQueue;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtendedFrontierImpl extends FrontierImpl implements ExtendedFrontier {


    public ExtendedFrontierImpl(KnownUriFilter knownUriFilter, UriQueue queue) {
        super(knownUriFilter, queue);
    }

    @Override
    public void informAboutDeadWorker(int idOfWorker, List<CrawleableUri> lstUrisToReassign) {
        if (queue instanceof IpAddressBasedQueue) {
            IpAddressBasedQueue ipQueue = (IpAddressBasedQueue) queue;

            Set<InetAddress> setIps = new HashSet<>();
            for (CrawleableUri uri : lstUrisToReassign) {
                InetAddress ip = uri.getIpAddress();
                setIps.add(ip);
            }
            setIps.forEach(ip -> ipQueue.markIpAddressAsAccessible(ip));
        }
    }
}
