package org.dice_research.squirrel.frontier.recrawling;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FrontierQueryGenerator {
    /**
     * Return outdated uris by comparing their endtime stamps.
     * @return All triples with time stamp in the default graph.
     */

    public static Query getOutdatedUrisQuery(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Query query = QueryFactory.create("PREFIX  sq:   <http://w3id.org/squirrel/vocab#>\n" +
            "PREFIX  prov: <http://www.w3.org/ns/prov#>\n" +
            "PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>\n" +
            "\n" +
            "SELECT ?url \n" +
            "WHERE{\n" +
            "{\n" +
            "SELECT ?url ?endtime\n" +
            "WHERE{\n" +
            "\n" +
            "  {\n" +
            "    SELECT  ?url  (MAX(?timestamp) as ?endtime)\n" +
            "    WHERE\n" +
            "    { \n" +
            "        ?s  sq:crawled  ?url ;\n" +
            "        prov:endedAtTime  ?timestamp.\n" +
            "\n" +
            "    }\n" +
            "    GROUP BY ?url\n" +
            "  } \n" +
            "}\n" +
            "}\n" +
            "FILTER(?endtime < \"" + dateFormat.format(date.getTime()) + "\"^^xsd:dateTime)\n" +
            "}");

        return query;
    }


}
