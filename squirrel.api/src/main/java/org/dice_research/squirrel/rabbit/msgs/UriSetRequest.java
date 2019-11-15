package org.dice_research.squirrel.rabbit.msgs;

import java.io.Serializable;

/**
 * Simple structure representing the request of a Worker for a set of URIs.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class UriSetRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The id of the {@link org.dice_research.squirrel.worker.Worker} that sent this request.
     */
    private String workerId;

    /**
     * Indicates whether the worker (see {@link #workerId}) sends {@link org.dice_research.squirrel.worker.impl.AliveMessage}.
     */
    private boolean workerSendsAliveMessages;

    /**
     * Standard constructor setting just default values.
     */
    public UriSetRequest() {
        this(null, false);
    }

    /**
     * Constructor.
     *
     * @param workerId
     * @param workerSendsAliveMessages
     */
    public UriSetRequest(String workerId, boolean workerSendsAliveMessages) {
        this.workerId = workerId;
        this.workerSendsAliveMessages = workerSendsAliveMessages;
    }

    public String getWorkerId() {
        return workerId;
    }

    public boolean workerSendsAliveMessages() {
        return workerSendsAliveMessages;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return getClass() == obj.getClass();
    }
}