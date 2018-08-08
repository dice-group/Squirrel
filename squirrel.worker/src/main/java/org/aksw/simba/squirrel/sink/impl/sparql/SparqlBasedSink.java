package org.aksw.simba.squirrel.sink.impl.sparql;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.tripleBased.AdvancedTripleBasedSink;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A sink which stores the data in different graphs in a sparql based db.
 */
public class SparqlBasedSink implements AdvancedTripleBasedSink, Sink {
    /**
     * Interval that specifies how many triples are to be buffered at once until they are sent to the DB.
     */
    private static final int SENDING_INTERVAL_BUFFERED_TRIPLES = 500;

    /**
     * The URI to the metadata DB in which updates can be performed.
     */
    private final String updateMetaDataUri;
    /**
     * The URI to the metadata DB in which querys can be performed.
     */
    private final String queryMetaDataUri;

    /**
     * The URI of the DB in which updates can be performed.
     */
    private String updateDatasetURI;
    /**
     * The URI of the DB in which querys can be performed.
     */
    @SuppressWarnings("unused")
    private String queryDatasetURI;
    /**
     * The data structure (map) in which the triples are buffered.
     */
    private ConcurrentHashMap<CrawleableUri, ConcurrentLinkedQueue<Triple>> mapBufferedTriples = new ConcurrentHashMap<>();

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);

    /**
     * Uri for the MetaData graph, will be stored in the default graph
     */
    private CrawleableUri metaDataGraphUri;

    /**
     * Constructor of SparqlBasedSink.
     *
     * @param host                   The host name of the sink.
     * @param port                   The port of the sink.
     * @param updateAppendix         The update appendix for the content data
     * @param queryAppendix          The query appendix for the content data
     * @param updateMetaDataAppendix The update appendix for the meta data
     * @param queryMetaDataAppendix  The query appendix for the meta data
     */
    public SparqlBasedSink(String host, String port, String updateAppendix, String queryAppendix, String updateMetaDataAppendix, String queryMetaDataAppendix) {
        String prefix = "http://" + host + ":" + port + "/";
        updateDatasetURI = prefix + updateAppendix;
        queryDatasetURI = prefix + queryAppendix;
        updateMetaDataUri = prefix + updateMetaDataAppendix;
        queryMetaDataUri = prefix + queryMetaDataAppendix;
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
        Query selectQuery = null;
        if (uri.equals(metaDataGraphUri)) {
            selectQuery = QueryGenerator.getInstance().getSelectQuery();
        } else {
            selectQuery = QueryGenerator.getInstance().getSelectQuery((String) uri.getData(CrawleableUri.UUID_KEY));
        }

        QueryExecution qe = QueryExecutionFactory.sparqlService(queryDatasetURI, selectQuery);
        ResultSet rs = qe.execSelect();
        List<Triple> triplesFound = new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode subject = sol.get("subject");
            RDFNode predicate = sol.get("predicate");
            RDFNode object = sol.get("object");
            triplesFound.add(Triple.create(subject.asNode(), predicate.asNode(), object.asNode()));
        }
        qe.close();
        return triplesFound;
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        mapBufferedTriples.put(uri, new ConcurrentLinkedQueue<>());
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        if (mapBufferedTriples.get(uri) == null) {
            LOGGER.info("Try to close Sink for an uri, without open it before. Do nothing.");
            return;
        }
        if (!mapBufferedTriples.get(uri).isEmpty()) {
            sendAllTriplesToDB(uri, mapBufferedTriples.get(uri));
        }
        mapBufferedTriples.remove(uri);
    }

    /**
     * Method to send all buffered triples to the database
     *
     * @param uri        the crawled {@link CrawleableUri}
     * @param tripleList the list of {@link Triple}s regarding that uri
     */
    private void sendAllTriplesToDB(CrawleableUri uri, ConcurrentLinkedQueue<Triple> tripleList) {
        String stringQuery = null;
        String sparqlEndpoint;
        if (uri.equals(metaDataGraphUri)) {
            stringQuery = QueryGenerator.getInstance().getAddQuery(tripleList);
            sparqlEndpoint = updateMetaDataUri;
        } else {
            stringQuery = QueryGenerator.getInstance().getAddQuery(getGraphId(uri), tripleList);
            sparqlEndpoint = updateDatasetURI;
        }

        try {
            UpdateRequest request = UpdateFactory.create(stringQuery);
            UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, sparqlEndpoint);
            try {
                proc.execute();
            } catch (Exception e) {
                LOGGER.error("Was not able to send the triples to the database (SPARQL), may because the dataset does not exists. Information will get lost :( [" + request + "] on " + updateDatasetURI + " with " + tripleList.size() + " triples]", e);
            }
        } catch (QueryException e) {
            LOGGER.error(stringQuery);
            LOGGER.error("Query could not be parsed, no data will be written to the sink", e);
        }
    }

    @Override
    public void addMetaData(Model model) {
        metaDataGraphUri = new CrawleableUri(Constants.DEFAULT_META_DATA_GRAPH_URI);
        StmtIterator iterator = model.listStatements();

        openSinkForUri(metaDataGraphUri);
        while (iterator.hasNext()) {
            addTriple(metaDataGraphUri, iterator.next().asTriple());
        }
        closeSinkForUri(metaDataGraphUri);
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
        return "graph:" + uri.getData(CrawleableUri.UUID_KEY);
    }

    public String getUpdateDatasetURI() {
        return updateDatasetURI;
    }
}
