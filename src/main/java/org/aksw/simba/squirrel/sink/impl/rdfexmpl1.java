package org.aksw.simba.squirrel.sink.impl;

/*import org.apache.jena.query.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.system.Txn;

/** RDF Connection example */
/*public class rdfexmpl1 {
    public static void main(String ...args) {
        Query query = QueryFactory.create("SELECT * { {?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } } }");
        Dataset dataset = DatasetFactory.createTxnMem();
        RDFConnection conn = RDFConnectionFactory.connect(dataset);

        Txn.executeWrite(conn, () ->{
            System.out.println("Load a file");
            conn.load("data.ttl");
            conn.load("http://example/g0", "data.ttl");
            System.out.println("In write transaction");
            conn.queryResultSet(query, ResultSetFormatter::out);
        });
        // And again - implicit READ transaction.
        System.out.println("After write transaction");
        conn.queryResultSet(query, ResultSetFormatter::out);
    }
}*/

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
/*import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import at.jku.dke.hilal.analysis_graphs.DimensionsToAnalysisSituation;
import at.jku.dke.hilal.md_elements.Dimension;
import at.jku.dke.hilal.owl_handler.BasicOWLHandler;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;*/

public class rdfexmpl1 extends Query {
    public static void main (String [] args){

        String queryString = "SELECT ?subject ?predicate ?object\n" +
            "WHERE {\n" +
            "  ?subject ?predicate ?object\n" +
            "}\n" +
            "LIMIT 25";
        Query query = QueryFactory.create(queryString) ;

        System.out.println(queryString);

        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/lolo/sparql", query);
        ResultSet results = qexec.execSelect();
        ResultSetFormatter.out(System.out, results, query) ;
    }
}
