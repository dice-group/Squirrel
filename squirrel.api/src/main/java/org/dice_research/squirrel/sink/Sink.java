package org.dice_research.squirrel.sink;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.quadbased.QuadBasedSink;
import org.dice_research.squirrel.sink.triplebased.TripleBasedSink;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;

/**
 * The interface of a sink used by a worker. It has to be able to handle
 * both---triples and unstructured data. Therefore, it extends
 * {@link TripleBasedSink} as well as {@link UnstructuredDataSink}.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
public interface Sink extends TripleBasedSink, QuadBasedSink, UnstructuredDataSink, Closeable {

    public default void addMetaData(Model model) {
        CrawleableUri uri = new CrawleableUri(Constants.DEFAULT_META_DATA_GRAPH_URI);
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            addTriple(uri, iterator.next().asTriple());
        }
        flushMetadata();
    }

    @Override
    public default void close() throws IOException {
        closeSinkForUri(new CrawleableUri(Constants.DEFAULT_META_DATA_GRAPH_URI));
    }
    
    public void flushMetadata();
    
}
