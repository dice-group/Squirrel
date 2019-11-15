package org.dice_research.squirrel.frontier;

import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * An extension of the {@link Frontier} interface adding a method for informing
 * the frontier about Workers that are not available, anymore.
 * 
 */
public interface ExtendedFrontier extends Frontier {

    /**
     * The frontier gets the information that some worker has died and he has to
     * react somehow.
     *
     * @param idOfWorker
     *            The id of the dead
     *            {@link org.dice_research.squirrel.worker.Worker}.
     * @param lstUrisToReassign
     *            A list of {@link CrawleableUri} that should have been handeled by
     *            the dead worker, but was not due to his sudden death.
     */
    void informAboutDeadWorker(String idOfWorker, List<CrawleableUri> lstUrisToReassign);
}