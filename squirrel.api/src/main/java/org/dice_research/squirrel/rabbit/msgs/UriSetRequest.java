package org.dice_research.squirrel.rabbit.msgs;

import java.io.Serializable;

public class UriSetRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The id of the {@link org.dice_research.squirrel.worker.Worker} that sent this request.
     */
    private int idOfWorker;

    /**
     * Indicates whether the worker (see {@link #idOfWorker}) sends {@link org.dice_research.squirrel.worker.impl.AliveMessage}.
     */
    private boolean workerSendsAliveMessages;

    /**
     * Standard constructor setting just default values.
     */
    public UriSetRequest() {
        this(0, false);
    }

    /**
     * Parametrized Constructor.
     *
     * @param idOfWorker
     * @param workerSendsAliveMessages
     */
    public UriSetRequest(int idOfWorker, boolean workerSendsAliveMessages) {
        this.idOfWorker = idOfWorker;
        this.workerSendsAliveMessages = workerSendsAliveMessages;
    }

    public int getIdOfWorker() {
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