package org.aksw.simba.squirrel.frontier.impl;

import org.aksw.simba.squirrel.components.FrontierComponent;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.rabbit.msgs.CrawlingResult;
import org.aksw.simba.squirrel.worker.impl.AliveMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This class checks whether some {@link org.aksw.simba.squirrel.worker.Worker} has died and propagates the
 * information to the {@link FrontierComponent}.
 */
public class WorkerGuard {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerGuard.class);

    /**
     * A map from {@link org.aksw.simba.squirrel.worker.Worker} id to a timestamp that
     * indicates when the {@link org.aksw.simba.squirrel.worker.Worker} has sent his last {@AliveMessage}.
     */
    private final Map<Integer, Date> mapWorkerTimestamps = new HashMap<>();

    /**
     * A map from {@link org.aksw.simba.squirrel.worker.Worker} id to a list of {@link CrawleableUri} which
     * contains all URIs that the worker has claimed to crawl, but has not yet sent a {@link CrawlingResult} for.
     */
    private final Map<Integer, List<CrawleableUri>> mapWorkerUris = new HashMap<>();

    /**
     * After this period of time (in seconds), a worker is considered to be dead if he has not sent
     * an {@link AliveMessage} since.
     */
    public final static long TIME_WORKER_DEAD = 10;


    public WorkerGuard(FrontierComponent frontierComponent) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                List<Integer> lstIdsToBeRemoved = new ArrayList<>();
                for (int id : mapWorkerTimestamps.keySet()) {
                    if (mapWorkerTimestamps.get(id) == null) {
                        continue;
                    }
                    long duration = new Date().getTime() - mapWorkerTimestamps.get(id).getTime();
                    if (TimeUnit.MILLISECONDS.toSeconds(duration) > TIME_WORKER_DEAD + 100) {
                        // worker is dead
                        lstIdsToBeRemoved.add(id);
                    }
                }

                //LOGGER.info("map: " +mapWorkerUris.toString());
                synchronized (this) {
                    lstIdsToBeRemoved.forEach(id -> {
                        mapWorkerTimestamps.remove(id);
                        frontierComponent.informFrontierAboutDeadWorker(id, mapWorkerUris.get(id));
                        mapWorkerUris.remove(id);
                    });

                }
            }
        }, 0, TimeUnit.SECONDS.toMillis(TIME_WORKER_DEAD) / 2);
    }

    public void putIntoTimestamps(int idOfWorker) {
        //LOGGER.info("received alive from " +idOfWorker);
        mapWorkerTimestamps.put(idOfWorker, new Date());
    }

    public void putUrisForWorker(int idOfWorker, List<CrawleableUri> lstUris) {
        mapWorkerUris.put(idOfWorker, lstUris);
    }

    public void removeUrisForWorker(int idOfWorker, List<CrawleableUri> lstUrisToRemove) {
        List<CrawleableUri> lstAllUris = mapWorkerUris.get(idOfWorker);
        lstAllUris.removeAll(lstUrisToRemove);
        mapWorkerUris.remove(idOfWorker, lstAllUris);
    }
}
