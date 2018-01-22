package org.aksw.simba.squirrel.frontier.impl;

import org.aksw.simba.squirrel.components.FrontierComponent;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.rabbit.msgs.CrawlingResult;
import org.aksw.simba.squirrel.worker.impl.AliveMessage;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Philip Frerk
 * This class checks whether some {@link org.aksw.simba.squirrel.worker.Worker} has died and propagates the
 * information to the {@link FrontierComponent}.
 */
public class WorkerGuard {

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


    /**
     * Counting the number of workers that already died.
     */
    private int numberOfDeadWorkers = 0;


    /**
     * Create an object of this class and provide an instance of {@link FrontierComponent} that it can contact.
     *
     * @param frontierComponent The instance of {@link FrontierComponent}.
     */
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
                    if (TimeUnit.MILLISECONDS.toSeconds(duration) > TIME_WORKER_DEAD + 10) {
                        // worker is dead
                        lstIdsToBeRemoved.add(id);
                    }
                }

                synchronized (this) {
                    lstIdsToBeRemoved.forEach(id -> {
                        mapWorkerTimestamps.remove(id);
                        frontierComponent.informFrontierAboutDeadWorker(id, mapWorkerUris.get(id));
                        mapWorkerUris.remove(id);
                        numberOfDeadWorkers++;
                    });

                }
            }
        }, 0, TimeUnit.SECONDS.toMillis(TIME_WORKER_DEAD) / 2);
    }

    /**
     * Put a new date for the worker identified by the given id.
     *
     * @param idOfWorker the given id.
     */
    public void putIntoTimestamps(int idOfWorker) {
        mapWorkerTimestamps.put(idOfWorker, new Date());
    }

    /**
     * Put the given uris for the given worker so that he can crawl them.
     *
     * @param idOfWorker The id of the worker for which to put the uris.
     * @param lstUris    The uris to put.
     */
    public void putUrisForWorker(int idOfWorker, List<CrawleableUri> lstUris) {
        mapWorkerUris.put(idOfWorker, lstUris);
    }

    /**
     * Remove the given uris for the given worker.
     *
     * @param idOfWorker      The id of the worker.
     * @param lstUrisToRemove The uris to be removed.
     */
    public void removeUrisForWorker(int idOfWorker, List<CrawleableUri> lstUrisToRemove) {
        List<CrawleableUri> lstAllUris = mapWorkerUris.get(idOfWorker);
        lstAllUris.removeAll(lstUrisToRemove);
        mapWorkerUris.remove(idOfWorker, lstAllUris);
    }

    /**
     * Getter for the number of running workers.
     *
     * @return the number of running workers.
     */
    public int getNumberOfLiveWorkers() {
        return mapWorkerTimestamps.size();
    }

    /**
     * Getter for {@link #mapWorkerTimestamps}.
     *
     * @return {@link #mapWorkerTimestamps}.
     */
    public Map<Integer, Date> getMapTimestamps() {
        return mapWorkerTimestamps;
    }

    /**
     * Getter for {@link #numberOfDeadWorkers}.
     *
     * @return {@link #numberOfDeadWorkers}..
     */
    public int getNumberOfDeadWorker() {
        return numberOfDeadWorkers;
    }
}
