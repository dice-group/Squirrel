package org.aksw.simba.squirrel.frontier.impl;

import org.aksw.simba.squirrel.components.FrontierComponent;
import org.aksw.simba.squirrel.components.WorkerComponent;
import org.junit.Test;

public class WorkerGuardTest {

    @Test
    public void bla() {
        try {
            WorkerComponent workerComponent = new WorkerComponent();
            workerComponent.init();
            FrontierComponent frontierComponent = new FrontierComponent();
            frontierComponent.init();
            workerComponent.run();
            frontierComponent.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
