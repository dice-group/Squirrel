package org.aksw.simba.squirrel.sink.impl.sparql;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is used to provides templates for basic SPARQL commands needed in this project.
 */
public class QueryGenerator {

    /**
     * The instance of the class QueryGenerator.
     */
    private static final QueryGenerator instance = new QueryGenerator();

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryGenerator.class);

    private QueryGenerator() {
    }

    /**
     * Getter for {@link #instance}.
     *
     * @return instannce of the class.
     */
    public static QueryGenerator getInstance() {
        return instance;
    }

    /**
     * Return an Add Query for the given uri and its triples.
     *
     * @param graphId                 the graph id where the triples are stored.
     * @param listBufferedTriples the given list of triples.
     * @return The generated query.
     */
    public String getAddQuery(String graphId, ConcurrentLinkedQueue<Triple> listBufferedTriples) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT DATA { ");
        if (graphId != null) {
            stringBuilder.append("Graph <");
            stringBuilder.append(graphId);
            stringBuilder.append("> { ");
        }
        for (Triple triple : listBufferedTriples) {
            stringBuilder.append(formatNodeToString(triple.getSubject()));
            stringBuilder.append(formatNodeToString(triple.getPredicate()));
            stringBuilder.append(formatNodeToString(triple.getObject()));
            stringBuilder.append(". ");
        }
        if (graphId != null) {
            stringBuilder.append("} ");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    /**
     * Return a select all query for a given uri.
     *
     * @param uri The given uri.
     * @return The generated query.
     */
    @SuppressWarnings("unused")
    public Query getSelectAllQuery(CrawleableUri uri) {
        return getSelectQuery(uri, null, true);
    }

    /**
     * Return a select query for a given uri and triple.
     *
     * @param uri    The given uri.
     * @param triple The given triple.
     * @return The generated query.
     */
    @SuppressWarnings("unused")
    public Query getSelectQuery(CrawleableUri uri, Triple triple) {
        return getSelectQuery(uri, triple, false);
    }

    /**
     * Return a select query for the given uri and triple.
     *
     * @param uri        The given uri.
     * @param triple     The given triple.
     * @param bSelectAll Indicates whether the query should be a select all query or not.
     * @return
     */
    public Query getSelectQuery(CrawleableUri uri, Triple triple, boolean bSelectAll) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ?subject ?predicate ?object WHERE { GRAPH <");
        stringBuilder.append(uri.getUri());
        stringBuilder.append("> { ");
        if (bSelectAll) {
            stringBuilder.append("?subject ?predicate ?object ");
        } else {
            stringBuilder.append(formatNodeToString(triple.getSubject()));
            stringBuilder.append(formatNodeToString(triple.getSubject()));
            stringBuilder.append(formatNodeToString(triple.getSubject()));
//            stringBuilder.append(". ");
        }
        stringBuilder.append("} } ");
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    public static String formatNodeToString(Node node) {
        StringBuilder stringBuilder = new StringBuilder();
        if (node.isURI()) {
            stringBuilder.append("<");
            stringBuilder.append(node.getURI());
            stringBuilder.append(">");
        } else if (node.isBlank()) {
            stringBuilder.append("_:");
            stringBuilder.append(node.getBlankNodeLabel());
        } else if (node.isLiteral()) {
            stringBuilder.append("\"");
            stringBuilder.append(node.getLiteral());
            stringBuilder.append("\"");
            if (node.getLiteralLanguage() != null && !node.getLiteralLanguage().isEmpty()) {
                stringBuilder.append("@");
                stringBuilder.append(node.getLiteralLanguage());
            } else if (node.getLiteralDatatype() != null) {
                stringBuilder.append("^^");
                stringBuilder.append("<");
                stringBuilder.append(node.getLiteralDatatype().getURI());
                stringBuilder.append(">");
            }
        }
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }
}
