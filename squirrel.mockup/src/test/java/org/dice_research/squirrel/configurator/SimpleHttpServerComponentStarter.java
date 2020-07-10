package org.dice_research.squirrel.configurator;

import org.hobbit.core.run.ComponentStarter;
import org.junit.Ignore;

import com.github.stefanbirkner.systemlambda.SystemLambda;

@Ignore
public class SimpleHttpServerComponentStarter {

    public static void main(String[] args) throws Exception {
        SystemLambda.withEnvironmentVariable("RESOURCE_MODEL", "../deployment/scenarios/2/nodeA.ttl")
            .and("ROBOTS_TXT", "../deployment/scenarios/2/robotsA.txt")
            .and("RESOURCE_MODEL_LANG", "N3")
            .and("PORT", "8080")
            .and("USE_DEREF", "true")
            .execute(() ->
                ComponentStarter.main(new String[] { "org.dice_research.squirrel.components.SimpleHttpServerComponent" })
            );
    }
}
