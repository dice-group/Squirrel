package org.aksw.simba.squirrel.worker.impl;

/**
 * This class is used to exchange information about objects of {@link org.aksw.simba.squirrel.worker.Worker}
 * over the network.
 */
public class WorkerInfo {

    /**
     * The id of the {@link org.aksw.simba.squirrel.worker.Worker}.
     */
    private int workerId;

    /**
     * Indicates whether the {@link org.aksw.simba.squirrel.worker.Worker} sends objects of {@link AliveMessage}.
     */
    private boolean workerSendsAliveMessages;

    public WorkerInfo(int workerId, boolean workerSendsAliveMessages) {
        this.workerId = workerId;
        this.workerSendsAliveMessages = workerSendsAliveMessages;
    }

    public int getWorkerId() {
        return workerId;
    }

    public boolean workerSendsAliveMessages() {
        return workerSendsAliveMessages;
    }
}
