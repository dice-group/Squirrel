package org.dice_research.squirrel.components;

import org.dice_research.squirrel.utils.Closer;
import org.hobbit.core.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;


/**
 * This is the main method creating and starting an instance of a
 * {@link Component} with the given class name.
 *
 */

public class FrontierComponentStarter {

    private static final int ERROR_EXIT_CODE = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontierComponentStarter.class);
    private static FileSystemXmlApplicationContext context;
    private static Component component;
    private static boolean closed = false;

    public static void main(String[] args) {
        addShutdownHook();
        boolean success = true;
        try {
            context = new FileSystemXmlApplicationContext(File.separator + System.getenv("FRONTIER_CONTEXT_CONFIG_FILE"));
            component = (Component) context.getBean("frontierComponent");
            component.init();

            component.run();
        } catch (Throwable t) {
            LOGGER.error("Exception while executing component. Exiting with error code.", t);
            success = false;
        } finally {
            closeComponent();
        }

        if (!success) {
            System.exit(ERROR_EXIT_CODE);
        }

    }

    private static synchronized void closeComponent() {
        if (!closed) {
            Closer.close(component, LOGGER);
            closed = true;
            Closer.close(context, LOGGER);
        }
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                closeComponent();
            }
        });
    }

}
