package org.aksw.simba.squirrel.sink.impl.sparql;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.metadata.CrawlingActivity;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.tripleBased.AdvancedTripleBasedSink;
import org.aksw.simba.squirrel.vocab.Squirrel;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A sink which stores the data in different graphs in a sparql based db.
 */
public class SparqlBasedSink implements AdvancedTripleBasedSink, Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);
    /**
     * Interval that specifies how many triples are to be buffered at once until
     * they are sent to the DB.
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
    private Map<CrawleableUri, GraphStatus> mapBufferedTriples = Collections.synchronizedMap(new HashMap<>());

    /**
     * Uri for the MetaData graph, will be stored in the default graph
     */
    private CrawleableUri metaDataGraphUri;

    /**
     * Constructor of SparqlBasedSink.
     *
     * @param host
     *            The host name of the sink.
     * @param port
     *            The port of the sink.
     * @param updateAppendix
     *            The update appendix for the content data
     * @param queryAppendix
     *            The query appendix for the content data
     * @param updateMetaDataAppendix
     *            The update appendix for the meta data
     * @param queryMetaDataAppendix
     *            The query appendix for the meta data
     */
    public SparqlBasedSink(String host, String port, String updateAppendix, String queryAppendix,
            String updateMetaDataAppendix, String queryMetaDataAppendix) {
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
        mapBufferedTriples.get(uri).addTriple(this, uri, triple);
    }

    @Override
    public List<Triple> getTriplesForGraph(CrawleableUri uri) {
        Query selectQuery = null;
        if (uri.equals(metaDataGraphUri)) {
            selectQuery = QueryGenerator.getInstance().getSelectQuery();
        } else {
            selectQuery = QueryGenerator.getInstance().getSelectQuery(getGraphId(uri));
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
        mapBufferedTriples.put(uri, new GraphStatus());
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        if (mapBufferedTriples.get(uri) == null) {
            LOGGER.info("Try to close Sink for an uri, without open it before. Do nothing.");
            return;
        }
        GraphStatus status = mapBufferedTriples.get(uri);
        status.sendTriples(this, uri);
        CrawlingActivity activity = (CrawlingActivity) uri.getData(Constants.URI_CRAWLING_ACTIVITY);
        if(activity != null) {
            activity.setNumberOfTriples(status.getNumberOfTriples());
            activity.addOutputResource(getGraphId(uri), Squirrel.ResultGraph);
        }
        mapBufferedTriples.remove(uri);
    }

    /**
     * Method to send all buffered triples to the database
     *
     * @param uri
     *            the crawled {@link CrawleableUri}
     * @param tripleList
     *            the list of {@link Triple}s regarding that uri
     */
    private void sendAllTriplesToDB(CrawleableUri uri, Collection<Triple> tripleList) {
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
                LOGGER.error(
                        "Was not able to send the triples to the database (SPARQL), may because the dataset does not exists. Information will get lost :( ["
                                + request + "] on " + updateDatasetURI + " with " + tripleList.size() + " triples]",
                        e);
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
     * @param uri
     *            The given uri.
     * @return The id of the graph.
     */
    public String getGraphId(CrawleableUri uri) {
        return Constants.DEFAULT_RESULT_GRAPH_URI_PREFIX + uri.getData(Constants.UUID_KEY).toString();
    }

    public String getUpdateDatasetURI() {
        return updateDatasetURI;
    }

    protected static class GraphStatus {
        protected List<Triple> buffer = new ArrayList<>(SENDING_INTERVAL_BUFFERED_TRIPLES);
        protected long numberOfTriples = 0;
        
        public synchronized void addTriple(SparqlBasedSink sink, CrawleableUri uri, Triple triple) {
            buffer.add(triple);
            if (buffer.size() >= SENDING_INTERVAL_BUFFERED_TRIPLES) {
                sendTriples(sink, uri);
            }
        }
        
        public void sendTriples(SparqlBasedSink sink, CrawleableUri uri) {
            sink.sendAllTriplesToDB(uri, buffer);
            numberOfTriples += buffer.size();
            buffer.clear();
        }
        
        public long getNumberOfTriples() {
            return numberOfTriples;
        }
    }
}
