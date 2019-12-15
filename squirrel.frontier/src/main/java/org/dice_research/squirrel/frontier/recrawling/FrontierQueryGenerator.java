package org.dice_research.squirrel.frontier.recrawling;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontierQueryGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierQueryGenerator.class);
    public static final long DEFAULT_GENERAL_RECRAWL_TIME = 1000 * 60 * 60 * 24 * 7;


    private FrontierQueryGenerator() {
    }

    /**
     * Return outdated uris by comparing their endtime stamps.
     * @return All triples with time stamp in the default graph.
     */



    public static Query getOutdatedUrisQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PREFIX  sq:   <http://w3id.org/squirrel/vocab#>\n" +
            "PREFIX  prov: <http://www.w3.org/ns/prov#>\n" +
            "PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>"
            + "SELECT ?uri  WHERE { \n "+
            "{\n" +
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
            "FILTER(?diff >"+DEFAULT_GENERAL_RECRAWL_TIME +
            ")}\n" +
            "");

        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }


}
