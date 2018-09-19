package org.aksw.simba.squirrel.worker;

import java.io.Serializable;

/** @author Philip Frerk
 * A simple message format for the {@link org.aksw.simba.squirrel.worker.Worker}
 * to the {@link org.aksw.simba.squirrel.components.FrontierComponent} that he is still alive.
 * The {@link org.aksw.simba.squirrel.worker.Worker} identifies himself by the {@link #idOfWorker}.
 */
public class AliveMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The id of the worker that sends the alive message.
     */
    private String idOfWorker;

    /**
     * Create aliveMessage by an id of a worker.
     *
     * @param idOfWorker The id of the worker.
     */
    public AliveMessage(String idOfWorker) {
        this.idOfWorker = idOfWorker;
    }

    /**
     * Get the id of the worker.
     *
     * @return the id of the worker.
     */
    public String getIdOfWorker() {
        return idOfWorker;
    }

}
