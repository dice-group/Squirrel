package org.dice_research.squirrel.sink.impl.sparql;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryGenerator {

    /**
     * The instance of the class QueryGenerator.
     */
    private static final QueryGenerator instance = new QueryGenerator();
    public static final String METADATA_GRAPH_ID = "http://w3id.org/squirrel/metadata";
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
     * Return a select query to find the graph id of the crawled uri.
     * @param uriCrawled The crawled uri for which graph id has to be selected.
     * @return select query string.
     */
    public Query getGraphIdQuery(String uriCrawled){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ?subject WHERE { GRAPH ?g {");
        stringBuilder.append("?subject sq:containsDataOf <");
        stringBuilder.append(uriCrawled);
        stringBuilder.append(">} ");
        stringBuilder.append("}");
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    /**
     * Return a select query to find the activity uri of the crawled uri.
     * @param uriCrawled The crawled uri for which activity uri has to be selected.
     * @return select query string.
     */
    public Query getActivityUriQuery(String uriCrawled){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ?subject WHERE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> {");
        stringBuilder.append("?subject sq:crawled <");
        stringBuilder.append(uriCrawled);
        stringBuilder.append(">} ");
        stringBuilder.append("}");
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    /**
     * Return a select query for generated uris from metadata graph for the respective activity uri.
     * @param activityUri The uri of the activity for which generated uris has to be selected.
     * @return select query string.
     */
    public Query getGeneratedUrisQuery(String activityUri){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ?object WHERE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> {");
        stringBuilder.append("<");
        stringBuilder.append(activityUri);
        stringBuilder.append("> prov:value ?object }");
        stringBuilder.append("}");
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
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
