package org.aksw.simba.squirrel.frontier.impl;

import org.aksw.simba.squirrel.components.FrontierComponent;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.rabbit.msgs.CrawlingResult;
import org.aksw.simba.squirrel.worker.impl.AliveMessage;
import org.aksw.simba.squirrel.worker.impl.WorkerInfo;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This class checks whether some {@link org.aksw.simba.squirrel.worker.Worker} has died and propagates the
 * information to the {@link FrontierComponent}.
 *
 * @author Philip Frerk
 */
public class WorkerGuard {

    /**
     * A map from {@link org.aksw.simba.squirrel.worker.Worker} id to a timestamp that
     * indicates when the {@link org.aksw.simba.squirrel.worker.Worker} has sent his last {@AliveMessage}.
     */
    private final Map<Integer, Date> mapWorkerTimestamps = new HashMap<>();

    /**
     * A map from {@link WorkerInfo} to a list of {@link CrawleableUri} which contains all URIs that the
     * worker has claimed to crawl, but has not yet sent a {@link CrawlingResult} for.
     */
    private final Map<WorkerInfo, List<CrawleableUri>> mapWorkerUris = new HashMap<>();

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
     * The timer to check for dead or alive workers.
     */
    private final Timer timer = new Timer();


    /**
     * Create an object of this class and provide an instance of {@link FrontierComponent} that it can contact.
     *
     * @param frontierComponent The instance of {@link FrontierComponent}.
     */
    public WorkerGuard(FrontierComponent frontierComponent) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<Integer> lstIdsToBeRemoved = new ArrayList<>();
                for (int idWorker : mapWorkerTimestamps.keySet()) {

                    if (mapWorkerTimestamps.get(idWorker) == null) {
                        continue;
                    }

                    boolean currentWorkerSendsAliveMessages = true;
                    for (WorkerInfo workerInfo : mapWorkerUris.keySet()) {

                        if (workerInfo.getWorkerId() == idWorker && !workerInfo.workerSendsAliveMessages()) {
                            // this worker does not even send aliveMessages, so we do not want to delete
                            // him, because we hope that he is alive as we cannot check it anyway
                            currentWorkerSendsAliveMessages = false;
                            break;
                        }
                    }

                    if (currentWorkerSendsAliveMessages) {
                        long duration = new Date().getTime() - mapWorkerTimestamps.get(idWorker).getTime();
                        if (TimeUnit.MILLISECONDS.toSeconds(duration) > TIME_WORKER_DEAD + 10) {
                            // worker is dead
                            lstIdsToBeRemoved.add(idWorker);
                        }
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
     * Put the given uriDatePairs for the given worker so that he can crawl them.
     *
     * @param idOfWorker The id of the worker for which to put the uriDatePairs.
     * @param lstUris    The uriDatePairs to put.
     */
    public void putUrisForWorker(int idOfWorker, boolean workerSendsAliveMessages, List<CrawleableUri> lstUris) {
        mapWorkerUris.put(new WorkerInfo(idOfWorker, workerSendsAliveMessages), lstUris);
    }

    /**
     * Remove the given uriDatePairs for the given worker.
     *
     * @param idOfWorker      The id of the worker.
     * @param lstUrisToRemove The uriDatePairs to be removed.
     */
    public void removeUrisForWorker(int idOfWorker, List<CrawleableUri> lstUrisToRemove) {
        List<CrawleableUri> lstAllUris = mapWorkerUris.get(idOfWorker);
        if (lstAllUris == null) {
            return;
        }
        lstAllUris.removeAll(lstUrisToRemove);
        mapWorkerUris.remove(idOfWorker, lstAllUris);
    }

    /**
     * Make the Guard stop working.
     */
    public void shutdown() {
        timer.cancel();
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
