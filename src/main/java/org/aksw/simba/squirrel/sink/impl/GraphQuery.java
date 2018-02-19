package org.aksw.simba.squirrel.sink.impl;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;


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
    }
}
