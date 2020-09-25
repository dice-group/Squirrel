package org.dice_research.squirrel.queue;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.group.UriGroupByOperator;

/**
 * This abstract class manages two important aspects of a queue. It uses a mutex
 * to access the queue and it manages a set containing Domains that are
 * currently blocked by one of the workers.
 *
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 */

public abstract class AbstractGroupingQueue<T> implements BlockingQueue<T> {

    /**
     * Operator used to group URIs.
     */
    private UriGroupByOperator<T> groupByOperator;
    /**
     * Set of blocked key values.
     */
    private Set<T> blockedKeys = new HashSet<T>();
    
    /**
     * if the queue will store the depth or not
     */
    protected boolean includeDepth = false;

    /**
     * Constructor.
     * 
     * @param groupByOperator
     *            Operator used to group URIs
     */
    public AbstractGroupingQueue(UriGroupByOperator<T> groupByOperator) {
        this.groupByOperator = groupByOperator;
    }

    @Override
    public void addUri(CrawleableUri uri) {
        synchronized (this) {
            addUri(uri, groupByOperator.retrieveKey(uri));
        }
    }

    /**
     * Adds the given URI with they given group key to the queue.
     * 
     * @param uri
     *            the URI that should be added to the queue
     * @param groupKey
     *            the group key which should be used to identify the group this URI
     *            belongs to
     */
    protected abstract void addUri(CrawleableUri uri, T groupKey);

    @Override
    public List<CrawleableUri> getNextUris() {
        synchronized (this) {
            T key;
            Iterator<T> iterator = getGroupIterator();
            do {
                if (!iterator.hasNext()) {
                    return null;
                }
                key = iterator.next();
            } while (blockedKeys.contains(key));
            blockedKeys.add(key);
            return getUris(key);
        }
    }

    @Override
    public int getNumberOfBlockedKeys() {
        return blockedKeys.size();
    }

    @Override
    public void markUrisAsAccessible(Collection<CrawleableUri> uris) {
        synchronized (this) {
            for (Entry<T, List<CrawleableUri>> uriGroup : groupByOperator.groupByKey(uris).entrySet()) {
                blockedKeys.remove(uriGroup.getKey());
                deleteUris(uriGroup.getKey(), uriGroup.getValue());
            }
        }
    }

    @Override
    public Iterator<SimpleEntry<T, List<CrawleableUri>>> getIterator() {
        return new Iterator<AbstractMap.SimpleEntry<T, List<CrawleableUri>>>() {

            Iterator<T> cursor = getGroupIterator();

            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            @Override
            public SimpleEntry<T, List<CrawleableUri>> next() {
                T key = cursor.next();
                return new AbstractMap.SimpleEntry<>(key, getUris(key));
            }
        };
    }

    /**
     * Returns an iterator over all group keys that are currently in the queue.
     * 
     * @return all group keys currently in the queue
     */
    protected abstract Iterator<T> getGroupIterator();

    /**
     * Returns all URIs of the given group key
     * 
     * @param groupKey
     *            key of the selected URI group
     * @return all URIs of the given group key
     */
    protected abstract List<CrawleableUri> getUris(T groupKey);

    /**
     * Removes the given set of URIs from the queue
     * 
     * @param groupKey
     *            key of the given URI group
     * @param uris
     *            set of URIs which should be removed
     */
    protected abstract void deleteUris(T groupKey, List<CrawleableUri> uris);
    
    /**
     * Returns if the queue is storing the crawled depth
     * 
     */
    public boolean isDepthIncluded() {
    	return this.includeDepth;
    }

}
