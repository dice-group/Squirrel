package org.dice_research.squirrel.collect;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple in-memory implementation of the {@link UriCollector} interface based
 * on the given {@link Serializer}.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SimpleUriCollector implements UriCollector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUriCollector.class);
    /**
     * Number of all URIs collected.
     */
    private long total_uris = 0;
    /**
     * Mapping from URIs to the new URIs that have been found.
     */
    protected Map<String, Map<String, byte[]>> urisOfUris = new HashMap<String, Map<String, byte[]>>();
    /**
     * {@link Serializer} used to serialize the given URIs.
     */
    protected Serializer serializer;

    /**
     * Constructor.
     * 
     * @param serializer
     *            the serializer that is used to serialize the new URIs.
     */
    public SimpleUriCollector(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        urisOfUris.put(uri.getUri().toString(), new HashMap<String, byte[]>());
    }

    @Override
    public Iterator<byte[]> getUris(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        Map<String, byte[]> set = null;
        if (urisOfUris.containsKey(uriString)) {
            set = urisOfUris.get(uriString);
        }
        return set == null ? Collections.emptyIterator() : set.values().iterator();
    }

    @Override
    public void addNewUri(CrawleableUri uri, CrawleableUri newUri) {
        String uriString = uri.getUri().toString();
        if (!urisOfUris.containsKey(uriString)) {
            LOGGER.error("The URI {} is not known. The open method was not called with this URI.", uriString);
            return;
        }
        Map<String, byte[]> uris = urisOfUris.get(uriString);
        try {
            uris.put(newUri.getUri().toString(), serializer.serialize(newUri));
            total_uris++;
        } catch (IOException e) {
            LOGGER.error("Error while trying to collect URI \"" + newUri + "\". It will be ignored.", e);
        }
    }

    /**
     * Returns the total number of new URIs that have been added to this collector.
     * 
     * @return the total number of new URIs that have been added to this collector.
     */
    public long getSize() {
        return total_uris;
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        urisOfUris.remove(uri.getUri().toString());
    }

    @Override
    public long getSize(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        if(urisOfUris.containsKey(uriString)) {
           return urisOfUris.get(uriString).size();
        } else {
            return 0;
        }
    }

}
