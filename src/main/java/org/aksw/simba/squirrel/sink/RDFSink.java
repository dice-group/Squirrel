package org.aksw.simba.squirrel.sink;

import org.aksw.jena_sparql_api.utils.Triples;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.graph.*;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.update.*;

import java.io.InputStream;
import java.util.UUID;

public class RDFSink implements Sink {

    private String databaseDirectory = "http://localhost:3030/";

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        //Dataset dataset= TDBFactory.createDataset(databaseDirectory+uri.getUri().toString());
        System.out.println("URI: " + databaseDirectory + uri.getUri().toString());
        Dataset dataset = DatasetFactory.create(databaseDirectory + uri.getUri().toString());
        Model model = dataset.getDefaultModel();
        dataset.begin(ReadWrite.WRITE);
        try {
            GraphStore graphStore = GraphStoreFactory.create(dataset);
            String sparqlUpdateString = StrUtils.strjoinNL(
                "PREFIX . <http://example/>",
                "INSERT { ?s ?p ?o } WHERE { BIND(triple.getSubject() AS ?s); BIND(triple.getPredicate() AS ?p); BIND(triple.getObject() AS ?o }");

            UpdateRequest request = UpdateFactory.create(sparqlUpdateString);
            UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore);
            proc.execute();

            // Finally, commit the transaction.
            dataset.commit();
        } finally {
            dataset.end();
        }
    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        // throw new UnsupportedOperationException();
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        //throw new UnsupportedOperationException();
    }

    public static void main(String[] argv) {
        RDFSink sink = new RDFSink();
        CrawleableUriFactoryImpl uriFactory = new CrawleableUriFactoryImpl();
        CrawleableUri uri = uriFactory.create("testCreateDataSet");
        Triple t1 = new Triple(NodeFactory.createBlankNode("sub1"), NodeFactory.createBlankNode("pred1"), NodeFactory.createBlankNode("obj1"));
        sink.addTriple(uri, t1);
    }
}
