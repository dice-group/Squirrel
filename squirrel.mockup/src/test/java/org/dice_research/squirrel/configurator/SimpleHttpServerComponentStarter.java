package org.dice_research.squirrel.configurator;

import org.hobbit.core.run.ComponentStarter;
import org.junit.Ignore;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

@Ignore
public class SimpleHttpServerComponentStarter {

    public static final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    public static void main(String[] args) {
        environmentVariables.set("RESOURCE_MODEL", "../deployment/scenarios/2/nodeA.ttl");
        environmentVariables.set("ROBOTS_TXT", "../deployment/scenarios/2/robotsA.txt");
        environmentVariables.set("RESOURCE_MODEL_LANG", "N3");
        environmentVariables.set("PORT", "8080");
        environmentVariables.set("USE_DEREF", "true");
        
        ComponentStarter.main(new String[] { "org.dice_research.squirrel.components.SimpleHttpServerComponent" });
    }
}
