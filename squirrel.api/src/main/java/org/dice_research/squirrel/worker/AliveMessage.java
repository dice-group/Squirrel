package org.dice_research.squirrel.worker;

import java.io.Serializable;

/** @author Philip Frerk
 * A simple message format for the {@link org.dice_research.squirrel.worker.Worker}
 * to the {@link org.dice_research.squirrel.components.FrontierComponent} that he is still alive.
 * The {@link org.dice_research.squirrel.worker.Worker} identifies himself by the {@link #idOfWorker}.
 */
public class AliveMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The id of the worker that sends the alive message.
     */
    private String workerId;

    /**
     * Create aliveMessage by an id of a worker.
     *
     * @param idOfWorker The id of the worker.
     */
    public AliveMessage(String workerId) {
        this.workerId = workerId;
    }

    /**
     * Get the id of the worker.
     *
     * @return the id of the worker.
     */
    public String getWorkerId() {
        return workerId;
    }

}
