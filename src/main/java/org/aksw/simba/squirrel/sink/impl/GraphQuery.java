package org.aksw.simba.squirrel.sink.impl;


import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;

import java.util.ArrayList;
import java.util.List;


public class GraphQuery extends Query {
    public static void main (String [] args){

        String queryString = "SELECT ?subject ?predicate ?object\n" +
            "WHERE {\n" +
            "  GRAPH <http://localhost:3030/lolo/data/"+ args[0] +">\n" + //specific graph should be selected in the dataset
            "  {\n" +
            "  ?subject ?predicate ?object\n" +
            "  }\n" +
            "}";
        //"LIMIT 25"; //for specifying the number of triples for large datasets
        Query query = QueryFactory.create(queryString) ;

        //for debugging the query
        System.out.println(queryString);

        //sparql endpoint for specific dataset
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/lolo/query", query);
        ResultSet results = qexec.execSelect();
        ResultSetFormatter.out(System.out, results, query) ;
        results.getResultVars();


    }

    public static List<Triple> GetAllTriplesFromGraph(String [] args) {
        List<Triple> results = new ArrayList<>();
        String queryString = "SELECT ?subject ?predicate ?object\n" +
            "WHERE {\n" +
            "  GRAPH <http://localhost:3030/lolo/data/" + args[0] + ">\n" + //specific graph should be selected in the dataset
            "  {\n" +
            "  ?subject ?predicate ?object\n" +
            "  }\n" +
            "}";
        //"LIMIT 25"; //for specifying the number of triples for large datasets
        Query query = QueryFactory.create(queryString);

        //for debugging the query
        System.out.println(queryString);

        //sparql endpoint for specific dataset
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/lolo/query", query);
        ResultSet queryResults = qexec.execSelect();
        while (queryResults.hasNext()) {
            QuerySolution qs = queryResults.nextSolution();
            results.add(new Triple(qs.get("?subject").asNode(), qs.get("?predicate").asNode(), qs.get("?object").asNode()));
        }
        qexec.close();

        return results;
    }
}
