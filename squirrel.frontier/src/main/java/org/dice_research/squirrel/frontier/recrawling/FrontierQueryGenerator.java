package org.dice_research.squirrel.frontier.recrawling;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontierQueryGenerator {

    private static final FrontierQueryGenerator instance = new FrontierQueryGenerator();

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierQueryGenerator.class);

    private FrontierQueryGenerator() {
    }

    /**
     * Getter for {@link #instance}.
     */
    public static FrontierQueryGenerator getInstance() {
        return instance;
    }


    /**
     * Return a time stamp query for the default graph.
     * It will return triples with time stamp contained in the default graph.
     *
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


}
