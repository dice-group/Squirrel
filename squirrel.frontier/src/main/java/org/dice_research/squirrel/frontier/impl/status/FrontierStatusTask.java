package org.dice_research.squirrel.frontier.impl.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

import org.dice_research.squirrel.frontier.impl.WorkerGuard;
import org.dice_research.squirrel.queue.UriQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontierStatusTask extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierStatusTask.class);

    private List<FrontierStatusFeature> features = new ArrayList<>();

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        builder.append("Frontier status:");
        for (FrontierStatusFeature feature : features) {
            builder.append(' ');
            builder.append(feature.name);
            builder.append('=');
            builder.append(feature.valueSupplier.get());
        }
        LOGGER.info(builder.toString());
    }

    public void addWorkerUriCount(WorkerGuard workerGuard) {
        features.add(new FrontierStatusFeature("Workers URI count", () -> Arrays.toString(workerGuard.getMapWorkerInfo()
                .entrySet().stream().mapToInt(e -> e.getValue().getUrisCrawling().size()).toArray())));
    }

    public void addQueueEmptiness(UriQueue queue) {
        features.add(new FrontierStatusFeature("Queue status", () -> queue.isEmpty() ? "empty" : "not empty"));
    }
}
