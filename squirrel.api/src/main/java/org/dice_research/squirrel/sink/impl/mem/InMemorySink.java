package org.dice_research.squirrel.sink.impl.mem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.Quad;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * This is a simple in-memory implementation of a sink that can be used for
 * testing purposes. The data that is written to this sink can be accessed using
 * the {@link #getCrawledRdfData()} method. If the sink encounters a problem
 * during its usage, e.g., data is written using a URI for which the stream
 * already has been closed before, the sink becomes unhealthy. Note that this
 * status does not influence the functionality of the sink. The status of the
 * sink, i.e., whether it is healthy or not, can be accessed using the
 * {@link #isSinkHealthy()} method.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class InMemorySink implements Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySink.class);

    /**
     * In-memory map used to store the RDF data that is written to the sink.
     */
    private Map<String, Model> rdfData = new HashMap<String, Model>();

    /**
     * In-memory map used to store the unstructured data that is written to the
     * sink.
     */
    private Map<String, List<byte[]>> unstrcuturedData = new HashMap<String, List<byte[]>>();
    /**
     * Set of URIs for which the sink has already been closed.
     */
    private Set<String> closedSinks = new HashSet<String>();
    /**
     * The healthyness of the sink that is set to false if an error is encountered.
     */
    private boolean healthyness = true;

    public InMemorySink() {
        openSinkForUri(new CrawleableUri(Constants.DEFAULT_META_DATA_GRAPH_URI));
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        String uriString = uri.getUri().toString();
        if (rdfData.containsKey(uriString)) {
            Model model = rdfData.get(uriString);
            Resource s;
            Node n = triple.getSubject();
            if (n.isBlank()) {
                s = model.createResource(new AnonId(triple.getSubject().getBlankNodeId()));
            } else {
                s = model.createResource(triple.getSubject().getURI());
            }
            Property p = model.createProperty(triple.getPredicate().getURI());
            if (triple.getObject().isURI()) {
                model.add(s, p, model.createResource(triple.getObject().getURI()));
            } else if (triple.getObject().isBlank()) {
                model.add(s, p, model.createResource(new AnonId(triple.getObject().getBlankNodeId())));
            } else {
                model.add(s, p, triple.getObject().getLiteralValue().toString());
            }
        } else {
            LOGGER.error("Called to add a triple to the URI \"" + uriString + "\" which has never been opened.");
            healthyness = false;
        }
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        if (!rdfData.containsKey(uriString)) {
            rdfData.put(uriString, ModelFactory.createDefaultModel());
        }
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        String uriString = uri.getUri().toString();
        if (rdfData.containsKey(uriString)) {
            closedSinks.add(uriString);
        } else {
            LOGGER.error("Called to close the sink for the URI \"" + uriString + "\" which has never been opened.");
            healthyness = false;
        }
    }

    /**
     * Returns the data written to the sink as a map with the crawled URI as key and
     * the RDF data as value.
     *
     * @return the data written to the sink.
     */
    public Map<String, Model> getCrawledRdfData() {
        return rdfData;
    }

    /**
     * Returns the data written to the sink as a map with the crawled URI as key and
     * the unstructured data as value.
     *
     * @return the data written to the sink.
     */
    public Map<String, List<byte[]>> getCrawledUnstructuredData() {
        return unstrcuturedData;
    }

    /**
     * Returns the status of the sink.
     *
     * @return the status of the sink.
     */
    public boolean isSinkHealthy() {
        SetView<String> unclosedSinks = Sets.difference(rdfData.keySet(), closedSinks);
        if (unclosedSinks.size() > 0) {
            LOGGER.error("Some sinks have not been closed: " + unclosedSinks.toString());
            healthyness = false;
        }
        return healthyness;
    }

    @Override
    public void addData(CrawleableUri uri, byte[] data) {
        List<byte[]> list;
        String uriString = uri.getUri().toString();
        if (unstrcuturedData.containsKey(uriString)) {
            list = unstrcuturedData.get(uriString);
        } else {
            list = new ArrayList<byte[]>();
            unstrcuturedData.put(uriString, list);
        }
        list.add(data);
    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {
        try {
            addData(uri, IOUtils.toByteArray(stream));
        } catch (IOException e) {
            LOGGER.error("Error while reading data from stream. The data won't be stored.", e);
        }
    }

    @Override
    public void addQuad(CrawleableUri uri, Quad quad) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void flushMetadata() {
        // TODO Auto-generated method stub
        
    }
}
