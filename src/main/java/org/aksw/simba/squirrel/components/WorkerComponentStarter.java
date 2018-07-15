package org.aksw.simba.squirrel.components;

import org.apache.commons.io.IOUtils;
import org.hobbit.core.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;

public class WorkerComponentStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerComponentStarter.class);

    /**
     * Exit code that is used if the program has to terminate because of an
     * internal error.
     */
    private static final int ERROR_EXIT_CODE = 1;

    private static Component component;

    private static boolean closed = false;

    private static FileSystemXmlApplicationContext context;

    /**
     * This is the main method creating and starting an instance of a
     * {@link Component} with the given class name.
     *
     * @param args The first element has to be the class name of the component.
     */
    public static void main(String[] args) {
        addShutdownHook();
        boolean success = true;
        try {

            context = new FileSystemXmlApplicationContext(File.separator + System.getenv("CONTEXT_CONFIG_FILE"));
            component = (Component) context.getBean("workerComponent");
            // initialize the component
            component.init();
            // run the component
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
        if (closed == false) {
            IOUtils.closeQuietly(component);
            closed = true;
            context.close();
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
