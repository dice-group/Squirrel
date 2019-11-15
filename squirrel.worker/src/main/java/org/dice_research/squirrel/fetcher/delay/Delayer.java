package org.dice_research.squirrel.fetcher.delay;

/**
 * A delayer can be used to make sure that a server is flooded with requests,
 * i.e., a fetcher is aware of doing delays between single requests without
 * programming the delay functionality in all fetcher instances.
 * 
 * <p>
 * The submission of the request and receiving the response should be
 * encapsulated by the two method calls of the {@link Delayer} instance
 * 
 * <pre>
 * Delayer d;
 * d.getRequestPermission();
 * try {
 *     // perform request
 * } finally {
 *     d.requestFinished();
 * }
 * </pre>
 * </p>
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface Delayer {

    /**
     * Waits for the permission to perform a new request.
     * 
     * @throws InterruptedException if the internal waiting is interrupted.
     */
    public void getRequestPermission() throws InterruptedException;

    /**
     * informs the Delayer instance about the finished request. Note that missing to
     * call this method might lead to a deadlock.
     */
    public void requestFinished();

    /**
     * Returns the delay (in ms) that this instance typically would introduce
     * between two consecutive requests.
     * 
     * @return the delay (in ms) that this instance typically would introduce
     *         between two consecutive requests
     */
    public long getDelay();
}
