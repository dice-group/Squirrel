package org.dice_research.squirrel.fetcher.delay;

/**
 * A simple implementation of the Delayer interface using a static delay between
 * requests.
 * 
 * <p>
 * Note that this implementation is <b>not thread-safe</b>!
 * </p>
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class StaticDelayer implements Delayer {

    /**
     * The delay (in ms) that should be introduced between two requests.
     */
    private long delay;
    /**
     * The time stamp (in ms) at which the last request has been finished.
     */
    private long lastRequestTimeStamp;

    /**
     * Constructor.
     * 
     * @param delay the delay (in ms) that should be introduced between two requests
     */
    public StaticDelayer(long delay) {
        this(delay, 0);
    }

    /**
     * Constructor.
     * 
     * @param delay                the delay (in ms) that should be introduced
     *                             between two requests
     * @param lastRequestTimeStamp the time stamp (in ms) at which the last request
     *                             has been finished
     */
    public StaticDelayer(long delay, long lastRequestTimeStamp) {
        this.delay = delay;
        this.lastRequestTimeStamp = lastRequestTimeStamp;
    }

    @Override
    public void getRequestPermission() throws InterruptedException {
        long sleep = (lastRequestTimeStamp + delay) - System.currentTimeMillis();
        if(sleep > 0) {
            Thread.sleep(delay);
        }
    }

    @Override
    public void requestFinished() {
        lastRequestTimeStamp = System.currentTimeMillis();
    }
    
    @Override
    public long getDelay() {
        return delay;
    }

}
