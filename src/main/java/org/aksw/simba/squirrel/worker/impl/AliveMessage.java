package org.aksw.simba.squirrel.worker.impl;

import java.io.Serializable;

/**
 * A simple message format for the {@link org.aksw.simba.squirrel.worker.Worker}
 * to the {@link org.aksw.simba.squirrel.components.FrontierComponent} that he is still alive.
 * The {@link org.aksw.simba.squirrel.worker.Worker} identifies himself by the {@link #idOfWorker}.
 */
public class AliveMessage implements Serializable {

    private int idOfWorker;

    public AliveMessage(int idOfWorker) {
        this.idOfWorker = idOfWorker;
    }

    public int getIdOfWorker() {
        return idOfWorker;
    }


}
