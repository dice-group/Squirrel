package org.aksw.simba.squirrel.sink;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

import java.io.InputStream;

public class RDFSink implements Sink {

    private static final String strContentDatasetUriUpdate = "http://localhost:3030/ContentSet/update";

    public static void main(String[] argv) {
        RDFSink sink = new RDFSink();
        //CrawleableUri uri=  new CrawleableUriFactoryImpl().create("http://www.testPage.de");
        CrawleableUri uri = new CrawleableUriFactoryImpl().create("http://www.google2.de");
        Node node = new Node_Variable("subj1");
        Node node2 = new Node_Variable("pred1");
        Node node3 = new Node_Variable("obj1");
        Triple triple1 = new Triple(node, node2, node3);

        sink.addTriple(uri, triple1);

    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        //Get the graphID for the uri - may change to Hashvalue
        //String graphUri = uri.toString();

        //May check if triple already exists
        //e.g. with select query (Limit=1)


        UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(uri, triple));
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, strContentDatasetUriUpdate);
        proc.execute();

    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {

    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {

    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {

    }
}
