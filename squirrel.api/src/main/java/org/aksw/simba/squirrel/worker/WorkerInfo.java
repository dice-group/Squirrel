package org.aksw.simba.squirrel.worker;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.util.Date;
import java.util.List;

/**
 * This class is used to exchange information about objects of {@link org.aksw.simba.squirrel.worker.Worker}
 * over the network.
 */
public class WorkerInfo {

    /**
     * Indicates whether the {@link org.aksw.simba.squirrel.worker.Worker} sends objects of {@link AliveMessage}.
     */
    private boolean workerSendsAliveMessages;

    /**
     * List contains all uris that the worker is currently crawling.
     */
    private List<CrawleableUri> urisCrawling;

    /**
     * The date of the last {@link AliveMessage} from the {@link org.apache.jena.sparql.sse.ItemWalker.Worker}.
     */
    private Date dateLastAlive;

    public WorkerInfo(boolean workerSendsAliveMessages, List<CrawleableUri> urisCrawling, Date dateLastAlive) {
        this.workerSendsAliveMessages = workerSendsAliveMessages;
        this.urisCrawling = urisCrawling;
        this.dateLastAlive = dateLastAlive;
    }

    public List<CrawleableUri> getUrisCrawling() {
        return urisCrawling;
    }

    public Date getDateLastAlive() {
        return dateLastAlive;
    }

    public boolean workerSendsAliveMessages() {
        return workerSendsAliveMessages;
    }

    public void setDateLastAlive(Date dateLastAlive) {
        this.dateLastAlive = dateLastAlive;
    }
}
