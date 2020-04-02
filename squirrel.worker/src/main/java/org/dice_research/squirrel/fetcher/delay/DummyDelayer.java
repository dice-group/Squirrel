package org.dice_research.squirrel.fetcher.delay;

/**
 * A dummy instance of the {@link Delayer} interface
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class DummyDelayer implements Delayer {

    private static DummyDelayer instance = new DummyDelayer();
    
    private DummyDelayer() {
    }
    
    public static DummyDelayer get() {
        return instance;
    }

    @Override
    public void getRequestPermission() {
        // nothing to do
    }

    @Override
    public void requestFinished() {
        // nothing to do
    }
    
    @Override
    public long getDelay() {
        return 0;
    }
}
