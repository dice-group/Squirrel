package org.aksw.simba.squirrel.sink.impl.sparql;

import org.aksw.simba.squirrel.components.FrontierComponent;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.queue.RDBQueue;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.NodeFactory;
import org.aksw.simba.squirrel.sink.tripleBased.AdvancedTripleBasedSink;
import org.apache.jena.graph.Triple;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SparqlBasedSink implements AdvancedTripleBasedSink, Sink {
    /**
     * Interval that specifies how many triples are to be buffered at once until they are sent to the DB.
     */
    private static final int SENDING_INTERVAL_BUFFERED_TRIPLES = 100;
    /**
     * The URI of the DB in which updates can be performed.
     */
    private String updateDatasetURI;
    /**
     * The URI of the DB in which querys can be performed.
     */
    private String queryDatasetURI;
    /**
     * The data structure (map) in which the triples are buffered.
     */
    private ConcurrentHashMap<CrawleableUri, ConcurrentLinkedQueue<Triple>> mapBufferedTriples = new ConcurrentHashMap<>();

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);

    /**
     * Constructor of SparqlBasedSink
     *
     * @param updateDatasetURI The URI of the DB in which updates can be performed.
     * @param queryDatasetURI  The URI of the DB in which querys can be performed.
     */
    public SparqlBasedSink(String updateDatasetURI, String queryDatasetURI) {
        this.updateDatasetURI = updateDatasetURI;
        this.queryDatasetURI = queryDatasetURI;
    }

    public void addMetadata() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        if (mapBufferedTriples.get(uri) == null) {
            LOGGER.info("Sink has not been opened for the uri, sink will be opened");
            openSinkForUri(uri);
        }
        mapBufferedTriples.get(uri).add(triple);

        if (mapBufferedTriples.get(uri).size() >= SENDING_INTERVAL_BUFFERED_TRIPLES) {
            sendAllTriplesToDB(uri, mapBufferedTriples.get(uri));
        }
    }

    @Override
    public List<Triple> getTriplesForGraph(CrawleableUri uri) {
        // TODO: Implement!
//        throw new UnsupportedOperationException("Not yet implemented.");
        return new ArrayList<>();
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        mapBufferedTriples.put(uri, new ConcurrentLinkedQueue<>());
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        if (mapBufferedTriples.get(uri) == null) {
            LOGGER.info("Try to close Sink for an uri, without open it before. Do nothing.");
        }
        if (!mapBufferedTriples.get(uri).isEmpty()) {
            sendAllTriplesToDB(uri, mapBufferedTriples.get(uri));
        }
        mapBufferedTriples.remove(uri);
    }

    /**
     * Method to send all buffered triples to the database
     * @param uri
     * @param tripleList
     */
    private void sendAllTriplesToDB(CrawleableUri uri, ConcurrentLinkedQueue<Triple> tripleList) {
        UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(getGraphId(uri), tripleList));
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, updateDatasetURI);
        proc.execute();
    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the id of the graph in which the given uri is stored.
     *
     * @param uri The given uri.
     * @return The id of the graph.
     */
    public String getGraphId(CrawleableUri uri) {
        return (String) uri.getData(CrawleableUri.UUID_KEY);
    }
}
