package org.aksw.simba.squirrel.collect;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleUriCollector implements UriCollector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUriCollector.class);
    
    protected Map<String, Set<String>> urisOfUris = new HashMap<String, Set<String>>();

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        urisOfUris.put(uri.getUri().toString(), new HashSet<String>());
    }

    @Override
    public Iterator<String> getUris(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        Set<String> set = null;
        if(urisOfUris.containsKey(uriString)) {
            set = urisOfUris.get(uriString);
        }
        return set == null ? Collections.emptyIterator() : set.iterator();
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        String uriString = uri.getUri().toString();
        if(!urisOfUris.containsKey(uriString)) {
            LOGGER.error("The URI {} is not known. The open method was not called with this URI.", uriString);
            return;
        }
        Set<String> uris = urisOfUris.get(uriString);
        addUri(triple.getSubject(), uris);
        addUri(triple.getPredicate(), uris);
        addUri(triple.getObject(), uris);
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        urisOfUris.remove(uri.getUri().toString());
    }

    protected static void addUri(Node node, Set<String> uris) {
        if (node.isURI()) {
            uris.add(node.getURI());
        }
    }
}
