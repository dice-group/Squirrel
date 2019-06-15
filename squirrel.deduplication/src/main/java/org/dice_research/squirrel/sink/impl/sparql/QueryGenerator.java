package org.dice_research.squirrel.sink.impl.sparql;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryGenerator {

    /**
     * The instance of the class QueryGenerator.
     */
    private static final QueryGenerator instance = new QueryGenerator();
    public static final String METADATA_GRAPH_ID = "http://w3id.org/squirrel/metadata";
    public static final String COLUMN_PREDICATE_ID = "sq:hashvalue";

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryGenerator.class);

    private QueryGenerator() {
    }

    private String getPrefixes(){
        StringBuilder sb = new StringBuilder();
        sb.append("PREFIX sq-s:  <http://w3id.org/squirrel/status#>\n");
        sb.append("PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n");
        sb.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
        sb.append("PREFIX sq-a:  <http://w3id.org/squirrel/activity#>\n");
        sb.append("PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n");
        sb.append("PREFIX sq-m:  <http://w3id.org/squirrel/metadata>\n");
        sb.append("PREFIX dcat:  <http://www.w3.org/ns/dcat#>\n");
        sb.append("PREFIX prov:  <http://www.w3.org/ns/prov#>\n");
        sb.append("PREFIX sq-g:  <http://w3id.org/squirrel/graph#>\n");
        sb.append("PREFIX sq-w:  <http://w3id.org/squirrel/worker#>\n");
        sb.append("PREFIX sq:  <http://w3id.org/squirrel/vocab#>\n");
        sb.append("PREFIX dc:  <http://purl.org/dc/terms/>\n");
        return sb.toString();
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
    public Query getTriplesGraphIdQuery(String uriCrawled){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefixes());
        stringBuilder.append("SELECT ?subject WHERE { GRAPH ?g {");
        stringBuilder.append("?subject sq:containsDataOf <");
        stringBuilder.append(uriCrawled);
        stringBuilder.append(">} ");
        stringBuilder.append("}");
        LOGGER.info("Dedup_Testing: query getTriplesGraphIdQuery : " + stringBuilder.toString());
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
        stringBuilder.append(getPrefixes());
        stringBuilder.append("SELECT ?subject WHERE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> {");
        stringBuilder.append("?subject sq:crawled <");
        stringBuilder.append(uriCrawled);
        stringBuilder.append(">} ");
        stringBuilder.append("}");
        LOGGER.info("Dedup_Testing: query getActivityUriQuery: " + stringBuilder.toString());
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
        stringBuilder.append(getPrefixes());
        stringBuilder.append("SELECT ?object WHERE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> {");
        stringBuilder.append("<");
        stringBuilder.append(activityUri+"_generatedURIs");
        stringBuilder.append("> prov:value ?object }");
        stringBuilder.append("}");
        LOGGER.info("Dedup_Testing: query getGeneratedUrisQuery: " + stringBuilder.toString());
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    /**
     * Return a select query for generated Hash values from metadata graph for the respective activity uri.
     * @param hashValue The uri of the activity for which generated uris has to be selected.
     * @return select query string.
     */
    public Query getHashQuery(String hashValue){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefixes());
        stringBuilder.append("SELECT ?subject WHERE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> {");
        stringBuilder.append("?subject ");
        stringBuilder.append(COLUMN_PREDICATE_ID);
        stringBuilder.append(" ");
        stringBuilder.append(hashValue);
        stringBuilder.append("}}");
        LOGGER.info("Dedup_Testing: query getHashQuery: " + stringBuilder.toString());
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
        stringBuilder.append(getPrefixes());
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
        LOGGER.info("Dedup_Testing: query getSelectQuery: " + stringBuilder.toString());
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    public Query getDeleteQuery(String graphID) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefixes());
        stringBuilder.append("DELETE WHERE {");
        stringBuilder.append("GRAPH <");
        stringBuilder.append(graphID);
        stringBuilder.append("> { ");
        stringBuilder.append("?subject ?predicate ?object ");
        stringBuilder.append("}}");
        LOGGER.info("Dedup_Testing: query getDeleteQuery: " + stringBuilder.toString());
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    public Query getUpdateTriplesGraphIdQuery(String newUri, RDFNode oldGraphId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefixes());
        stringBuilder.append("DELETE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> { ?subject sq:containsDataOf <");
        stringBuilder.append(newUri);
        stringBuilder.append("> }}");
        stringBuilder.append("INSERT { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> {");
        stringBuilder.append(oldGraphId.toString());
		stringBuilder.append(" sq:containsDataOf <");
		stringBuilder.append(newUri);
		stringBuilder.append("> } }");
		stringBuilder.append(" WHERE { GRAPH <");
		stringBuilder.append(METADATA_GRAPH_ID);
		stringBuilder.append("> { ?subject ?object ?predicate } }");
        LOGGER.info("Dedup_Testing: query getUpdateTriplesGraphIdQuery: " + stringBuilder.toString());
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
            stringBuilder.append(node.getURI().replace(" ", ""));
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