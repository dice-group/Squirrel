package org.dice_research.squirrel.sink.impl.sparql;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.sink.triplebased.AdvancedTripleBasedSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A sink which stores the data in different graphs in a sparql based db.
 */
public class TDBSink extends AbstractBufferingSink implements AdvancedTripleBasedSink, Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(TDBSink.class);
    /**
     * The URI to the metadata DB in which updates can be performed.
     */
    private final String updateMetaDataUri;
//    /**
//     * The URI to the metadata DB in which querys can be performed.
//     */
//    private final String queryMetaDataUri;

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
    public TDBSink(String host, String port, String updateAppendix, String queryAppendix,
            String updateMetaDataAppendix, String queryMetaDataAppendix) {
        String prefix = "http://" + host + ":" + port + "/";
        updateDatasetURI = prefix + updateAppendix;
        queryDatasetURI = prefix + queryAppendix;
        updateMetaDataUri = prefix + updateMetaDataAppendix;
//        queryMetaDataUri = prefix + queryMetaDataAppendix;
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

    /**
     * Method to send all buffered triples to the database
     *
     * @param uri
     *            the crawled {@link CrawleableUri}
     * @param tripleList
     *            the list of {@link Triple}s regarding that uri
     */
    @Override
    protected void sendTriples(CrawleableUri uri, Collection<Triple> triples) {
        String stringQuery = null;
        String sparqlEndpoint;
        if (uri.equals(metaDataGraphUri)) {
            stringQuery = QueryGenerator.getInstance().getAddQuery(triples);
            sparqlEndpoint = updateMetaDataUri;
        } else {
            stringQuery = QueryGenerator.getInstance().getAddQuery(getGraphId(uri), triples);
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
                                + request + "] on " + updateDatasetURI + " with " + triples.size() + " triples]",
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

    @Override
    protected void sendQuads(CrawleableUri uri, Collection<Quad> buffer) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void flushMetadata() {
        // TODO Auto-generated method stub
        
    }

}
