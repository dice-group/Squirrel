// Written in 2015 by Thilo Planz
// To the extent possible under law, I have dedicated all copyright and related and neighboring rights
// to this software to the public domain worldwide. This software is distributed without any warranty.
// http://creativecommons.org/publicdomain/zero/1.0/

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class FusekiExample {

	public static void uploadRDF(File rdf, String serviceURI)
			throws IOException {

		// parse the file
		Model m = ModelFactory.createDefaultModel();
		try (FileInputStream in = new FileInputStream(rdf)) {
			m.read(in, null, "RDF/XML");
		}

		// upload the resulting model
		DatasetAccessor accessor = DatasetAccessorFactory
				.createHTTP(serviceURI);
		accessor.putModel(m);
	}

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

	public static void main(String[] argv) throws IOException {
		// uploadRDF(new File("test.rdf"), );
		execSelectAndPrint(
				"http://localhost:3030/test",
				"SELECT ?x WHERE { ?x  <http://www.w3.org/2001/vcard-rdf/3.0#FN>  \"John Smith\" }");

		execSelectAndProcess(
				"http://localhost:3030/test",
				"SELECT ?x WHERE { ?x  <http://www.w3.org/2001/vcard-rdf/3.0#FN>  \"John Smith\" }");
	}
}
