package org.aksw.simba.squirrel.sink.impl.sparql;

import java.util.Collection;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to provides querys for basic SPARQL commands needed in this project.
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
     * Return an Add Query for the default uri and its triples.
     *
     * @param listBufferedTriples the given list of triples.
     * @return The generated query.
     */
    public String getAddQuery(Collection<Triple> listBufferedTriples) {
        return getAddQuery(null, listBufferedTriples, true);
    }

    /**
     * Return an Add Query for the given uri and its triples.
     *
     * @param graphId             the graph id where the triples are stored.
     * @param listBufferedTriples the given list of triples.
     * @return The generated query.
     */
    public String getAddQuery(String graphId, Collection<Triple> listBufferedTriples) {
        return getAddQuery(graphId, listBufferedTriples, false);
    }

    /**
     * Return an Add Query for the given uri or default graph and its triples.
     *
     * @param graphId                 the graph id where the triples are stored.
     * @param listBufferedTriples the given list of triples.
     * @param defaultGraph Identify if query is for the default graph.
     * @return The generated query.
     */
    public String getAddQuery(String graphId, Collection<Triple> listBufferedTriples, boolean defaultGraph) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT DATA { ");
        if (!defaultGraph) {
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
        if (!defaultGraph) {
            stringBuilder.append("} ");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    /**
     * Return a select query for the default graph.
     * It will return all triples contained in the default graph.
     * @return All triples contained in the default graph.
     */
    public Query getSelectQuery() {
        return getSelectQuery(null, true);
    }

    /**
     * Return a select query for the given graphID or default graph.
     * It will return all triples contained in the graph.
     *
     * @param graphID      The id of the graph from which you want to select.
     * @param defaultGraph Identify if query is for the default graph
     * @return All triples contained in the graph.
     */
    public Query getSelectQuery(String graphID, boolean defaultGraph) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ?subject ?predicate ?object WHERE { ");
        if (!defaultGraph) {
            stringBuilder.append("GRAPH <");
            stringBuilder.append(graphID);
            stringBuilder.append("> { ");
        }
        stringBuilder.append("?subject ?predicate ?object ");
        if (!defaultGraph) {
            stringBuilder.append("} ");
        }
        stringBuilder.append("}");
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    /**
     * Return a select query for the given graphID.
     * It will return all triples contained in the graph.
     * @param graphID The id of the graph from which you want to select.
     * @return All triples contained in the graph.
     */
    public Query getSelectQuery(String graphID) {
        return getSelectQuery(graphID, false);
    }

    /**
     * Formats the node for a query
     *
     * @param node The node which should formated
     * @return a robust representation of the node
     * <p>
     * Note: Should be updated in relation to the robustness of parsing.
     */
    public static String formatNodeToString(Node node) {
        StringBuilder stringBuilder = new StringBuilder();
        if (node.isURI()) {
            stringBuilder.append("<");
            //Should possibly be further improved
            stringBuilder.append(node.getURI().replace(" ",""));
            stringBuilder.append(">");
        } else if (node.isBlank()) {
            stringBuilder.append("_:");
            //Should possibly be further improved
            String label = node.getBlankNodeId().getLabelString().replace(":", "");
            if (label.startsWith("-")) {
                label = label.substring(1);
            }
            stringBuilder.append(label);
        } else if (node.isLiteral()) {
            stringBuilder.append("\"");
            //Should possibly be further improved
            stringBuilder.append(node.getLiteral().getLexicalForm().replace("\n", "").replace("\"", "'").replace("\r", ""));
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
