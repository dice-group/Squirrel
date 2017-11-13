package org.aksw.simba.squirrel.collect;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.CrawleableUriSerializer;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleUriCollector implements UriCollector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUriCollector.class);

    protected Map<String, Map<String, String>> urisOfUris = new HashMap<String, Map<String, String>>();
    protected CrawleableUriSerializer serializer;

    public SimpleUriCollector(CrawleableUriSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        urisOfUris.put(uri.getUri().toString(), new HashMap<String, String>());
    }

    @Override
    public Iterator<String> getUris(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        Map<String, String> set = null;
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
        Map<String, String> uris = urisOfUris.get(uriString);
        uris.put(newUri.getUri().toString(), serializer.serialize(newUri));
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        urisOfUris.remove(uri.getUri().toString());
    }

}
