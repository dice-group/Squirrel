package org.aksw.simba.squirrel.postprocessing;

/**
 * A component to compute arbitrary things in a post processing style that should run concurrently.
 * Logic can be implemented within the method {@link #postprocess()}.
 */
public interface PostProcessor extends Runnable {

    @Override
    default void run() {
        postprocess();
    }

    /**
     * The method that makes the processing and is by default invoked by {@link #run()}.
     */
    void postprocess();
}
