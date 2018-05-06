package org.aksw.simba.squirrel.postprocessing;

/**
 * A component to compute arbitrary things in a post processing style that should run concurrently.
 * Logic can be implemented within the method {@link #postprocess()}.
 *
 * @param <T> The type of the data that will be returned by {@link #postprocess()}.
 */
public interface PostProcessor<T> extends Runnable {

    @Override
    default void run() {
        postprocess();
    }

    /**
     * The method that makes the processing and is by default invoked by {@link #run()}.
     *
     * @return The data that has been processed.
     */
    T postprocess();
}
