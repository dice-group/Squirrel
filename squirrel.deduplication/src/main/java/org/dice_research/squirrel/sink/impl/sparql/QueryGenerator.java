package org.dice_research.squirrel.sink.impl.sparql;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.squirrel.vocab.PROV_O;
import org.dice_research.squirrel.vocab.Squirrel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains static methods that generates SPARQL queries.
 */
public class QueryGenerator {

    public static final String METADATA_GRAPH_ID = "http://w3id.org/squirrel/metadata";

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryGenerator.class);

    private static String getPrefixes() {
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
     * Return a select query to find the graph id of the crawled uri.
     *
     * @param uriCrawled The crawled uri for which graph id has to be selected.
     * @return select query string.
     */
    public static Query getTriplesGraphIdQuery(String uriCrawled) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefixes());
        stringBuilder.append("SELECT ?subject WHERE { GRAPH ?g {");
        stringBuilder.append("?subject "+Squirrel.containsDataOf +" <");
        stringBuilder.append(uriCrawled);
        stringBuilder.append(">} ");
        stringBuilder.append("}");
        LOGGER.info("Dedup_Testing: query getTriplesGraphIdQuery : " + stringBuilder.toString());
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    /**
     * Return a select query to find the activity uri of the crawled uri.
     *
     * @param uriCrawled The crawled uri for which activity uri has to be selected.
     * @return select query string.
     */
    public static Query getActivityUriQuery(String uriCrawled) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefixes());
        stringBuilder.append("SELECT ?subject WHERE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> {");
        stringBuilder.append("?subject "+ Squirrel.crawled +" <");
        stringBuilder.append(uriCrawled);
        stringBuilder.append(">} ");
        stringBuilder.append("}");
        LOGGER.info("Dedup_Testing: query getActivityUriQuery: " + stringBuilder.toString());
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    /**
     * Return a select query for generated uris from metadata graph for the respective activity uri.
     *
     * @param activityUri The uri of the activity for which generated uris has to be selected.
     * @return select query string.
     */
    public static Query getGeneratedUrisQuery(String activityUri){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefixes());
        stringBuilder.append("SELECT ?object WHERE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> {");
        stringBuilder.append("?subject ");
        stringBuilder.append(PROV_O.value + " ?object ; ");
        stringBuilder.append(RDF.type + " " + Squirrel.generatedURIs + "; ");
        stringBuilder.append(PROV_O.wasGeneratedBy + " " + activityUri);
        stringBuilder.append("} }");
        LOGGER.info("Dedup_Testing: query getGeneratedUrisQuery: " + stringBuilder.toString());
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    /**
     * Return a select query for generated Hash values from metadata graph for the respective activity uri.
     * The query assumes that the hashvalues are stored in the form of RDF literal
     * @param hashValue The uri of the activity for which generated uris has to be selected.
     * @return select query string.
     */
    public static Query getHashQuery(String hashValue) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefixes());
        stringBuilder.append("SELECT ?subject WHERE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> {");
        stringBuilder.append("?subject ");
        stringBuilder.append(Squirrel.hashValue);
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
    public static Query getTriplesFromGraphUriQuery(String graphID, boolean defaultGraph) {
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
        LOGGER.info("Dedup_Testing: query getTriplesFromGraphUriQuery: " + stringBuilder.toString());
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    /**
     * Query to delete the triples from the given graphUri
     * @param graphID
     * @return the query
     */
    public static Query getDeleteTriplesFromGraphQuery(String graphID) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefixes());
        stringBuilder.append("DELETE WHERE {");
        stringBuilder.append("GRAPH <");
        stringBuilder.append(graphID);
        stringBuilder.append("> { ");
        stringBuilder.append("?subject ?predicate ?object ");
        stringBuilder.append("}}");
        LOGGER.info("Dedup_Testing: query getDeleteTriplesFromGraphQuery: " + stringBuilder.toString());
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    /**
     * This method returns a query to delete the graphUri of the duplicate uri (newUri)
     * and update its graphUri with the already present ones (oldGraphId)
     * @param newUri duplicate uri
     * @param oldGraphId graph uri of the already present old uri
     * @return query
     */
    public static Query getUpdateTriplesGraphIdQuery(String newUri, RDFNode oldGraphId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefixes());
        stringBuilder.append("DELETE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> { ?subject "+ Squirrel.containsDataOf  +" <");
        stringBuilder.append(newUri);
        stringBuilder.append("> }}");
        stringBuilder.append("INSERT { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> {");
        stringBuilder.append(oldGraphId.toString());
        stringBuilder.append(" "+ Squirrel.containsDataOf +" <");
        stringBuilder.append(newUri);
        stringBuilder.append("> } }");
        stringBuilder.append(" WHERE { GRAPH <");
        stringBuilder.append(METADATA_GRAPH_ID);
        stringBuilder.append("> { ?subject ");
        stringBuilder.append(Squirrel.containsDataOf + " <");
        stringBuilder.append(newUri);
        stringBuilder.append("> } }");
        LOGGER.info("Dedup_Testing: query getUpdateTriplesGraphIdQuery: " + stringBuilder.toString());
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }
}
