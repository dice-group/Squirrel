package org.aksw.simba.squirrel.worker.impl;

import java.io.Serializable;

public class AliveMessage implements Serializable {
    private int idOfWorker;

    public int getIdOfWorker() {
        return idOfWorker;
    }

    public AliveMessage(int idOfWorker) {
        this.idOfWorker = idOfWorker;
    }
}
