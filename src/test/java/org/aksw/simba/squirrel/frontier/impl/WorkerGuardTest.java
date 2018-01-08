package org.aksw.simba.squirrel.frontier.impl;

import org.aksw.simba.squirrel.components.FrontierComponent;
import org.aksw.simba.squirrel.components.WorkerComponent;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.queue.RDBQueue;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WorkerGuardTest {


    FrontierImpl frontier;
    RDBQueue queue;
    RDBKnownUriFilter filter;
    WorkerComponent workerComponent;
    FrontierComponent frontierComponent;
    Worker worker;
    Sink sink;

    @Before
    public void setUp() throws Exception {
        filter = new RDBKnownUriFilter("localhost", 28015);
        queue = new RDBQueue("localhost", 28015);

        filter.purge();
        queue.purge();
        frontier = new FrontierImpl(filter, queue);
        workerComponent = new WorkerComponent();
        frontierComponent = new FrontierComponent();
        frontierComponent.setFrontier(frontier);

        //worker = new WorkerImpl(this, sink, new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))),
        //  serializer, collector, 2000, outputFolder + File.separator + "log");
        workerComponent.setWorker(worker);
    }

    @Test
    public void testAliveMechanism() {
        try {
            workerComponent.init();
            frontierComponent.init();
            workerComponent.run();
            frontierComponent.run();
            int workerId = worker.getId();
            Thread.sleep(WorkerGuard.TIME_WORKER_DEAD / 2 + 10);
            boolean idContained = frontierComponent.getWorkerGuard().getMapTimestamps().containsKey(workerId);
            ((WorkerImpl) worker).setTerminateFlag(true);


            Thread.sleep(WorkerGuard.TIME_WORKER_DEAD + 10);
            boolean idStillContained = frontierComponent.getWorkerGuard().getMapTimestamps().containsKey(workerId);

            Assert.assertTrue(idContained && !idStillContained);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
