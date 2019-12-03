package org.dice_research.squirrel;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

public class SparqlQueryGenerator {

    // query to get all the crawled URIs
    public static Query getDomain() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PREFIX sq:  <http://w3id.org/squirrel/vocab#>" +
            "select ?uri WHERE{\n" +
            "    ?s  sq:crawled  ?uri \n" +
            "}");
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }
}

