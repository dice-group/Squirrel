package org.aksw.simba.squirrel.sink.impl;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;


public class OntExample {

    /*public static void uploadRDF(File rdf, String serviceURI)
        throws IOException {


        Model m = ModelFactory.createDefaultModel();

        try (FileInputStream in = new FileInputStream(rdf)) {
            m.read(in, null, "RDF/XML");
        }

        // upload the resulting model
        DatasetAccessor accessor = DatasetAccessorFactory
            .createHTTP(serviceURI);
        accessor.putModel(m);
    }*/

    public static void execSelectAndPrint(String serviceURI, String query) {
        QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI,
            query);
        ResultSet results = q.execSelect();

        ResultSetFormatter.out(System.out, results);

        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            RDFNode x = soln.get("x");
            System.out.println(x);
        }
    }

    public static void execSelectAndProcess(String serviceURI, String query) {
        QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI,
            query);
        ResultSet results = q.execSelect();

        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            // assumes that you have an "?x" in your query
            RDFNode x = soln.get("x");
            System.out.println(x);
        }
    }

    public static void uploadSampleQuery() {
        String update_sample =
            "PREFIX prov: <http://www.w3.org/ns/prov#>"
                + "INSERT DATA"
                + "{ <http://example/%s>    prov:title    \"test book\" ;"
                + "                         prov:creator  \"N.Other\" ." + "}   ";

        String id = UUID.randomUUID().toString();
        System.out.println(String.format("Adding %s", id));
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(UpdateFactory.create(String.format(update_sample, id)), "http://localhost:3030/test2/update");
        upp.execute();
    }

    public static void main(String[] argv) throws IOException {
        //uploadRDF(new File("C:\\Users\\Matthias\\PG\\exampleRDF.rdf"), "http://localhost:3030/test/upload");
        uploadSampleQuery();

        execSelectAndPrint(
            "http://localhost:3030/test2",
            "SELECT ?x WHERE { ?x  ?y  \"test book\" }");

        execSelectAndProcess(
            "http://localhost:3030/test2",
            "SELECT ?x WHERE { ?x  ?y  \"A new book\" }");

    }
}
