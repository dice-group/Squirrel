package org.dice_research.squirrel.fetcher.delay;

import org.junit.Assert;
import org.junit.Test;

public class StaticDelayerTest {

    @Test
    public void test() throws InterruptedException {
        long delay = 200;

        Delayer delayer = new StaticDelayer(delay);
        delayer.getRequestPermission();
        long timeRequest1 = System.currentTimeMillis();
        delayer.requestFinished();

        delayer.getRequestPermission();
        long timeRequest2 = System.currentTimeMillis();
        Assert.assertTrue("Delayer was not delaying the execution as expected.", timeRequest1 + delay <= timeRequest2);
        timeRequest1 = timeRequest2;
        delayer.requestFinished();

        // wait some time (< delay)
        Thread.sleep(50);

        delayer.getRequestPermission();
        timeRequest2 = System.currentTimeMillis();
        Assert.assertTrue("Delayer was not delaying the execution as expected.", timeRequest1 + delay <= timeRequest2);
        timeRequest1 = timeRequest2;
        delayer.requestFinished();

        // wait some time (> delay)
        int longDelay = 400;
        Thread.sleep(longDelay);

        delayer.getRequestPermission();
        timeRequest2 = System.currentTimeMillis();
        // Make sure that the delayer is not adding the 200ms delay since it is not
        // necessary after the 400ms delay
        Assert.assertTrue("Delayer was delaying the execution more than expected.",
                (timeRequest1 + longDelay + delay) > timeRequest2);
        delayer.requestFinished();

    }
}
