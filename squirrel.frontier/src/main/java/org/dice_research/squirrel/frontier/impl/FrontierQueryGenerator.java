package org.dice_research.squirrel.frontier.impl;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

public class FrontierQueryGenerator {
	   /**
     * The instance of the class QueryGenerator.
     */
    private static final FrontierQueryGenerator instance = new FrontierQueryGenerator();
    String PREFIX= "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierQueryGenerator.class);

    private FrontierQueryGenerator() {
    }

    /**
     * Getter for {@link #instance}.
     *
     * @return instannce of the class.
     */
    public static FrontierQueryGenerator getInstance() {
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
     * Return a time stamp query for the default graph.
     * It will return triples with time stamp contained in the default graph.
     * @return All triples with time stamp in the default graph.
     */
  
    public Query getOutdatedUrisQuery() {
        return getOutdatedUrisQuery(null, true);
    }
    public Query getOutdatedUrisQuery(String graphID, boolean defaultGraph) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PREFIX  sq:   <http://w3id.org/squirrel/vocab#>\n" + 
        		"PREFIX  prov: <http://www.w3.org/ns/prov#>\n" + 
        		"PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>"
        		+ "SELECT ?uri  WHERE { \n ");
        // + "SELECT ?uri  WHERE { \n ");
        if (!defaultGraph) {
            stringBuilder.append("GRAPH <");
            stringBuilder.append(graphID);
            stringBuilder.append("> { ");
        }
        stringBuilder.append("{\n" + 
        		"SELECT ?uri ?endtime (NOW() - (?endtime) AS ?diff)\n" + 
        		"WHERE{\n" + 
        		"\n" + 
        		"  {\n" + 
        		"    SELECT  ?uri  (MAX(?timestamp) as ?endtime)\n" + 
        		"    WHERE\n" + 
        		"    { \n" + 
        		"        ?s  sq:crawled  ?uri ;\n" + 
        		"        prov:endedAtTime  ?timestamp.\n" + 
        		"\n" + 
        		"    }\n" + 
        		"    GROUP BY ?uri\n" + 
        		"  } \n" + 
        		"}\n" + 
        		"}\n" + 
        		"FILTER(?diff > \"18000\"^^xsd:double)\n" +
        		"");
        if (!defaultGraph) {
            stringBuilder.append("}");
        }
       
       // stringBuilder.append("}GROUP BY ?uri");
          stringBuilder.append("}");
       
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

    public Query getSelectQuery() {
        return getSelectQuery(null, true);
    }
    /**
     * Return a select query for the given graphID or default graph.
     * It will return all triples contained in the graph.
     * @return All triples contained in the default graph.
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
