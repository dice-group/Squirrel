package org.aksw.simba.squirrel.rabbit.msgs;

import java.io.Serializable;

public class UriSetRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The id of the {@link org.aksw.simba.squirrel.worker.Worker} that sent this request.
     */
    private String idOfWorker;

    /**
     * Indicates whether the worker (see {@link #idOfWorker}) sends {@link org.aksw.simba.squirrel.worker.impl.AliveMessage}.
     */
    private boolean workerSendsAliveMessages;

    /**
     * Standard constructor setting just default values.
     */
    public UriSetRequest() {
        this(null, false);
    }

    /**
     * Parametrized Constructor.
     *
     * @param idOfWorker
     * @param workerSendsAliveMessages
     */
    public UriSetRequest(String idOfWorker, boolean workerSendsAliveMessages) {
        this.idOfWorker = idOfWorker;
        this.workerSendsAliveMessages = workerSendsAliveMessages;
    }

    public String getIdOfWorker() {
        return idOfWorker;
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