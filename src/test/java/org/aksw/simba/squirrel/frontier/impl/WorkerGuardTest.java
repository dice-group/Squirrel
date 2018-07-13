package org.aksw.simba.squirrel.frontier.impl;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import org.aksw.simba.squirrel.collect.SqlBasedUriCollector;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.components.FrontierComponent;
import org.aksw.simba.squirrel.components.WorkerComponent;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.queue.RDBQueue;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class WorkerGuardTest {


    FrontierImpl frontier;
    RDBKnownUriFilter filter;
    WorkerComponent workerComponent;
    FrontierComponent frontierComponent;
    Worker worker;
    Sink sink;
    RDBQueue queue;


    @Before
    public void setUp() {


        filter = new RDBKnownUriFilter("localhost", 28015);
        filter.open();
        queue = new RDBQueue("localhost", 28015);
        queue.open();
        filter.purge();
        queue.purge();
        frontier = new FrontierImpl(filter, queue);
        workerComponent = new WorkerComponent();
        frontierComponent = new FrontierComponent();
        frontierComponent.setFrontier(frontier);
        String outputFolder = "output";

        Serializer serializer = new GzipJavaUriSerializer();
        Sink sink = new FileBasedSink(new File(outputFolder), true);
        UriCollector collector = SqlBasedUriCollector.create(serializer);
        worker = new WorkerImpl(null, sink, new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))),
            serializer, collector, 2000, outputFolder + File.separator + "log", true);

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
            boolean idContained = frontierComponent.getWorkerGuard().getMapWorkerInfo().containsKey(workerId);
            ((WorkerImpl) worker).setTerminateFlag(true);


            Thread.sleep(WorkerGuard.TIME_WORKER_DEAD + 10);
            boolean idStillContained = frontierComponent.getWorkerGuard().getMapWorkerInfo().containsKey(workerId);

            Assert.assertTrue(idContained && !idStillContained);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        String rethinkDockerStopCommand = "docker stop squirrel-test-rethinkdb";
        Process p = Runtime.getRuntime().exec(rethinkDockerStopCommand);
        p.waitFor();
        String rethinkDockerRmCommand = "docker rm squirrel-test-rethinkdb";
        p = Runtime.getRuntime().exec(rethinkDockerRmCommand);
        p.waitFor();
    }
}
