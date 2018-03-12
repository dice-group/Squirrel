package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.RDFSink;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrawlingActivity {
    private UUID id;
    private Date dateStarted;
    private Date dateEnded;
    private Map<CrawleableUri, CrawlingURIState> mapUri;
    private CrawlingActivityState status;
    private Worker worker;
    private int numTriples;

    private Sink sink;

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlingActivity.class);

    public int getNumTriples() {
        return numTriples;
    }

    public CrawlingActivity(List<CrawleableUri> listUri, Worker worker, Sink sink) {
        this.worker = worker;
        this.dateStarted = new Date();
        this.status = CrawlingActivityState.STARTED;
        mapUri = new HashedMap();
        for (CrawleableUri uri : listUri) {
            mapUri.put(uri, CrawlingURIState.UNKNOWN);
        }
        id = UUID.randomUUID();
        this.sink = sink;
    }

    public void setState(CrawleableUri uri, CrawlingURIState state) {

    }

    public void finishActivity() {
        countTriples();
        dateEnded = new Date();
        status = CrawlingActivityState.ENDED;

    }

    private void countTriples() {
        int sum = 0;
        if (sink instanceof RDFSink) {
            for (CrawleableUri uri : mapUri.keySet()) {
                sum += ((RDFSink) sink).getNumberOfTriplesForGraph(uri);
            }
            numTriples = sum;
        } else {
            numTriples = -1;
        }
    }

    public UUID getId() {
        return id;
    }

    public String getDateStarted() {
        String dateString = dateStarted.toString();
        dateString = dateString.replace(" ", ";");
        return dateString;
    }

    public String getDateEnded() {

        String dateString = dateEnded.toString();
        dateString = dateString.replace(" ", ";");
        return dateString;
    }

    public CrawlingActivityState getStatus() {
        return status;
    }

    public Worker getWorker() {
        return worker;
    }

    public enum CrawlingURIState {SUCCESSFUL, UNKNOWN, FAILED}

    public enum CrawlingActivityState {STARTED, ENDED, SUCCESSFUL, FAILED}
}
