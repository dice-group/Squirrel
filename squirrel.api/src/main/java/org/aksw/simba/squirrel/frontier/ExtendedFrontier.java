package org.aksw.simba.squirrel.frontier;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.util.List;

public interface ExtendedFrontier extends Frontier {

    /**
     * The frontier gets the information that some worker has died and he has to react somehow.
     *
     * @param idOfWorker        The id of the dead {@link org.aksw.simba.squirrel.worker.Worker}.
     * @param lstUrisToReassign A list of {@link CrawleableUri} that should have been handeled by the
     *                          dead worker, but was not due to his sudden death.
     */
    void informAboutDeadWorker(String idOfWorker, List<CrawleableUri> lstUrisToReassign);
}