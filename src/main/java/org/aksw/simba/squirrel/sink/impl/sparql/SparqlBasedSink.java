package org.aksw.simba.squirrel.sink.impl.sparql;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.metadata.CrawlingActivity;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Triple;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

public class SparqlBasedSink implements Sink {

    private static String strContentDatasetUriUpdate;
    private static String strContentDatasetUriQuery;
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);
    @SuppressWarnings("unused")
    private static String strMetaDatasetUriQuery;
    @SuppressWarnings("unused")
    private static String strMetaDatasetUriUpdate;
    private static String datasetPrefix;


    public SparqlBasedSink() {
        String strIP = "jena";
        String portVar = "PORT";
        Map<String, String> env = System.getenv();
        String port = "3030";
        datasetPrefix = "http://" + strIP + ":" + port + "/";
        strContentDatasetUriUpdate = datasetPrefix + "ContentSet/update";
        strMetaDatasetUriUpdate = datasetPrefix + "MetaData/update";
        strContentDatasetUriQuery = datasetPrefix + "ContentSet/query";
        strMetaDatasetUriQuery = datasetPrefix + "MetaData/query";

    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(uri, triple, false));
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
