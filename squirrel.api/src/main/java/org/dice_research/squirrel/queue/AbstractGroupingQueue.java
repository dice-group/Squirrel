package org.dice_research.squirrel.queue;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
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
     * @param groupByOperator Operator used to group URIs
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
     * @param uri      the URI that should be added to the queue
     * @param groupKey the group key which should be used to identify the group this URI
     *                 belongs to
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
     * @param groupKey key of the selected URI group
     * @return all URIs of the given group key
     */
    protected abstract List<CrawleableUri> getUris(T groupKey);

    /**
     * Removes the given set of URIs from the queue
     *
     * @param groupKey key of the given URI group
     * @param uris     set of URIs which should be removed
     */
    protected abstract void deleteUris(T groupKey, List<CrawleableUri> uris);

    /**
     * Returns if the queue is storing the crawled depth
     */
    public boolean isDepthIncluded() {
        return this.includeDepth;
    }

    @Override
    public List<CrawleableUri> addUris(List<CrawleableUri> uris) {
        synchronized (this) {
            return addKeywiseUris(makeURIsKeywise(uris), getMinNumOfUrisToCheck());
        }
    }

    /**
     * This method adds the {@link CrawleableUri} according to the domain/ip in the mongodb queue
     *
     * @param uri the {@link CrawleableUri} that has to be added to the mongodb queue domain/ip wise
     * @return score of the {@link CrawleableUri} stored in the mongodb queue
     */
    protected abstract float addKeywiseUri(CrawleableUri uri);

    /**
     * Group the {@link CrawleableUri}s according to their keys i.e. domain-wise/ip-wise
     *
     * @param uris the {@link CrawleableUri}s to be grouped
     * @return grouped {@link CrawleableUri}s
     */
    public Map<String, List<CrawleableUri>> makeURIsKeywise(List<CrawleableUri> uris) {
        List<CrawleableUri> distinctUris = uris.stream().distinct().collect(Collectors.toList());
        Map<String, List<CrawleableUri>> keyWiseUris = new HashMap<>();
        for (CrawleableUri uri : distinctUris) {
            String key = getKey(uri);
            List<CrawleableUri> uriList = keyWiseUris.get(key);
            if (CollectionUtils.isEmpty(uriList)) {
                uriList = new ArrayList<>();
            }
            uriList.add(uri);
            keyWiseUris.put(key, uriList);
        }
        return keyWiseUris;
    }

    /**
     * This method adds the {@link CrawleableUri}s according to the domain/ip in the mongodb queue. For each domain/ip,
     * minimum number of {@link CrawleableUri}s are checked for score, if they are above the critical score, all the
     * remaining are added to queue. If all of them are below the critical score, the {@link CrawleableUri}s are
     * considered duplicates and returned without adding to the queue.
     *
     * @param keyWiseUris {@link CrawleableUri}s to be added
     * @param minNumberOfUrisToCheck the minimum number of {@link CrawleableUri}s to be checked before adding remaining
     *                               {@link CrawleableUri}s to the queue
     * @return the {@link CrawleableUri}s which are not added to the queue because these are considered duplicates.
     */
    public List<CrawleableUri> addKeywiseUris(Map<String, List<CrawleableUri>> keyWiseUris, int minNumberOfUrisToCheck) {
        List<CrawleableUri> notAddedURIs = new ArrayList<>();
        Collection<List<CrawleableUri>> uriLists = keyWiseUris.values();
        for (List<CrawleableUri> uriList : uriLists) {
            boolean scoresBelowCritical = true;
            for (int i = 0; i < uriList.size(); i++) {
                if (i < minNumberOfUrisToCheck) {
                    float score = addKeywiseUri(uriList.get(i));
                    if (score > getCriticalScore()) {
                        scoresBelowCritical = false;
                    }
                    if (scoresBelowCritical) {
                        notAddedURIs.add(uriList.get(i));
                    }
                    continue;
                }
                if (scoresBelowCritical) {
                    notAddedURIs.add(uriList.get(i));
                } else {
                    addKeywiseUri(uriList.get(i));
                }
            }
        }
        return notAddedURIs;
    }

    /**
     * Get the key of the {@link CrawleableUri} i.e. either domain or ip
     *
     * @param uri the {@link CrawleableUri} whose key has to be returned
     * @return the key of the {@link CrawleableUri}
     */
    protected abstract String getKey(CrawleableUri uri);

    /**
     * The critical score which for {@link CrawleableUri}s according to their keys are returned.
     *
     * @return Critical score w.r.t key
     */
    protected abstract float getCriticalScore();

    /**
     * the minimum number of {@link CrawleableUri}s to be checked before adding the rest of the {@link CrawleableUri}s
     * to the queue are returned w.r.t. key
     *
     * @return minimum number of keys to be checked
     */
    protected abstract int getMinNumOfUrisToCheck();
}
