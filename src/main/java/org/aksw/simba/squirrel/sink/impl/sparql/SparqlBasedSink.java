package org.aksw.simba.squirrel.sink.impl.sparql;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SparqlBasedSink implements Sink {
    /**
     * Interval that specifies how many triples are to be buffered at once until they are sent to the DB.
     */
    private static final int SENDING_INTERVAL_BUFFERED_TRIPLES = 100;
    /**
     * The URI of the DB in which updates can be performed.
     */
    private String updateDatasetURI;
    /**
     * The URI of the DB in which querys can be performed.
     */
    private String queryDatasetURI;
    /**
     * The data structure (map) in which the triples are buffered.
     */
    private ConcurrentHashMap<CrawleableUri, ConcurrentLinkedQueue<Triple>> mapBufferedTriples = new ConcurrentHashMap<>();


    /**
     * Constructor of SparqlBasedSink
     *
     * @param updateDatasetURI The URI of the DB in which updates can be performed.
     * @param queryDatasetURI  The URI of the DB in which querys can be performed.
     */
    public SparqlBasedSink(String updateDatasetURI, String queryDatasetURI) {
        this.updateDatasetURI = updateDatasetURI;
        this.queryDatasetURI = queryDatasetURI;
    }

    public void addMetadata() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        mapBufferedTriples.get(uri).add(triple);

        if (mapBufferedTriples.get(uri).size() >= SENDING_INTERVAL_BUFFERED_TRIPLES) {
            sendAllTriplesToDB(uri, mapBufferedTriples.get(uri));
        }
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        mapBufferedTriples.put(uri, new ConcurrentLinkedQueue<>());
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        if (!mapBufferedTriples.get(uri).isEmpty()) {
            sendAllTriplesToDB(uri, mapBufferedTriples.get(uri));
        }
        mapBufferedTriples.remove(uri);
    }

    /**
     * Method to send all buffered triples to the database
     * @param uri
     * @param tripleList
     */
    private void sendAllTriplesToDB(CrawleableUri uri, ConcurrentLinkedQueue<Triple> tripleList) {
        UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(uri, tripleList));
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, updateDatasetURI);
        proc.execute();
    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {
        throw new UnsupportedOperationException();
    }


    public static void main(String[] args) {
        Triple triple1 = new Triple(NodeFactory.createBlankNode("testSubject1"), NodeFactory.createBlankNode("testPredicate1"), NodeFactory.createBlankNode("testObject1"));
        Triple triple2 = new Triple(NodeFactory.createLiteral("512335testSubject2"), NodeFactory.createLiteral("testPredicate2"), NodeFactory.createLiteral("testObject2"));
        Triple triple3 = new Triple(NodeFactory.createURI("349rf0ejn4f90wj"), NodeFactory.createURI("49f0j4efh"), NodeFactory.createURI("30r9j3f9j"));
        Triple triple4 = new Triple(NodeFactory.createVariable("349rf0ejn4f90wj"), NodeFactory.createVariable("pred49f0j4efh"), NodeFactory.createVariable("30r9j3f9j"));
        List<Triple> listBufferedTriples = new ArrayList<>();
//        listBufferedTriples.add(triple1);
//        listBufferedTriples.add(triple2);
        listBufferedTriples.add(triple4);

        System.out.println(QueryGenerator.formatNodeToString(triple4.getSubject()));


//
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("INSERT DATA { Graph <");
//        stringBuilder.append("sampleUri");
//        stringBuilder.append("> { ");
//        for (Triple triple : listBufferedTriples) {
////            stringBuilder.append("<");
//            stringBuilder.append(triple.getSubject());
////            stringBuilder.append("> <");
//            stringBuilder.append(" ");
//            stringBuilder.append(triple.getPredicate());
////            stringBuilder.append("> <");
//            stringBuilder.append(" ");
//            stringBuilder.append(triple.getObject());
////            stringBuilder.append("> . ");
//            stringBuilder.append(" . ");
//        }
//        stringBuilder.append("} ");
//        stringBuilder.append("}");
//
//        System.out.println(stringBuilder.toString());
//        UpdateRequest request = UpdateFactory.create(stringBuilder.toString());

    }
}
