package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.worker.Worker;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CrawlingActivity {
    private int id;
    private Date dateStarted;
    private Date dateEnded;
    private Map<CrawleableUri, CrawlingURIState> mapUri;
    private CrawlingActivityState status;
    private Worker worker;

    public CrawlingActivity(List<CrawleableUri> listUri, Worker worker) {
        this.worker = worker;
        this.dateStarted = new Date();
        this.status = CrawlingActivityState.STARTED;
        for (CrawleableUri uri : listUri) {
            mapUri.put(uri, CrawlingURIState.UNKNOWN);
        }
        id = (int) Math.floor(Math.random() * 100000);

    }

    public void setState(CrawleableUri uri, CrawlingURIState state) {

    }

    public void finishActivity() {
        dateEnded = new Date();
        status = CrawlingActivityState.ENDED;
    }

    public int getId() {
        return id;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public Date getDateEnded() {
        return dateEnded;
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
