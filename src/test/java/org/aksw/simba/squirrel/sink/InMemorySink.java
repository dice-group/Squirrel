package org.aksw.simba.squirrel.sink;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@Ignore
public class InMemorySink implements Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySink.class);

    private Map<String, Model> resources = new HashMap<String, Model>();
    private Set<String> closedSinks = new HashSet<String>();
    private boolean healthyness = true;

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        String uriString = uri.getUri().toString();
        if (resources.containsKey(uriString)) {
            Model model = resources.get(uriString);
            if (triple.getObject().isURI()) {
                model.add(model.createResource(triple.getSubject().getURI()),
                        model.createProperty(triple.getPredicate().getURI()),
                        model.createResource(triple.getObject().getURI()));
            } else if (triple.getObject().isBlank()) {
                model.add(model.createResource(triple.getSubject().getURI()),
                        model.createProperty(triple.getPredicate().getURI()),
                        model.createResource(triple.getObject().getBlankNodeId()));
            } else {
                model.add(model.createResource(triple.getSubject().getURI()),
                        model.createProperty(triple.getPredicate().getURI()), triple.getObject().toString());
            }
        } else {
            LOGGER.error("Called to add a triple to the URI \"" + uriString + "\" which has never been opened.");
            healthyness = false;
        }
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        if (!resources.containsKey(uriString)) {
            resources.put(uriString, ModelFactory.createDefaultModel());
        }
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        if (resources.containsKey(uriString)) {
            closedSinks.add(uriString);
        } else {
            LOGGER.error("Called to close the sink for the URI \"" + uriString + "\" which has never been opened.");
            healthyness = false;
        }
    }

    public Map<String, Model> getCrawledResources() {
        return resources;
    }

    public boolean isSinkHealthy() {
        SetView<String> unclosedSinks = Sets.difference(resources.keySet(), closedSinks);
        if (unclosedSinks.size() > 0) {
            LOGGER.error("Some sinks have not been closed: " + unclosedSinks.toString());
            healthyness = false;
        }
        return healthyness;
    }
}
