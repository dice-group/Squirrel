package org.dice_research.squirrel;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.dice_research.squirrel.frontier.recrawling.FrontierQueryGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlQueryGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierQueryGenerator.class);


    public static Query getDomain() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>" +
            "select ?prop ?domain WHERE{\n" +
            "    ?prop rdfs:domain ?domain\n" +
            "}");
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }
}


   /* PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    PREFIX owl: <http://www.w3.org/2002/07/owl#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX dbo: <http://dbpedia.org/ontology/>

    select distinct ?s
    where {
    {
    ?s rdfs:domain dbo:Person .
    }
    union
    {
    ?s rdfs:range dbo:Person .
    }
    union
    {
    ?s rdfs:subClassOf dbo:Person .
    }
    }
    */
